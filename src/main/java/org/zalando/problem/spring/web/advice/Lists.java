package org.zalando.problem.spring.web.advice;

/*
 * #%L
 * Problem: Spring Web
 * %%
 * Copyright (C) 2015 - 2016 Zalando SE
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

import java.util.List;

final class Lists {

    Lists() {
        // package private so we can trick code coverage
    }

    /**
     * Returns the length of the longest trailing partial sublist of the
     * target list within the specified source list, or 0 if there is no such
     * occurrence.  More formally, returns the length <tt>i</tt>
     * such that
     * {@code source.subList(source.size() - i, source.size()).equals(target.subList(target.size() - i, target.size()))},
     * or 0 if there is no such index.
     *
     * @param source the list in which to search for the longest trailing partial sublist
     *               of <tt>target</tt>.
     * @param target the list to search for as a trailing partial sublist of <tt>source</tt>.
     * @return the length of the last occurrence of trailing partial sublist the specified
     * target list within the specified source list, or 0 if there is no such occurrence.
     * @since 1.4
     */
    public static int lengthOfTrailingPartialSubList(final List<?> source, final List<?> target) {
        final int s = source.size() - 1;
        final int t = target.size() - 1;
        int l = 0;

        while (l <= s && l <= t && source.get(s - l).equals(target.get(t - l))) {
            l++;
        }

        return l;
    }

}
