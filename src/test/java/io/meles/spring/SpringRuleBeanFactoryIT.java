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

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;

public class SpringRuleBeanFactoryIT {

    @Rule
    public SpringContext springContext = SpringContext.builder()
            .config(SimpleConfig.class)
            .build();

    @Test
    public void canGetBeanByName() {
        final Object asdf = springContext.getBean("stringBean");
        assertThat(asdf, is((Object) "it's a string"));
    }

    @Test
    public void canGetBeanByNameAndType() {
        final String string = springContext.getBean("stringBean", String.class);
        assertThat(string, is("it's a string"));
    }

    @Test
    public void canGetBeanByType() {
        final Integer integer = springContext.getBean(Integer.class);
        assertThat(integer, is(1));
    }

    @Test
    public void canGetBeanByNameAndOverrideParams() {
        final Object prototypeStringWithArgOverride = springContext.getBean("prototypeStringWithArg", Integer.valueOf(3));
        assertEquals("3", prototypeStringWithArgOverride);
    }

    @Test
    public void canCheckContainsBeanByName() {
        assertTrue(springContext.containsBean("stringBean"));
        assertFalse(springContext.containsBean("not_present"));
    }

    @Test
    public void canCheckSingletonScopeByName() {
        assertTrue(springContext.isSingleton("singletonString"));
        assertFalse(springContext.isSingleton("prototypeString"));
    }

    @Test
    public void canCheckPrototypeScopeByName() {
        assertFalse(springContext.isPrototype("singletonString"));
        assertTrue(springContext.isPrototype("prototypeString"));
    }

    @Test
    public void canCheckTypeMatch() {
        assertTrue(springContext.isTypeMatch("stringBean", String.class));
        assertFalse(springContext.isTypeMatch("stringBean", Integer.class));
    }

    @Test
    public void canGetBeanTypeByName() {
        final Class<?> type = springContext.getType("stringBean");
        assertEquals(String.class, type);
    }

    @Test
    public void canGetAliasesByName() {
        final String[] stringBeanAliases = springContext.getAliases("stringBean");
        assertThat(stringBeanAliases, arrayContainingInAnyOrder("stringBeanAlias"));
    }


}
