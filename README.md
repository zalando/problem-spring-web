# Problems for Spring Web MVC

[![Build Status](https://img.shields.io/travis/zalando/problem-spring-web.svg)](https://travis-ci.org/zalando/problem-spring-web)
[![Coverage Status](https://img.shields.io/coveralls/zalando/problem-spring-web.svg)](https://coveralls.io/r/zalando/problem-spring-web)
[![Release](https://img.shields.io/github/release/zalando/problem-spring-web.svg)](https://github.com/zalando/problem-spring-web/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/problem-spring-web.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/problem-spring-web)

This library offers 
[`@ControllerAdvice`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)
traits, which makes use of the [*Problem* library](https://github.com/zalando/problem) to map common exceptions of your
Spring Web MVC application to an
[`application/problem+json`](https://tools.ietf.org/html/draft-nottingham-http-problem-07).

## Dependency

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>problem-spring-web</artifactId>
    <version>${problem-spring-web.version}</version>
</dependency>
```

## Usage

**Before** you start using this library, make sure you read and understood [Exception Handling in Spring MVC](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc).

What *Problem Spring Web* offers is a bunch of *Advice Traits* that you can use to create your own *Controller Advice*. Advice traits are small, reusable `@ExceptionHandler` methods that are implemented as [*default methods*](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html) and placed in single-method interfaces:

```java
public interface NotAcceptableAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleMediaTypeNotAcceptable(
            final HttpMediaTypeNotAcceptableException exception,
            final NativeWebRequest request) {
        return Responses.create(Status.NOT_ACCEPTABLE, exception, request);
    }

}
```

This allows them to be picked and combined *à la carte*:

```java
@ControllerAdvice
class ExceptionHandling implements MethodNotAllowedAdviceTrait, NotAcceptableAdviceTrait {

}
```

There are ~15 different advice traits provided by *Problem Spring Web*. They are grouped into aggregate advice traits, e.g. the `HttpAdviceTrait` includes `NotAcceptableAdviceTrait`, `UnsupportedMediaTypeAdviceTrait` and `MethodNotAllowedAdviceTrait`.

```java
public interface HttpAdviceTrait extends
        NotAcceptableAdviceTrait,
        UnsupportedMediaTypeAdviceTrait,
        MethodNotAllowedAdviceTrait {

}
```

Future versions of this library may add additional traits to those aggregates and in case you want to have all of them, just use `ProblemHandling`:

```java
@ControllerAdvice
class ExceptionHandling implements ProblemHandling {

}
```

## License

Copyright [2015] Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
