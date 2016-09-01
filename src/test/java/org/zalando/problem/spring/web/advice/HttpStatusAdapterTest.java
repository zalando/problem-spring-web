package org.zalando.problem.spring.web.advice;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static javax.ws.rs.core.Response.Status.Family;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public final class HttpStatusAdapterTest {

    @Test
    public void shouldMapHttpStatusProperties() {
        HttpStatusAdapter adapter = new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT);

        assertThat(adapter.getStatusCode(), is(418));
        assertThat(adapter.getFamily(), is(Family.CLIENT_ERROR));
        assertThat(adapter.getReasonPhrase(), is(HttpStatus.I_AM_A_TEAPOT.getReasonPhrase()));
    }

    @Test
    public void shouldUseHttpStatusEqualsAndHashCode() {
        HttpStatus status = HttpStatus.I_AM_A_TEAPOT;
        HttpStatusAdapter adapter = new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT);

        assertThat(adapter, is(adapter));
        assertThat(adapter, is(new HttpStatusAdapter(status)));
        assertThat(adapter, not(new HttpStatusAdapter(HttpStatus.BAD_GATEWAY)));
        assertThat(adapter, not(HttpStatus.I_AM_A_TEAPOT));
        assertThat(adapter.hashCode(), is(new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT).hashCode()));
    }
}
