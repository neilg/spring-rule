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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContextRule implements TestRule {

    private final Class<?>[] config;
    private ApplicationContext applicationContext;

    private SpringContextRule(final Builder builder) {
        this.config = builder.configClasses.toArray(new Class[builder.configClasses.size()]);
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Class> configClasses = new ArrayList<>();

        /**
         * Replace this <code>Builder</code>'s list of config <code>Class</code>es.
         *
         * @param configClasses the spring config classes to use
         * @return this Builder
         */
        public Builder withConfig(Class... configClasses) {
            return withConfig(asList(configClasses));
        }

        /**
         * Replace this <code>Builder</code>'s list of config <code>Class</code>es.
         *
         * @param configClasses the spring config classes to use
         * @return this Builder
         */
        public Builder withConfig(Collection<Class> configClasses) {
            this.configClasses = new ArrayList<>(configClasses);
            return this;
        }

        /**
         * Add <code>configClasses</code> to this <code>Builder</code>'s list of config <code>Class</code>es.
         *
         * @param configClasses the spring config classes to add
         * @return this Builder
         */
        public Builder addConfig(Class... configClasses) {
            return addConfig(asList(configClasses));
        }

        /**
         * Add <code>configClasses</code> to this <code>Builder</code>'s list of config <code>Class</code>es.
         *
         * @param configClasses the spring config classes to add
         * @return this Builder
         */
        private Builder addConfig(Collection<Class> configClasses) {
            this.configClasses.addAll(configClasses);
            return this;
        }

        public SpringContextRule build() {
            return new SpringContextRule(this);
        }
    }
}
