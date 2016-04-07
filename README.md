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

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change.
For more details check the [contribution guidelines](CONTRIBUTING.md).

## Credits and references

- [Problem Details for HTTP APIs](http://tools.ietf.org/html/rfc7807)
- [Problem library](https://github.com/zalando/problem)
- [Exception Handling in Spring MVC](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#using-controlleradvice-classes)
