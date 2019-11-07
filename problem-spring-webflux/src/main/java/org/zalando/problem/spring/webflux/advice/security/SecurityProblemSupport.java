package org.zalando.problem.spring.webflux.advice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.utils.AdviceUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Component
public class SecurityProblemSupport implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private final ObjectMapper mapper;

    private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache =
            new LinkedHashMap<>(64);

    @Autowired
    public SecurityProblemSupport(final ApplicationContext ctx, final ObjectMapper mapper) {
        this.mapper = mapper;
        initControllerAdviceCache(ctx);
    }

    @Override
    public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException e) {
        return resolveException(exchange, e);
    }

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final AccessDeniedException e) {
        return resolveException(exchange, e);
    }

    private void initControllerAdviceCache(ApplicationContext applicationContext) {
        List<ControllerAdviceBean> beans = ControllerAdviceBean.findAnnotatedBeans(applicationContext);
        AnnotationAwareOrderComparator.sort(beans);

        for (ControllerAdviceBean bean : beans) {
            Class<?> beanType = bean.getBeanType();
            if (beanType != null) {
                ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
                if (resolver.hasExceptionMappings()) {
                    this.exceptionHandlerAdviceCache.put(bean, resolver);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Mono<Void> resolveException(ServerWebExchange exchange, Exception e) {
        Object targetBean = null;
        Method targetMethod = null;
        for (ControllerAdviceBean advice : this.exceptionHandlerAdviceCache.keySet()) {
            targetBean = advice.resolveBean();
            targetMethod = this.exceptionHandlerAdviceCache.get(advice).resolveMethodByThrowable(e);
            if (targetMethod != null) {
                break;
            }
        }
        if (targetMethod == null) {
            return null;
        }

        try {
            ReflectionUtils.makeAccessible(targetMethod);
            Mono<ResponseEntity<Problem>> o = (Mono<ResponseEntity<Problem>>) targetMethod.invoke(targetBean, e, exchange);
            return o.flatMap(entity -> AdviceUtils.setHttpResponse(entity, exchange, mapper));
        } catch (InvocationTargetException ex) {
            return Mono.error(ex.getTargetException());
        } catch (Throwable ex) {
            return Mono.error(new IllegalStateException(ex));
        }
    }

}
