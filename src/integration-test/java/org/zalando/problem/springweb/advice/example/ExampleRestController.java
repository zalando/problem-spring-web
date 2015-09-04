package org.zalando.problem.springweb.advice.example;

/*
 * #%L
 * problem-handling
 * %%
 * Copyright (C) 2015 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api")
public class ExampleRestController {

    @RequestMapping(value = "/handler-ok", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/handler-throwable", method = RequestMethod.GET)
    public ResponseEntity<String> throwable() {
        throw new RuntimeException("expected");
    }

    @RequestMapping(value = "/handler-problem", method = RequestMethod.GET)
    public ResponseEntity<String> problem() {
        throw new ExpectedProblem();
    }

    @RequestMapping(value = "/handler-put", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@RequestBody final String body) {
        return ResponseEntity.ok(body);
    }

    @RequestMapping(value = "/noSuchRequestHandlingMethod", method = RequestMethod.GET)
    public ResponseEntity<Void> noSuchRequestHandlingMethod(final HttpServletRequest request)
            throws NoSuchRequestHandlingMethodException {
        // I have no clue how to trigger this naturally
        throw new NoSuchRequestHandlingMethodException(request);
    }

    @RequestMapping(value = "/handler-params", method = RequestMethod.GET)
    public ResponseEntity<Void> params(
            @RequestParam("params1") final String[] params1,
            @RequestParam("params2") final String[] params2) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/handler-headers", method = RequestMethod.GET)
    public ResponseEntity<Void> headers(
            @RequestHeader("X-Custom-Header") final String header1) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/handler-conversion", method = RequestMethod.GET)
    public ResponseEntity<Void> conversion(@RequestParam("dateTime") final OffsetDateTime dateTime) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/handler-multipart", method = RequestMethod.POST)
    public ResponseEntity<Void> conversion(
            @RequestPart("payload1") final MultipartFile payload1,
            @RequestPart("payload2") final MultipartFile payload2) {
        return ResponseEntity.ok().build();
    }

}
