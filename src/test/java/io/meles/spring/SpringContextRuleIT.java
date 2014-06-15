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

public class SpringContextRuleIT {

    @Rule
    public SpringContextRule springContextRule = SpringContextRule.builder()
            .withConfig(SimpleConfig.class)
            .build();

    @Test
    public void canRetrieveBeanFromContext() {
        assertThat((String) springContextRule.getApplicationContext().getBean("stringBean"), is("it's a string"));
    }

    @Test
    public void canGetBeanByName() {
        final Object asdf = springContextRule.getBean("stringBean");
        assertThat(asdf, is((Object) "it's a string"));
    }

    @Test
    public void canGetBeanByNameAndType() {
        final String string = springContextRule.getBean("stringBean", String.class);
        assertThat(string, is("it's a string"));
    }

    @Test
    public void canGetBeanByType() {
        final Integer integer = springContextRule.getBean(Integer.class);
        assertThat(integer, is(1));
    }

    @Test
    public void canGetBeanByNameAndOverrideParams() {
        final Object prototypeStringWithArgOverride = springContextRule.getBean("prototypeStringWithArg", Integer.valueOf(3));
        assertEquals("3", prototypeStringWithArgOverride);
    }

    @Test
    public void canCheckContainsBeanByName() {
        assertTrue(springContextRule.containsBean("stringBean"));
        assertFalse(springContextRule.containsBean("not_present"));
    }

    @Test
    public void canCheckSingletonScopeByName() {
        assertTrue(springContextRule.isSingleton("singletonString"));
        assertFalse(springContextRule.isSingleton("prototypeString"));
    }

    @Test
    public void canCheckPrototypeScopeByName() {
        assertFalse(springContextRule.isPrototype("singletonString"));
        assertTrue(springContextRule.isPrototype("prototypeString"));
    }

    @Test
    public void canCheckTypeMatch() {
        assertTrue(springContextRule.isTypeMatch("stringBean", String.class));
        assertFalse(springContextRule.isTypeMatch("stringBean", Integer.class));
    }

    @Test
    public void canGetBeanTypeByName() {
        final Class<?> type = springContextRule.getType("stringBean");
        assertEquals(String.class, type);
    }

    @Test
    public void canGetAliasesByName() {
        final String[] stringBeanAliases = springContextRule.getAliases("stringBean");
        assertThat(stringBeanAliases, arrayContainingInAnyOrder("stringBeanAlias"));
    }

}
