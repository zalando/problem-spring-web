# Problem: Spring Web MVC

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>problem-spring-web</artifactId>
    <version>${problem-spring-web.version}</version>
</dependency>
```

## Configuration

Make sure you register the required modules with your ObjectMapper:

```java
@Bean
public ProblemModule problemModule() {
    return new ProblemModule();
}

@Bean
public ConstraintViolationProblemModule constraintViolationProblemModule() {
    return new ConstraintViolationProblemModule();
}
```

The following table shows all built-in advice traits:

| Advice Trait                                                                                                                                                       | Produces                                                  |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| [**`ProblemHandling`**](src/main/java/org/zalando/problem/spring/web/advice/ProblemHandling.java)                                                                  |                                                           |
| `├──`[**`GeneralAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/general/GeneralAdviceTrait.java)                                               |                                                           |
| `│   ├──`[`ProblemAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/general/ProblemAdviceTrait.java)                                               | *depends*                                                 |
| `│   ├──`[`ThrowableAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/general/ThrowableAdviceTrait.java)                                           | [`500 Internal Server Error`](https://httpstatus.es/500)  |
| `│   └──`[ `UnsupportedOperationAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/general/UnsupportedOperationAdviceTrait.java)                    | [`501 Not Implemented`](https://httpstatus.es/501)        |
| `├──`[**`HttpAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/http/HttpAdviceTrait.java)                                                        |                                                           |
| `│   ├──`[`MethodNotAllowedAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/http/MethodNotAllowedAdviceTrait.java)                                | [`405 Method Not Allowed`](https://httpstatus.es/405)     |
| `│   ├──`[`NotAcceptableAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/http/NotAcceptableAdviceTrait.java)                                      | [`406 Not Acceptable`](https://httpstatus.es/406)         |
| `│   └──`[`UnsupportedMediaTypeAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/http/UnsupportedMediaTypeAdviceTrait.java)                        | [`415 Unsupported Media Type`](https://httpstatus.es/415) |
| `├──`[**`IOAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/io/IOAdviceTrait.java)                                                              |                                                           |
| `│   ├──`[`MessageNotReadableAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/io/MessageNotReadableAdviceTrait.java)                              | [`400 Bad Request`](https://httpstatus.es/400)            |
| `│   ├──`[`MultipartAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/io/MultipartAdviceTrait.java)                                                | [`400 Bad Request`](https://httpstatus.es/400)            |
| `│   └──`[`TypeMismatchAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/io/TypeMismatchAdviceTrait.java)                                          | [`400 Bad Request`](https://httpstatus.es/400)            |
| `├──`[**`RoutingAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/routing/RoutingAdviceTrait.java)                                               |                                                           |
| `│   ├──`[`MissingServletRequestParameterAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/MissingServletRequestParameterAdviceTrait.java) | [`400 Bad Request`](https://httpstatus.es/400)            |
| `│   ├──`[`MissingServletRequestPartAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/MissingServletRequestPartAdviceTrait.java)           | [`400 Bad Request`](https://httpstatus.es/400)            |
| `│   ├──`[`NoHandlerFoundAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/NoHandlerFoundAdviceTrait.java)                                 | [`404 Not Found`](https://httpstatus.es/404)              |
| `│   ├──`[`NoSuchRequestHandlingMethodAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/NoSuchRequestHandlingMethodAdviceTrait.java)       | [`404 Not Found`](https://httpstatus.es/404)              |
| `│   └──`[`ServletRequestBindingAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/ServletRequestBindingAdviceTrait.java)                   | [`400 Bad Request`](https://httpstatus.es/400)            |
| `├──`[**`SecurityAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/security/SecurityAdviceTrait.java)                                            |                                                           |
| `│   ├──`[`AccessDeniedAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/security/AccessDeniedAdviceTrait.java)                                    | [`403 Forbidden`](https://httpstatus.es/403)              |
| `│   └──`[`AuthenticationAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/security/AuthenticationAdviceTrait.java)                                | [`401 Unauthorized`](https://httpstatus.es/401)           |
| `└──`[**`ValidationAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/validation/ValidationAdviceTrait.java)                                      |                                                           |
| `    ├──`[`ConstraintViolationAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/validation/ConstraintViolationAdviceTrait.java)                    | [`400 Bad Request`](https://httpstatus.es/400)            |
| `    └──`[`MethodArgumentNotValidAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/validation/MethodArgumentNotValidAdviceTrait.java)              | [`400 Bad Request`](https://httpstatus.es/400)            |

You're free to use them either individually or in groups. Future versions of this library may add additional traits to groups. A typical usage would look like this:

```java
@ControllerAdvice
class ExceptionHandling implements ProblemHandling {

}
```

The [`NoHandlerFoundAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/NoHandlerFoundAdviceTrait.java)
in addition also requires the following configuration:

```yaml
spring:
  resources:
    add-mappings: false
  mvc:
    throw-exception-if-no-handler-found: true
```

If you're using Spring Boot, make sure you disable the `ErrorMvcAutoConfiguration`:

```java
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
```

### Security

The Spring Security integration requires additional steps:

```java
@Configuration
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private SecurityProblemSupport problemSupport;

    @Override
    public void configure(final HttpSecurity http) {
        http.exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport);
    }

}
```

To return valid problem objects upon authentication exceptions, you will also need to implement the [`SecurityAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/security/SecurityAdviceTrait.java), this is already sufficient:

```java
@ControllerAdvice
public class SecurityExceptionHandler implements SecurityAdviceTrait {
}
```
