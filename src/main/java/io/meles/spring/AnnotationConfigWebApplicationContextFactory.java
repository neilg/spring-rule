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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class AnnotationConfigWebApplicationContextFactory implements ApplicationContextFactory<AnnotationConfigWebApplicationContext> {
    private final Class<?>[] configClasses;
    private final Map<String, Object> beans;

    public AnnotationConfigWebApplicationContextFactory(final Class<?>[] configClasses, final Map<String, Object> beans) {
        this.configClasses = copy(configClasses);
        this.beans = Collections.unmodifiableMap(new HashMap<>(beans));
    }

    @Override
    public AnnotationConfigWebApplicationContext newInstance() {
        final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext() {
            @Override
            protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) {
                registerSingletons(beanFactory);
                super.loadBeanDefinitions(beanFactory);
            }
        };

        try {
            registerConfig(applicationContext);
        } catch (RuntimeException e) {
            applicationContext.close();
            throw e;
        }

        return applicationContext;
    }

    private DefaultListableBeanFactory registerSingletons(final DefaultListableBeanFactory factory) {
        for (final Entry<String, Object> bean : beans.entrySet()) {
            factory.registerSingleton(bean.getKey(), bean.getValue());
        }
        return factory;
    }

    private void registerConfig(final AnnotationConfigWebApplicationContext applicationContext) {
        if (configClasses.length > 0) {
            applicationContext.register(copy(configClasses));
        }
        applicationContext.refresh();
    }

    private static <T> T[] copy(T[] array) {
        return Arrays.copyOf(array, array.length);
    }
}
