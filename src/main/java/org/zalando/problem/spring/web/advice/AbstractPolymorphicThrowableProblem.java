package org.zalando.problem.spring.web.advice;

import org.zalando.problem.DefaultDeserializingProblem;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = ConstraintViolationProblem.class, name = ConstraintViolationProblem.TYPE_VALUE),
    @Type(value = DefaultDeserializingProblem.class, name = "about:blank")
    })
public abstract class AbstractPolymorphicThrowableProblem {
}
