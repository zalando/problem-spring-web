package org.zalando.problem.spring.web.advice;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

final class AdviceTraitLoggingTest {

    private final AdviceTrait unit = spy(AdviceTrait.class);

    @Test
    void shouldLogOnCreate() {
        Throwable throwable = new NoHandlerFoundException("GET", "/", new HttpHeaders());
        NativeWebRequest request = mock(NativeWebRequest.class);
        ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);

        unit.create(Status.BAD_REQUEST, throwable, request);

        verify(unit).log(eq(throwable), problemCaptor.capture(), eq(request), eq(HttpStatus.BAD_REQUEST));
        assertThat(problemCaptor.getValue().getStatus(), is(Status.BAD_REQUEST));
    }

}
