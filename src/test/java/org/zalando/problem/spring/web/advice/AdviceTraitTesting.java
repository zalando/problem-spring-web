package org.zalando.problem.spring.web.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.example.ExampleRestController;

public interface AdviceTraitTesting {

    default Object unit() {
        return new ExceptionHandling();
    }

    default MockMvc mvc() {
        final ObjectMapper mapper = mapper();

        return MockMvcBuilders.standaloneSetup(new ExampleRestController())
                .setControllerAdvice(unit())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    default ObjectMapper mapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ProblemModule());
        return mapper;
    }

}
