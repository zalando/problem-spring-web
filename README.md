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