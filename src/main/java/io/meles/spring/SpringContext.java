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
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext implements TestRule, BeanFactory {

    private final Class<?>[] configClasses;
    private final List<Object> autowireTargets;
    private final Map<String, Object> beans;

    private ApplicationContext applicationContext;

    private SpringContext(final Builder builder) {
        this.configClasses = builder.configClasses.toArray(new Class[builder.configClasses.size()]);
        this.autowireTargets = unmodifiableList(new ArrayList<>(builder.autowireTargets));
        this.beans = unmodifiableMap(new HashMap<>(builder.beans));
    }

    private AnnotationConfigApplicationContext createContext() {
        final DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        for (final Entry<String, Object> bean : beans.entrySet()) {
            factory.registerSingleton(bean.getKey(), bean.getValue());
        }
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(factory);
        if (configClasses.length > 0) {
            context.register(Arrays.copyOf(configClasses, configClasses.length));
        }
        context.refresh();
        return context;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (final AnnotationConfigApplicationContext applicationContext = createContext()) {
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

        private final List<Class<?>> configClasses = new ArrayList<>();
        private final List<Object> autowireTargets = new ArrayList<>();
        private final Map<String, Object> beans = new HashMap<>();

        /**
         * Add <code>configClasses</code> to this <code>Builder</code>'s list of config <code>Class</code>es.
         *
         * @param configClasses the spring config classes to add
         * @return this Builder
         */
        public Builder config(final Class<?>... configClasses) {
            return config(asList(configClasses));
        }

        /**
         * Add <code>configClasses</code> to this <code>Builder</code>'s list of config <code>Class</code>es.
         *
         * @param configClasses the spring config classes to add
         * @return this Builder
         */
        private Builder config(final Collection<Class<?>> configClasses) {
            this.configClasses.addAll(configClasses);
            return this;
        }

        /**
         * Add the provided objects as autowire targets.
         *
         * @param targets the objects to autowire
         * @return this Builder
         */
        public Builder autowire(final Object... targets) {
            return autowire(asList(targets));
        }

        /**
         * Add the provided objects as autowire targets.
         *
         * @param targets the objects to autowire
         * @return this Builder
         */
        public Builder autowire(final Collection<Object> targets) {
            this.autowireTargets.addAll(targets);
            return this;
        }

        public Builder singleton(final String name, final Object bean) {
            this.beans.put(name, bean);
            return this;
        }

        public SpringContext build() {
            return new SpringContext(this);
        }
    }
}
