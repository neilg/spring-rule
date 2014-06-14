package io.meles.spring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class SpringContextRuleIT {

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

}
