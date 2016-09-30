package org.zalando.problem;

import org.junit.Test;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ConstantConditions")
public class DefaultDeserializingProblemTest {

    private final URI type = URI.create("https://example.org/out-of-stock");

    @Test
    public void shouldDefaultToAboutBlank() {
        final DefaultDeserializingProblem problem = new DefaultDeserializingProblem(null, null, null, null, null, null, null);
        assertThat(problem.getType(), hasToString("about:blank"));
    }

    @Test
    public void shouldImplementProblem() {
        final DefaultDeserializingProblem problem = new DefaultDeserializingProblem(type, "Out of Stock", BAD_REQUEST,
                "Item B00027Y5QG is no longer available",
                URI.create("https://example.org/e7203fd2-463b-11e5-a823-10ddb1ee7671"),
                null, null);

        problem.set("foo", "bar");

        assertThat(problem, hasFeature("type", Problem::getType, equalTo(type)));
        assertThat(problem, hasFeature("title", Problem::getTitle, equalTo("Out of Stock")));
        assertThat(problem, hasFeature("status", Problem::getStatus, equalTo(BAD_REQUEST)));
        assertThat(problem, hasFeature("detail", Problem::getDetail,
                is("Item B00027Y5QG is no longer available")));
        assertThat(problem, hasFeature("instance", Problem::getInstance,
                hasToString("https://example.org/e7203fd2-463b-11e5-a823-10ddb1ee7671")));
        assertThat(problem, hasFeature(DefaultDeserializingProblem::getParameters, hasEntry("foo", "bar")));
    }

}
