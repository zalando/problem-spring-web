package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.reactive.server.MockServerConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.*;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.webflux.advice.example.ExampleRestController;
import org.zalando.problem.spring.webflux.advice.http.HttpAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblemModule;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public interface AdviceTraitTesting {

    default ProblemHandling unit() {
        return new ExceptionHandling();
    }

    default MockMvc mvc() {
        final ObjectMapper mapper = mapper();

        return MockMvcBuilders
                .standaloneSetup(new ExampleRestController())
                .setContentNegotiationManager(new ContentNegotiationManager(singletonList(
                        new FixedContentNegotiationStrategy(APPLICATION_JSON))))
                .setControllerAdvice(unit())
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(mapper),
                        new MappingJackson2XmlHttpMessageConverter())
                .build();
    }

    default WebTestClient webTestClient() {
        return WebTestClient.bindToController(new ExampleRestController())
                .apply(new MockServerConfigurer() {
                    @Override
                    public void beforeServerCreated(WebHttpHandlerBuilder builder) {
                        builder.exceptionHandlers(handlers ->
                                handlers.add(0, new WebFluxResponseStatusExceptionHandler2(mapper(), unit())));
                    }
                })
                .controllerAdvice(unit())
                .httpMessageCodecs(this::configureJson)
                .configureClient()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(this::configureJson).build())
                .build();
    }

    default void configureJson(CodecConfigurer configurer) {
        final ObjectMapper mapper = mapper();
        CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
        defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
        defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
    }

    default ObjectMapper mapper() {
        return new ObjectMapper()
                .registerModule(new ProblemModule())
                .registerModule(new ConstraintViolationProblemModule());
    }

}

class WebFluxResponseStatusExceptionHandler2 extends WebFluxResponseStatusExceptionHandler {

    public final ObjectMapper mapper;

    public final HttpAdviceTrait advice;

    WebFluxResponseStatusExceptionHandler2(ObjectMapper mapper, HttpAdviceTrait advice) {
        this.mapper = mapper;
        this.advice = advice;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        Mono<ResponseEntity<Problem>> entityMono;
        if (throwable instanceof ResponseStatusException) {
            if (throwable instanceof NotAcceptableStatusException) {
                entityMono = advice.handleMediaTypeNotAcceptable((NotAcceptableStatusException) throwable, exchange);
            } else if (throwable instanceof UnsupportedMediaTypeStatusException) {
                entityMono = advice.handleMediaTypeNotSupportedException((UnsupportedMediaTypeStatusException) throwable, exchange);
            } else if (throwable instanceof MethodNotAllowedException) {
                entityMono = advice.handleRequestMethodNotSupportedException((MethodNotAllowedException) throwable, exchange);
            } else {
                entityMono = advice.handleResponseStatusException((ResponseStatusException) throwable, exchange);
            }
            return entityMono
                    .flatMap(response -> {
                        exchange.getResponse().setStatusCode(response.getStatusCode());
                        exchange.getResponse().getHeaders().addAll(response.getHeaders());
                        try {
                            return exchange.getResponse()
                                    .writeWith(Mono.just(new DefaultDataBufferFactory().wrap(mapper.writeValueAsBytes(response.getBody()))));
                        } catch (JsonProcessingException e) {
                            return Mono.error(throwable);
                        }
                    });
        }
        return Mono.error(throwable);
    }


        /*HttpStatus status = resolveStatus(ex);
        if (status != null && exchange.getResponse().setStatusCode(status)) {
            if (status.is5xxServerError()) {

                //logger.error(buildMessage(exchange.getRequest(), ex));
            }
            else if (status == HttpStatus.BAD_REQUEST) {
                //logger.warn(buildMessage(exchange.getRequest(), ex));
            }
            else {
                //logger.trace(buildMessage(exchange.getRequest(), ex));
            }
            return exchange.getResponse().setComplete();
        }
        return Mono.error(ex);
    }

    @Nullable
    private HttpStatus resolveStatus(Throwable ex) {
        HttpStatus status = determineStatus(ex);
        if (status == null) {
            Throwable cause = ex.getCause();
            if (cause != null) {
                status = resolveStatus(cause);
            }
        }
        return status;
    }*/
}
