package org.zalando.problem.spring.common;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

final class HttpStatusAdapterTest {

    @Test
    void shouldMapHttpStatusProperties() {
        final HttpStatusAdapter adapter = new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT);

        assertThat(adapter.getStatusCode(), is(418));
        assertThat(adapter.getReasonPhrase(), is(HttpStatus.I_AM_A_TEAPOT.getReasonPhrase()));
    }

    @Test
    void shouldUseHttpStatusEqualsAndHashCode() {
        final HttpStatus status = HttpStatus.I_AM_A_TEAPOT;
        final HttpStatusAdapter adapter = new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT);

        assertThat(adapter, is(adapter));
        assertThat(adapter, is(new HttpStatusAdapter(status)));
        assertThat(adapter, not(new HttpStatusAdapter(HttpStatus.BAD_GATEWAY)));
        assertThat(adapter, not(HttpStatus.I_AM_A_TEAPOT));
        assertThat(adapter.hashCode(), is(new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT).hashCode()));
    }
}
