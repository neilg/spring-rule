package io.meles.spring;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContextRule implements TestRule {

    private final Class<?> config;
    private ApplicationContext applicationContext;

    public SpringContextRule(final Class<?> config) {
        this.config = config;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(config)) {
                    SpringContextRule.this.applicationContext = applicationContext;
                    base.evaluate();
                }
            }
        };
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
