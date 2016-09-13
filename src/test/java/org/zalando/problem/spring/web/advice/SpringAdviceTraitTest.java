package org.zalando.problem.spring.web.advice;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;

public final class SpringAdviceTraitTest {

    private final SpringAdviceTrait unit = new SpringAdviceTrait() {
    };

    @Test
    public void buildsOnThrowable() {
        final HttpStatusAdapter adapter = new HttpStatusAdapter(RESET_CONTENT);

        final ResponseEntity<Problem> result = unit.create(HttpStatus.RESET_CONTENT,
                new IllegalStateException("Message"), mock(NativeWebRequest.class));

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(adapter)))
                .and(hasFeature("Detail", Problem::getDetail, is("Message"))));
    }

    @Test
    public void toProblemWithoutCause() {
        final ThrowableProblem problem = unit.toProblem(new IllegalStateException("Message"), BAD_REQUEST);

        assertThat(problem.getCause(), nullValue());
        assertThat(problem.getMessage(),
                allOf(containsString(BAD_REQUEST.getReasonPhrase()), containsString("Message")));
    }
}
