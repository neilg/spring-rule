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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SpringRuleMockingIT demonstrates a way of using mock objects with a SpringContext.
 */
public class SpringRuleMockingIT {

    @Rule
    public SpringContext springContext = SpringContext.builder()
            .config(SimpleConfig.class, BarConfig.class)
            .singleton("fooRepository", mock(FooRepository.class))
            .autowire(this)
            .build();

    @Autowired
    private FooRepository mockFooRepository;
    @Autowired
    private BarService barService;

    private Foo loadedFoo;
    private Foo savedFoo;

    @Before
    public void setupMocks() {
        // specify the behaviour of the mocks
        loadedFoo = new Foo();
        savedFoo = new Foo();
        when(mockFooRepository.load("123asdf")).thenReturn(loadedFoo);
        when(mockFooRepository.save(any(Foo.class))).thenReturn(savedFoo);
    }

    @Test
    public void canUseMocks() {
        assertThat(barService.findMeAFoo("123asdf"), is(loadedFoo));
    }

}
