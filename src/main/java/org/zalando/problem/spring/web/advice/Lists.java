package org.zalando.problem.spring.web.advice;

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
