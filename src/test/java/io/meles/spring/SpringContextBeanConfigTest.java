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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SpringContextBeanConfigTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SpringContext springContext;

    @Before
    public void createRules() {
        springContext = SpringContext.context()
                .config(SimpleConfig.class)
                .config(ExtraConfig.class)
                .build();
    }

    @Test
    public void canBuildASpringContextWithNoConfig() throws Throwable {
        final Statement statement = mock(Statement.class);
        SpringContext.context()
                .build()
                .apply(statement, Description.EMPTY)
                .evaluate();
    }

    @Test
    public void canRetrieveBeanFromContext() throws Throwable {
        final Object[] holder = new Object[1];
        springContext.apply(new Statement() {
            @Override
            public void evaluate() {
                holder[0] = springContext.getApplicationContext().getBean("stringBean");
            }
        }, Description.EMPTY).evaluate();
        assertEquals("it's a string", holder[0]);
    }

    @Test
    public void canGetBeanFromAddedConfig() throws Throwable {
        final Object[] holder = new Object[1];
        springContext.apply(new Statement() {
            @Override
            public void evaluate() {
                holder[0] = springContext.getApplicationContext().getBean("extraString");
            }
        }, Description.EMPTY).evaluate();
        assertEquals("the extra string", holder[0]);
    }

}
