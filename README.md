# Problems for Spring Web MVC

[![Build Status](https://img.shields.io/travis/zalando/problem-handling.svg)](https://travis-ci.org/zalando/problem-handling)
[![Coverage Status](https://img.shields.io/coveralls/zalando/problem-handling.svg)](https://coveralls.io/r/zalando/problem-handling)

This library offers 
[`@ControllerAdvice`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)
traits, which makes use of the [*Problem* library](https://github.com/zalando/problem) to map common exceptions of your
Spring Web MVC application to an
[`application/problem+json`](https://tools.ietf.org/html/draft-nottingham-http-problem-07).