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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

public abstract class AbstractAnnotationConfigApplicationContextBuilder<T extends ApplicationContext>
        implements SpringContextBuilder<T> {

    private final List<Class<?>> configClasses = new ArrayList<>();
    private final List<Object> autowireTargets = new ArrayList<>();
    private final Map<String, Object> beans = new HashMap<>();

    /**
     * Add <code>configClasses</code> to this <code>Builder</code>'s list of config <code>Class</code>es.
     *
     * @param configClasses the spring config classes to add
     * @return this Builder
     */
    public AbstractAnnotationConfigApplicationContextBuilder<T> config(final Class<?>... configClasses) {
        return config(asList(configClasses));
    }

    /**
     * Add <code>configClasses</code> to this <code>Builder</code>'s list of config <code>Class</code>es.
     *
     * @param configClasses the spring config classes to add
     * @return this Builder
     */
    public AbstractAnnotationConfigApplicationContextBuilder<T> config(final Collection<Class<?>> configClasses) {
        this.configClasses.addAll(configClasses);
        return this;
    }

    /**
     * Add the provided objects as autowire targets.
     *
     * @param targets the objects to autowire
     * @return this Builder
     */
    public AbstractAnnotationConfigApplicationContextBuilder<T> autowire(final Object... targets) {
        return autowire(asList(targets));
    }

    /**
     * Add the provided objects as autowire targets.
     *
     * @param targets the objects to autowire
     * @return this Builder
     */
    public AbstractAnnotationConfigApplicationContextBuilder<T> autowire(final Collection<Object> targets) {
        this.autowireTargets.addAll(targets);
        return this;
    }

    public AbstractAnnotationConfigApplicationContextBuilder<T> singleton(final String name, final Object bean) {
        this.beans.put(name, bean);
        return this;
    }

    protected Class<?>[] configClasses() {
        return configClasses.toArray(new Class[configClasses.size()]);
    }

    protected Map<String, Object> beans() {
        return unmodifiableMap(new HashMap<>(beans));
    }

    protected List<Object> autowireTargets() {
        return unmodifiableList(new ArrayList<>(this.autowireTargets));
    }

}
