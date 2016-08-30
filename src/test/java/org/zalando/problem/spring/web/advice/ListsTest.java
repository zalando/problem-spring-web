package org.zalando.problem.spring.web.advice;

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
