package org.zalando.problem.spring.common;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.zalando.problem.spring.common.Lists.lengthOfTrailingPartialSubList;

final class ListsTest {

    @Test
    void shouldFindCompleteMatch() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), asList("a", "b", "c"));

        assertThat(length, is(equalTo(3)));
    }

    @Test
    void shouldFindPartialMatch() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), asList("e", "d", "c"));

        assertThat(length, is(equalTo(1)));
    }

    @Test
    void shouldFindNoMatch() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), asList("d", "e", "f"));

        assertThat(length, is(equalTo(0)));
    }

    @Test
    void shouldFindNoMatchInEmptySource() {
        final int length = lengthOfTrailingPartialSubList(
                emptyList(), asList("a", "b", "c"));

        assertThat(length, is(equalTo(0)));
    }

    @Test
    void shouldFindNoMatchInEmptyTarget() {
        final int length = lengthOfTrailingPartialSubList(
                asList("a", "b", "c"), emptyList());

        assertThat(length, is(equalTo(0)));
    }

}
