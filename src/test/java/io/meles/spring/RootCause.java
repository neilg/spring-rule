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

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class RootCause<T extends Throwable> extends TypeSafeDiagnosingMatcher<T> {

    private final Matcher<? super Throwable> throwableMatcher;

    public RootCause(final Matcher<? super Throwable> throwableMatcher) {
        this.throwableMatcher = throwableMatcher;
    }

    public static <T extends Throwable> Matcher<T> rootCause(final Matcher<? super Throwable> throwableMatcher) {
        return new RootCause<>(throwableMatcher);
    }

    @Override
    protected boolean matchesSafely(Throwable item, org.hamcrest.Description mismatchDescription) {
        Throwable cause = item;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        final boolean matches = throwableMatcher.matches(cause);
        if (!matches) {
            mismatchDescription.appendText("root cause ");
            throwableMatcher.describeMismatch(cause, mismatchDescription);
        }
        return matches;
    }

    @Override
    public void describeTo(org.hamcrest.Description description) {
        description.appendText("throwable with root cause ").appendDescriptionOf(throwableMatcher);
    }
}
