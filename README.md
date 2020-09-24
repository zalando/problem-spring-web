# Problems for Spring MVC and Spring WebFlux

[![Stability: Sustained](https://masterminds.github.io/stability/sustained.svg)](https://masterminds.github.io/stability/sustained.html)
![Build Status](https://github.com/zalando/problem-spring-web/workflows/build/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/problem-spring-web/main.svg)](https://coveralls.io/r/zalando/problem-spring-web)
[![Code Quality](https://img.shields.io/codacy/grade/0236149bf46749b1a582f9fbbde2a4eb/main.svg)](https://www.codacy.com/app/whiskeysierra/problem-spring-web)
[![Release](https://img.shields.io/github/release/zalando/problem-spring-web.svg)](https://github.com/zalando/problem-spring-web/releases)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/problem-spring-web/main/LICENSE)

*Problem Spring Web* is a set of libraries that makes it easy to produce
[`application/problem+json`](http://tools.ietf.org/html/rfc7807) responses from a Spring
application. It fills a niche, in that it connects the [Problem library](https://github.com/zalando/problem) and either 
[Spring Web MVC's exception handling](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)
or [Spring WebFlux's exception handling](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-controller-exceptions)
so that they work seamlessly together, while requiring minimal additional developer effort. In doing so, it aims to
perform a small but repetitive task — once and for all.

The way this library works is based on what we call *advice traits*. An advice trait is a small, reusable
`@ExceptionHandler` implemented as a [default method](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)
placed in a single method interface. Those advice traits can be combined freely and don't require to use a common base
class for your [`@ControllerAdvice`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html).

:mag_right: Please check out [Baeldung: A Guide to the Problem Spring Web Library](https://www.baeldung.com/problem-spring-web) for a detailed introduction!

## Features

- lets you choose traits *à la carte*
- favors composition over inheritance
- ~20 useful advice traits built in
- Spring MVC and Spring WebFlux support
- Spring Security support
- customizable processing

## Dependencies

- Java 8
- Any build tool using Maven Central, or direct download
- Servlet Container for [problem-spring-web](problem-spring-web) or
- Reactive, non-blocking runtime for [problem-spring-webflux](problem-spring-webflux)
- Spring 5
  - Spring 4 (or Spring Boot 1.5) users may use version [0.23.0](https://github.com/zalando/problem-spring-web/releases/tag/0.23.0)
- Spring Security 5 (optional)
- Failsafe 2.3.3 (optional)

## Installation and Configuration

- [Spring Web MVC](problem-spring-web)
- [Spring WebFlux](problem-spring-webflux)

## Customization

The problem handling process provided by `AdviceTrait` is built in a way that allows for customization whenever the
need arises. All of the following aspects (and more) can be customized by implementing the appropriate advice trait interface:

| Aspect              | Method(s)                   | Default                                                                                               |
|---------------------|-----------------------------|-------------------------------------------------------------------------------------------------------|
| Creation            | `AdviceTrait.create(..)`    |                                                                                                       |
| Logging             | `AdviceTrait.log(..)`       | 4xx as `WARN`, 5xx as `ERROR` including stack trace                                                   |
| Content Negotiation | `AdviceTrait.negotiate(..)` | `application/json`, `application/*+json`, `application/problem+json` and `application/x.problem+json` |
| Fallback            | `AdviceTrait.fallback(..)`  | `application/problem+json`                                                                            |
| Post-Processing     | `AdviceTrait.process(..)`   | n/a                                                                                                   |

The following example customizes the `MissingServletRequestParameterAdviceTrait` by adding a `parameter` extension field to the `Problem`:

```java
@ControllerAdvice
public class MissingRequestParameterExceptionHandler implements MissingServletRequestParameterAdviceTrait {
    @Override
    public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {
        var exception = (MissingServletRequestParameterException) throwable;
        return Problem.builder()
                      .withTitle(status.getReasonPhrase())
                      .withStatus(status)
                      .withDetail(exception.getMessage())
                      .with("parameter", exception.getParameterName());
    }
}
```

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

**Before you continue**, please read the section about 
[*Stack traces and causal chains*](https://github.com/zalando/problem#stack-traces-and-causal-chains) 
in [zalando/problem](https://github.com/zalando/problem).

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
more details, check the [contribution guidelines](.github/CONTRIBUTING.md).

## Credits and references

- [Baeldung: A Guide to the Problem Spring Web Library](https://www.baeldung.com/problem-spring-web)
- [Problem Details for HTTP APIs](http://tools.ietf.org/html/rfc7807)
- [Problem library](https://github.com/zalando/problem)
- [Exception Handling in Spring MVC](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)
