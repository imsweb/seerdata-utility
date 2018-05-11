/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerdata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtils {

    /**
     * Possible search modes
     */
    public enum SearchMode {
        OR, AND
    }

    /**
     * Regex used to split the search terms
     */
    private static final Pattern _TOKEN_REGEX = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    /**
     * Splits the provided search string by spaces. Keeps the tokens together if they are enclosed in double-quotes.
     * <p/>
     * &nbsp;&nbsp;&nbsp;A B C -> [A, B, C]<br>
     * &nbsp;&nbsp;&nbsp;"A B C" -> [A B C]
     * <p/>
     * Created on Jan 2, 2011 by depryf
     * @param searchString the search string to split, can be null
     * @return the list of split tokens, maybe empty but never null
     */
    public static List<String> splitSearchString(String searchString) {
        List<String> result = new ArrayList<>();

        if (searchString == null || searchString.trim().isEmpty())
            return result;

        Matcher matcher = _TOKEN_REGEX.matcher(searchString);
        while (matcher.find()) {
            String s;
            if (matcher.group(1) != null)
                s = matcher.group(1);
            else if (matcher.group(2) != null)
                s = matcher.group(2);
            else
                s = matcher.group();

            if (s != null && !s.trim().isEmpty())
                result.add(s.toUpperCase());
        }

        return result;
    }

    /**
     * Weights the given field value according to the provided parameters.
     * @param value can be a string or a list of string
     * @param weight weight for this particular field
     * @param terms terms to look for
     * @param searchMode search mode (AND or OR)
     * @return the weighted score
     */
    @SuppressWarnings("unchecked")
    public static int weightField(Object value, int weight, List<String> terms, SearchMode searchMode) {
        int result = 0;

        if (value == null)
            return result;

        if (value instanceof String) {
            String val = (String)value;
            int fieldScore = 0;
            boolean allTermsMatched = true;
            for (String term : terms) {
                int tmpScore = weightTerm(val, term, weight);
                if (tmpScore == 0)
                    allTermsMatched = false;
                else
                    fieldScore += tmpScore;
            }
            if (allTermsMatched || searchMode == SearchMode.OR)
                result += fieldScore;
        }
        else if (value instanceof List) {
            for (String val : (List<String>)value) {
                int fieldScore = 0;
                boolean allTermsMatched = true;
                for (String term : terms) {
                    int tmpScore = weightTerm(val, term, weight);
                    if (tmpScore == 0)
                        allTermsMatched = false;
                    else
                        fieldScore += tmpScore;
                }
                if (allTermsMatched || searchMode == SearchMode.OR)
                    result += fieldScore;
            }
        }

        return result;
    }

    /**
     * Weights the search term in the text using the provided weight.  The following rules are used:
     * <p/>
     * IF the text EXACTLY matches the search term, add weight * 25
     * ELSE:
     * - for each word in the text that completely matches the search term, add weight * 8
     * - for each word in the text that starts with (but doesn't exactly match) the search term, add weight * 4
     * - for each word in the text that contains the search term (but doesn't start with), add weight
     * <p/>
     * Note that all search is case-insensitive.
     * @param text text to weight
     * @param searchTerm search term to look for
     * @param weight weight to apply
     * @return the total weight
     */
    public static int weightTerm(String text, String searchTerm, int weight) {
        int score = 0;

        // make sure both strings are non-empty
        if (text == null || text.isEmpty() || searchTerm == null || searchTerm.isEmpty())
            return score;

        text = text.toLowerCase();
        searchTerm = searchTerm.toLowerCase();

        // perfect match on text
        if (text.equals(searchTerm))
            score += weight * 25;
        else {
            // search for term as complete words (add weight * 8 for every match in the text)
            score += (weight * 8) * getNumMatches(Pattern.compile("\\b" + searchTerm + "\\b"), text);

            // search for term as starts of words (add weight * 4 for every match in the text)
            score += (weight * 4) * getNumMatches(Pattern.compile("\\b" + searchTerm + "\\B"), text);

            // search for substring (add weight for every match in the text)
            score += weight * getNumMatches(Pattern.compile("\\B" + searchTerm), text);
        }

        return score;
    }

    /**
     * Utility method used by the weightTerm method.  The Matcher.getGroupCount doesn't actually return the number of matches, and there is no
     * way to get the number of matches without looping, so I put it in a utility method.
     * @param pattern compiled regex Pattern
     * @param text text to weight
     * @return the number of matches
     */
    private static int getNumMatches(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        int count = 0;

        while (matcher.find())
            count++;

        return count;
    }

    /**
     * Given a search string, returns the start and end positions of all the terms that should be highlighted (because they matched) in the given text.
     * <p/>
     * Created on Nov 17, 2010 by depryf
     * @param text text that needs to be hightlighted
     * @param searchString the search string the user provided (will be split into tokens)
     * @param mode search mode (and/or)
     * @return a list of start and end posistions (each sub-list is of size 2); maybe empty but never null
     */
    public static List<List<Integer>> calculateHighlighting(String text, String searchString, SearchMode mode) {
        List<List<Integer>> result = new ArrayList<>();

        // make sure there is something to highlight
        if (text == null || text.isEmpty())
            return result;

        // search should not be case-sensitive (and string are not mutable, so we can safely change it here)
        text = text.toUpperCase();

        // split the search string (taking into account quoted values) and iterate over each term
        boolean foundAllTerms = true;
        for (String searchText : splitSearchString(searchString)) {
            int start = text.indexOf(searchText);
            if (start == -1)
                foundAllTerms = false;
            while (start != -1) {
                int end = start + searchText.length() - 1;

                List<Integer> list = new ArrayList<>(2);
                list.add(start);
                list.add(end);
                result.add(list);

                start = text.indexOf(searchText, end + 1);
            }
        }

        if (!foundAllTerms && mode == SearchMode.AND)
            result.clear();

        // sort the results by start position (note that there could be some overlapping between the highlighted areas)
        result.sort(Comparator.comparing(l -> l.get(0)));

        return result;
    }

    /**
     * Given a search string, returns the start and end positions of all the terms that should be highlighted (because they matched) in the given list of text.
     * <p/>
     * Each text element is considered to be on its own line and is searched individually; the resulting indexed are based on the full text (the lines put together,
     * separeated by a single end-of-line character).
     * <p/>
     * Created on Nov 17, 2010 by depryf
     * @param lines text that needs to be hightlighted
     * @param searchString the search string the user provided (will be split into tokens)
     * @param mode search mode (and/or)
     * @return a list of start and end posistions (each sub-list is of size 2); maybe empty but never null
     */
    public static List<List<Integer>> calculateHighlighting(List<String> lines, String searchString, SearchMode mode) {
        List<List<Integer>> result = new ArrayList<>();

        if (lines.isEmpty())
            return result;

        int currentIdx = 0;
        for (String line : lines) {
            List<List<Integer>> lineResult = calculateHighlighting(line, searchString, mode);
            for (List<Integer> res : lineResult) {
                int start = res.get(0);
                int end = res.get(1);
                res.clear();
                res.add(start + currentIdx);
                res.add(end + currentIdx);
                result.add(res);
            }

            currentIdx += (line.length() + 1); // +1 for the end-of-line character
        }

        return result;
    }

}
