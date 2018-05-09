# Problems for Spring Web MVC

[![Build Status](https://img.shields.io/travis/zalando/problem-spring-web/master.svg)](https://travis-ci.org/zalando/problem-spring-web)
[![Coverage Status](https://img.shields.io/coveralls/zalando/problem-spring-web/master.svg)](https://coveralls.io/r/zalando/problem-spring-web)
[![Code Quality](https://img.shields.io/codacy/grade/0236149bf46749b1a582f9fbbde2a4eb/master.svg)](https://www.codacy.com/app/whiskeysierra/problem-spring-web)
[![Javadoc](http://javadoc.io/badge/org.zalando/problem-spring-web.svg)](http://www.javadoc.io/doc/org.zalando/problem-spring-web)
[![Release](https://img.shields.io/github/release/zalando/problem-spring-web.svg)](https://github.com/zalando/problem-spring-web/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/problem-spring-web.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/problem-spring-web)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/problem-spring-web/master/LICENSE)

*Problem Spring Web* is a library that makes it easy to produce
[`application/problem+json`](http://tools.ietf.org/html/rfc7807) responses from a Spring
application. It fills a niche, in that it connects the [Problem library](https://github.com/zalando/problem) and
[Spring Web MVC's exception handling](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)
so that they work seamlessly together, while requiring minimal additional developer effort. In doing so, it aims to
perform a small but repetitive task — once and for all.

The way this library works is based on what we call *advice traits*. An advice trait is a small, reusable
`@ExceptionHandler` implemented as a [default method](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)
placed in a single method interface. Those advice traits can be combined freely and don't require to use a common base
class for your [`@ControllerAdvice`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html).

## Features

- lets you choose traits *à la carte*
- favors composition over inheritance
- ~20 useful advice traits built in
- Spring Security support
- customizable processing

## Dependencies

- Java 8
- Any build tool using Maven Central, or direct download
- Servlet Container
- Spring
- Spring Security

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
public ObjectMapper objectMapper() {
    return new ObjectMapper()
            .registerModule(new ProblemModule())
            .registerModule(new ConstraintViolationProblemModule());
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
@Import(SecurityProblemSupport.class)
@Configuration
public class SecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private SecurityProblemSupport problemSupport;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
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

### Customization

The problem handling process provided by `AdviceTrait` is built in a way that it allows for customization whenever the
need arises. All of the following aspects can be overridden and tweaked:

| Aspect              | Method(s)                   | Default                                                                                               |
|---------------------|-----------------------------|-------------------------------------------------------------------------------------------------------|
| Creation            | `AdviceTrait.create(..)`    |                                                                                                       |
| Logging             | `AdviceTrait.log(..)`       | 4xx as `WARN`, 5xx as `ERROR` including stack trace                                                   |
| Content Negotiation | `AdviceTrait.negotiate(..)` | `application/json`, `application/*+json`, `application/problem+json` and `application/x.problem+json` |
| Fallback            | `AdviceTrait.fallback(..)`  | `406 Not Acceptable`, without a body                                                                  |
| Post-Processing     | `AdviceTrait.process(..)`   | n/a                                                                                                   |

## Usage

Assuming there is a controller like this:

```java
@RestController
@RequestMapping("/products")
class ProductsResource {

    @RequestMapping(method = GET, value = "/{productId}", produces = APPLICATION_JSON_VALUE)
    public Product getProduct(String productId) {
        // TODO implement
        return null;
    }

    @RequestMapping(method = PUT, value = "/{productId}", consumes = APPLICATION_JSON_VALUE)
    public Product updateProduct(String productId, Product product) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

}
```

The following HTTP requests will produce the corresponding response respectively:

```http
GET /products/123 HTTP/1.1
Accept: application/xml
```

```http
HTTP/1.1 406 Not Acceptable
Content-Type: application/problem+json

{
  "title": "Not Acceptable",
  "status": 406,
  "detail": "Could not find acceptable representation"
}
```

```http
POST /products/123 HTTP/1.1
Content-Type: application/json

{}
```

```http
HTTP/1.1 405 Method Not Allowed
Allow: GET
Content-Type: application/problem+json

{
  "title": "Method Not Allowed",
  "status": 405,
  "detail": "POST not supported"
}
```

### Stack traces and causal chains

**Before you continue**, please read the section about [*Stack traces and causal chains*]
(https://github.com/zalando/problem#stack-traces-and-causal-chains) in [zalando/problem]
(https://github.com/zalando/problem).

In case you want to enable stack traces, please configure your `ProblemModule` as follows:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new ProblemModule().withStackTraces());
```

Causal chains of problems are **disabled by default**, but can be overridden if desired:

```java
@ControllerAdvice
class ExceptionHandling implements ProblemHandling {

    @Override
    public boolean isCausalChainsEnabled() {
        return true;
    }

}
```

**Note** Since you have full access to the application context at that point, you can externalize the
configuration to your `application.yml` and even decide to reuse Spring's `server.error.include-stacktrace` property.

Enabling both features, causal chains and stacktraces, will yield:

```yaml
{
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Illegal State",
  "stacktrace": [
    "org.example.ExampleRestController.newIllegalState(ExampleRestController.java:96)",
    "org.example.ExampleRestController.nestedThrowable(ExampleRestController.java:91)"
  ],
  "cause": {
    "title": "Internal Server Error",
    "status": 500,
    "detail": "Illegal Argument",
    "stacktrace": [
      "org.example.ExampleRestController.newIllegalArgument(ExampleRestController.java:100)",
      "org.example.ExampleRestController.nestedThrowable(ExampleRestController.java:88)"
    ],
    "cause": {
      "title": "Internal Server Error",
      "status": 500,
      "detail": "Null Pointer",
      "stacktrace": [
        "org.example.ExampleRestController.newNullPointer(ExampleRestController.java:104)",
        "org.example.ExampleRestController.nestedThrowable(ExampleRestController.java:86)",
        "sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
        "sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)",
        "sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)",
        "java.lang.reflect.Method.invoke(Method.java:483)",
        "org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)",
        "org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)",
        "org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)",
        "org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)",
        "org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)",
        "org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)",
        "org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)",
        "org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)",
        "org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)",
        "org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)",
        "org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)",
        "org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)",
        "org.junit.runners.ParentRunner.run(ParentRunner.java:363)",
        "org.junit.runner.JUnitCore.run(JUnitCore.java:137)",
        "com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:117)",
        "com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:234)",
        "com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:74)"
      ]
    }
  }
}
```

## Known Issues

Spring allows to restrict the scope of a `@ControllerAdvice` to a certain subset of controllers:

```java

@ControllerAdvice(assignableTypes = ExampleController.class)
public final class ExceptionHandling implements ProblemHandling
```

By doing this you'll loose the capability to handle certain types of exceptions namely:
- `HttpRequestMethodNotSupportedException`
- `HttpMediaTypeNotAcceptableException`
- `HttpMediaTypeNotSupportedException`
- `NoHandlerFoundException`

We inherit this restriction from Spring and therefore recommend to use an unrestricted `@ControllerAdvice`.

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](CONTRIBUTING.md).

## Credits and references

- [Problem Details for HTTP APIs](http://tools.ietf.org/html/rfc7807)
- [Problem library](https://github.com/zalando/problem)
- [Exception Handling in Spring MVC](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)
