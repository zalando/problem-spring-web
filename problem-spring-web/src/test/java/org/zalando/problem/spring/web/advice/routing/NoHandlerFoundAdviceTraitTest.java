package org.zalando.problem.spring.web.advice.routing;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.DispatcherServlet;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class NoHandlerFoundAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void noHandlerInController() throws Exception {
        final MockMvc mvc = mvc();
        throwExceptionIfNoHandlerFound(mvc);

        mvc.perform(request(GET, "http://localhost/api/no-handler"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("No endpoint GET")));
    }

    @Test
    void noHandler() throws Exception {
        final MockMvc mvc = mvc();
        throwExceptionIfNoHandlerFound(mvc);

        mvc.perform(request(GET, "http://localhost/no-handler"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("No endpoint GET")));
    }

    private void throwExceptionIfNoHandlerFound(final MockMvc mvc) throws NoSuchFieldException, IllegalAccessException {
        final Field field = MockMvc.class.getDeclaredField("servlet");
        field.setAccessible(true);
        final DispatcherServlet servlet = (DispatcherServlet) field.get(mvc);
        servlet.setThrowExceptionIfNoHandlerFound(true);
    }

}
