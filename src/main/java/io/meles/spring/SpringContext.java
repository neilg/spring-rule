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
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext implements TestRule, BeanFactory {

    private final Class<?>[] configClasses;
    private final List<Object> autowireTargets;

    private ApplicationContext applicationContext;

    private SpringContext(final Builder builder) {
        this.configClasses = builder.configClasses.toArray(new Class[builder.configClasses.size()]);
        this.autowireTargets = unmodifiableList(new ArrayList<>(builder.autowireTargets));
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final Class<?>[] configClassesCopy = Arrays.copyOf(configClasses, configClasses.length);
                try (final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(configClassesCopy)) {
                    SpringContext.this.applicationContext = applicationContext;
                    for (final Object autowireTarget : autowireTargets) {
                        autowire(autowireTarget);
                    }

                    base.evaluate();
                } finally {
                    applicationContext = null;
                }
            }
        };
    }

    /**
     * @return the active spring application context (if there is one)
     * @throws java.lang.IllegalStateException if there is no active context
     */
    public ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("no spring application context, are you calling getApplicationContext() outside of a test execution");
        }
        return applicationContext;
    }

    // BeanFactory implementation

    @Override
    public Object getBean(String name) throws BeansException {
        return getApplicationContext().getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getApplicationContext().getBean(name, args);
    }

    @Override
    public boolean containsBean(String name) {
        return getApplicationContext().containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
        return getApplicationContext().isTypeMatch(name, targetType);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getType(name);
    }

    @Override
    public String[] getAliases(String name) {
        return getApplicationContext().getAliases(name);
    }

    // End of BeanFactory implementation

    /**
     * Perform autowiring of the provided object. Fields annotated with <code>@Autowired</code> will be set from the
     * spring context. This method can be called from a method annotated with <code>@Before</code>.
     *
     * @param object the object to autowire
     * @throws java.lang.IllegalStateException if the spring context isn't active
     */
    public void autowire(Object object) {
        getAutowireCapableBeanFactory().autowireBean(object);
    }

    private AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
        return getApplicationContext().getAutowireCapableBeanFactory();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Class> configClasses = new ArrayList<>();
        private List<Object> autowireTargets = new ArrayList<>();

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

        public Builder autowire(Object... targets) {
            this.autowireTargets.addAll(asList(targets));
            return this;
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

        public SpringContext build() {
            return new SpringContext(this);
        }
    }
}
