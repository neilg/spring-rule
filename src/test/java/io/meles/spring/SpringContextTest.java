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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringContextTest {

    @Test
    public void shouldUseApplicationContextFactoryToProvideContext() throws Throwable {
        final ApplicationContextFactory<ApplicationContext> applicationContextFactory = mock(ApplicationContextFactory.class);
        SpringContext<ApplicationContext> springContext = new SpringContext<>(applicationContextFactory, asList());

        springContext.apply(mock(Statement.class), Description.EMPTY).evaluate();

        verify(applicationContextFactory).newInstance();
    }

    @Test
    public void shouldAutowire() throws Throwable {
        final Object autowireTarget = new Object();
        final AutowireCapableBeanFactory autowireCapableBeanFactory = mock(AutowireCapableBeanFactory.class);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autowireCapableBeanFactory);
        final ApplicationContextFactory<ApplicationContext> applicationContextFactory = mock(ApplicationContextFactory.class);
        when(applicationContextFactory.newInstance()).thenReturn(applicationContext);

        SpringContext<ApplicationContext> springContext = new SpringContext<>(applicationContextFactory, asList(autowireTarget));


        springContext.apply(mock(Statement.class), Description.EMPTY).evaluate();

        verify(autowireCapableBeanFactory).autowireBean(autowireTarget);
    }

}