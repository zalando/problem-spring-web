package org.zalando.problem.spring.web.advice;

/*
 * #%L
 * Problem: Spring Web
 * %%
 * Copyright (C) 2015 - 2016 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.problem.spring.web.advice.Lists.lengthOfTrailingPartialSubList;

public final class ListsTest {

    @Test
    public void shouldFindCompleteMatch() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), asList("a", "b", "c"));

        assertThat(length, is(equalTo(3)));
    }

    @Test
    public void shouldFindPartialMatch() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), asList("e", "d", "c"));

        assertThat(length, is(equalTo(1)));
    }

    @Test
    public void shouldFindNoMatch() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), asList("d", "e", "f"));

        assertThat(length, is(equalTo(0)));
    }

    @Test
    public void shouldFindNoMatchInEmptySource() {
        final int length = lengthOfTrailingPartialSubList(
                emptyList(), asList("a", "b", "c"));

        assertThat(length, is(equalTo(0)));
    }

    @Test
    public void shouldFindNoMatchInEmptyTarget() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), emptyList());

        assertThat(length, is(equalTo(0)));
    }

}
