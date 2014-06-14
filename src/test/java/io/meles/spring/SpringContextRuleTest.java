package io.meles.spring;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class SpringContextRuleTest {

    @Configuration
    public static class SimpleConfig {
        @Bean
        public String asdf() {
            return "it's a string";
        }
    }

    private SpringContextRule springContextRule;

    @Before
    public void createRule() {
        springContextRule = new SpringContextRule(SimpleConfig.class);
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
        verify(statement).evaluate();
    }
}
