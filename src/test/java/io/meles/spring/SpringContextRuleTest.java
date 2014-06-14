package io.meles.spring;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
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

public class SpringContextRuleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SpringContextRule springContextRule;
    private SpringContextRule badSpringContextRule;

    @Before
    public void createRules() {
        springContextRule = new SpringContextRule(SimpleConfig.class);
        badSpringContextRule = new SpringContextRule(ThrowingConfig.class);
    }

    @Test
    public void canRetrieveBeanFromContext() throws Throwable {
        final Object[] holder = new Object[1];
        springContextRule.apply(new Statement() {
            @Override
            public void evaluate() {
                holder[0] = springContextRule.getApplicationContext().getBean("asdf");
            }
        }, Description.EMPTY).evaluate();
        assertEquals("it's a string", holder[0]);
    }

    @Test
    public void underlyingStatementIsEvaluated() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = springContextRule.apply(statement, Description.EMPTY);
        wrappedStatement.evaluate();
        verify(statement, times(1)).evaluate();
    }

    @Test
    public void underlyingStatementIsNotEvaluatedIfContextFailsToStart() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = badSpringContextRule.apply(statement, Description.EMPTY);
        try {
            wrappedStatement.evaluate();
        } catch (Throwable t) {
        } finally {
            verify(statement, never()).evaluate();
        }
    }

    @Test
    public void failureToStartIsPropagatedOnEvaluation() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = badSpringContextRule.apply(statement, Description.EMPTY);
        expectedException.expect(rootCause(hasMessage(equalTo("bad, bad, bad"))));
        wrappedStatement.evaluate();
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
