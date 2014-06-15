/*
 * Copyright (c) 2014 Neil Green
 *
 * This file is part of Meles Spring Rule.
 *
 * Meles Spring Rule is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Meles Spring Rule is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Meles Spring Rule.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.meles.spring;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SpringContext springContext;
    private SpringContext badSpringContext;

    @Before
    public void createRules() {
        springContext = SpringContext.builder()
                .withConfig(SimpleConfig.class)
                .addConfig(ExtraConfig.class)
                .build();
        badSpringContext = SpringContext.builder()
                .withConfig(ThrowingConfig.class)
                .build();
    }

    @Test
    public void canRetrieveBeanFromContext() throws Throwable {
        final Object[] holder = new Object[1];
        springContext.apply(new Statement() {
            @Override
            public void evaluate() {
                holder[0] = springContext.getApplicationContext().getBean("stringBean");
            }
        }, Description.EMPTY).evaluate();
        assertEquals("it's a string", holder[0]);
    }

    @Test
    public void canGetBeanFromAddedConfig() throws Throwable {
        final Object[] holder = new Object[1];
        springContext.apply(new Statement() {
            @Override
            public void evaluate() {
                holder[0] = springContext.getApplicationContext().getBean("extraString");
            }
        }, Description.EMPTY).evaluate();
        assertEquals("the extra string", holder[0]);
    }

    @Test
    public void underlyingStatementIsEvaluated() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = springContext.apply(statement, Description.EMPTY);
        wrappedStatement.evaluate();
        verify(statement, times(1)).evaluate();
    }

    @Test
    public void underlyingStatementIsNotEvaluatedIfContextFailsToStart() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = badSpringContext.apply(statement, Description.EMPTY);
        try {
            wrappedStatement.evaluate();
        } catch (Throwable ignored) {
        } finally {
            verify(statement, never()).evaluate();
        }
    }

    @Test
    public void failureToStartIsPropagatedOnEvaluation() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = badSpringContext.apply(statement, Description.EMPTY);
        expectedException.expect(rootCause(hasMessage(equalTo("bad, bad, bad"))));
        wrappedStatement.evaluate();
    }

    @Test
    public void cannotGetApplicationContextOutsideOfEvaluation() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("no spring application context, are you calling getApplicationContext() outside of a test execution");
        springContext.getApplicationContext();
    }

    @Test
    public void contextIsClosedAfterEvaluation() throws Throwable {
        final ApplicationContext[] holder = new ApplicationContext[1];
        springContext.apply(new Statement() {
            @Override
            public void evaluate() {
                holder[0] = springContext.getApplicationContext();
            }
        }, Description.EMPTY).evaluate();
        assertFalse(((ConfigurableApplicationContext) holder[0]).isActive());
        assertFalse(((ConfigurableApplicationContext) holder[0]).isRunning());
    }

    private Matcher<Throwable> rootCause(final Matcher<? super Throwable> throwableMatcher) {
        return new TypeSafeDiagnosingMatcher<Throwable>() {
            @Override
            protected boolean matchesSafely(Throwable item, org.hamcrest.Description mismatchDescription) {
                Throwable cause = item;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                final boolean matches = throwableMatcher.matches(cause);
                if (!matches) {
                    mismatchDescription.appendText("root cause ");
                    throwableMatcher.describeMismatch(cause, mismatchDescription);
                }
                return matches;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("throwable with root cause ").appendDescriptionOf(throwableMatcher);
            }
        };
    }
}
