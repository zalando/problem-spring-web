# Problems for Spring Web MVC

[![Build Status](https://img.shields.io/travis/zalando/problem-spring-web.svg)](https://travis-ci.org/zalando/problem-spring-web)
[![Coverage Status](https://img.shields.io/coveralls/zalando/problem-spring-web.svg)](https://coveralls.io/r/zalando/problem-spring-web)
[![Release](https://img.shields.io/github/release/zalando/problem-spring-web.svg)](https://github.com/zalando/problem-spring-web/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/problem-spring-web.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/problem-spring-web)

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

- let's you choose traits *à la carte*
- favors composition over inheritance
- 15+ useful advice traits built in

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
            .registerModule(new Jdk8Module())
            .registerModule(new ProblemModule());
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
| `│   └──`[`TypeMistmatchAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/io/TypeMistmatchAdviceTrait.java)                                        | [`400 Bad Request`](https://httpstatus.es/400)            |
| `├──`[**`RoutingAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/routing/RoutingAdviceTrait.java)                                               |                                                           |
| `│   ├──`[`MissingServletRequestParameterAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/MissingServletRequestParameterAdviceTrait.java) | [`400 Bad Request`](https://httpstatus.es/400)            |
| `│   ├──`[`MissingServletRequestPartAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/MissingServletRequestPartAdviceTrait.java)           | [`400 Bad Request`](https://httpstatus.es/400)            |
| `│   ├──`[`NoHandlerFoundAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/NoHandlerFoundAdviceTrait.java)                                 | [`404 Not Found`](https://httpstatus.es/404)              |
| `│   ├──`[`NoSuchRequestHandlingMethodAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/NoSuchRequestHandlingMethodAdviceTrait.java)       | [`404 Not Found`](https://httpstatus.es/404)              |
| `│   └──`[`ServletRequestBindingAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/routing/ServletRequestBindingAdviceTrait.java)                   | [`400 Bad Request`](https://httpstatus.es/400)            |
| `└──`[**`ValidationAdviceTrait`**](src/main/java/org/zalando/problem/spring/web/advice/validation/ValidationAdviceTrait.java)                                      |                                                           |
| `    ├──`[`ConstraintViolationAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/validation/ConstraintViolationAdviceTrait.java)                    | [`422 Unprocessable Entity`](https://httpstatus.es/422)   |
| `    └──`[`MethodArgumentNotValidAdviceTrait`](src/main/java/org/zalando/problem/spring/web/advice/validation/MethodArgumentNotValidAdviceTrait.java)              | [`422 Unprocessable Entity`](https://httpstatus.es/422)   |

You're free to use them individually or in groups. Future versions of this library may add additional traits to groups.
A typical usage would look like this:

```java
@ControllerAdvice
class ExceptionHandling implements ProblemHandling {

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
        return ..;
    }

    @RequestMapping(method = PUT, value = "/{productId}", consumes = APPLICATION_JSON_VALUE}
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
Content-Type: application/json

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
Content-Type: application/json

{
  "title": "Method Not Allowed",
  "status": 405,
  "detail": "POST not supported"
}
```

### Stacktraces and causal chains

**Before you continue** please read the section about [*Stacktraces and causal chains*]
(https://github.com/zalando/problem#stacktraces-and-causal-chains) in [zalando/problem]
(https://github.com/zalando/problem).

In case you want to enable stacktraces, please configure your `ProblemModule` as follows.

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new ProblemModule().withStacktraces());
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

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change.
For more details check the [contribution guidelines](CONTRIBUTING.md).

## Credits and references

- [Problem Details for HTTP APIs](http://tools.ietf.org/html/rfc7807)
- [Problem library](https://github.com/zalando/problem)
- [Exception Handling in Spring MVC](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)
