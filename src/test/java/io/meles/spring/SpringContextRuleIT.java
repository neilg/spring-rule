package io.meles.spring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

public class SpringContextRuleIT {

    @Rule
    public SpringContextRule springContextRule = new SpringContextRule(SimpleConfig.class);

    @Test
    public void canRetrieveBeanFromContext() {
        assertThat((String) springContextRule.getApplicationContext().getBean("asdf"), is("it's a string"));
    }

}
