package io.meles.spring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
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

    @Rule
    public SpringContextRule springContextRule = new SpringContextRule(SimpleConfig.class);

    @Test
    public void canRetrieveBeanFromContext() {
        assertThat((String) springContextRule.getApplicationContext().getBean("asdf"), is("it's a string"));
    }

    @Test
    public void underlyingStatementIsEvaluated() throws Throwable {
        final Statement statement = mock(Statement.class);
        final Statement wrappedStatement = springContextRule.apply(statement, Description.EMPTY);
        wrappedStatement.evaluate();
        verify(statement).evaluate();
    }
}
