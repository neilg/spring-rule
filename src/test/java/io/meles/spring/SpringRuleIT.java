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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringRuleIT {

    @Rule
    public SpringContext springContext = SpringContext.builder()
            .config(SimpleConfig.class)
            .autowire(this)
            .build();

    @Autowired
    public String stringBean;

    @Test
    public void canAutowire() {
        assertThat(stringBean, is("it's a string"));
    }

    @Test
    public void canRetrieveBeanFromContext() {
        assertThat((String) springContext.getApplicationContext().getBean("stringBean"), is("it's a string"));
    }

}
