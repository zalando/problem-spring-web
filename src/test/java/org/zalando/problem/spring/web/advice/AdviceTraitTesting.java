package org.zalando.problem.spring.web.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.example.ExampleRestController;

import java.util.Collections;

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
        return new ObjectMapper().registerModule(new ProblemModule());
    }

}
