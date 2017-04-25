package org.zalando.problem.spring.web.advice.validation;

import static org.springframework.http.HttpMethod.POST;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

public final class ConstraintViolationAdviceTraitTest implements AdviceTraitTesting {

  @Test
  public void invalidRequestParam() throws Exception {
    final MvcResult result = this.mvc()
        .perform(MockMvcRequestBuilders.request(POST, "http://localhost/api/handler-invalid-param")
            .contentType("application/json").content("{\"name\":\"Bob\"}"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.header().string("Content-Type",
            Matchers.is("application/problem+json")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.type",
            Matchers.is("https://zalando.github.io/problem/constraint-violation")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Constraint Violation")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(400)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.violations", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].field", Matchers.is(""))) // field
                                                                                             // is
                                                                                             // not
                                                                                             // set
                                                                                             // when
                                                                                             // validation
                                                                                             // manually
        .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].message",
            Matchers.is("must not be called Bob")))
        .andReturn();
    System.out.println(result.getResponse().getContentAsString());
  }

}
