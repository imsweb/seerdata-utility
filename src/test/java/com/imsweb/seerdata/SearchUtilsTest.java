/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerdata;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SearchUtilsTest {

    @Test
    public void testSplitSearchString() {
        Assert.assertEquals(0, SearchUtils.splitSearchString(null).size());
        Assert.assertEquals(0, SearchUtils.splitSearchString("").size());
        Assert.assertEquals(0, SearchUtils.splitSearchString("   ").size());
        Assert.assertEquals(0, SearchUtils.splitSearchString("\"\"").size());
        Assert.assertEquals(0, SearchUtils.splitSearchString("\"   \"").size());
        Assert.assertEquals(0, SearchUtils.splitSearchString("   \"   \"   ").size());
        Assert.assertEquals(1, SearchUtils.splitSearchString("val1").size());
        Assert.assertEquals(2, SearchUtils.splitSearchString("val1 val2").size());
        Assert.assertEquals(3, SearchUtils.splitSearchString("val1 val2 val3").size());
        Assert.assertEquals(1, SearchUtils.splitSearchString("\"val1 val2 val3\"").size());
        Assert.assertEquals(2, SearchUtils.splitSearchString("\"val1 val2\" val3").size());
        Assert.assertEquals(2, SearchUtils.splitSearchString("val1 \"val2 val3\"").size());
        Assert.assertEquals(3, SearchUtils.splitSearchString("\"val1\" \"val2\" \"val3\"").size());
        Assert.assertEquals(3, SearchUtils.splitSearchString("   \"   val1   \"    \"   val2   \"    \"   val3   \"   ").size());
        List<String> result = SearchUtils.splitSearchString("   \"val1\"    \"val2\"    \"val3\"   ");
        Assert.assertTrue(result.contains("VAL1"));
        Assert.assertTrue(result.contains("VAL2"));
        Assert.assertTrue(result.contains("VAL3"));
        Assert.assertEquals(3, SearchUtils.splitSearchString("   \"   val1   \"    \"   val2   \"    \"   val3   \"   ").size());
        result = SearchUtils.splitSearchString("   \"   val1   \"    \"   val2   \"    \"   val3   \"   ");
        Assert.assertTrue(result.contains("   VAL1   "));
        Assert.assertTrue(result.contains("   VAL2   "));
        Assert.assertTrue(result.contains("   VAL3   "));
    }

    @Test
    public void testCalculateHighlighting() {

        // regular searches
        assertHighlighting("", SearchUtils.calculateHighlighting((String)null, null, SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting((String)null, "", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting((String)null, "   ", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting((String)null, "abc", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("", "abc", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("   ", "abc", SearchUtils.SearchMode.OR));
        assertHighlighting("0-2", SearchUtils.calculateHighlighting("abc", "abc", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("abc", "", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("abc", "\"\"", SearchUtils.SearchMode.OR));
        assertHighlighting("1-3", SearchUtils.calculateHighlighting(" abc ", "abc", SearchUtils.SearchMode.OR));
        assertHighlighting("3-5", SearchUtils.calculateHighlighting("123abc456", "abc", SearchUtils.SearchMode.OR));
        assertHighlighting("3-5", SearchUtils.calculateHighlighting("123abc456", "\"abc\"", SearchUtils.SearchMode.OR));
        assertHighlighting("3-5", SearchUtils.calculateHighlighting("123abc456", "   \"abc\"   ", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("123abc456", "   \"   abc   \"   ", SearchUtils.SearchMode.OR));
        assertHighlighting("0-2,0-5,0-8", SearchUtils.calculateHighlighting("123abc456", "123 123abc 123abc456", SearchUtils.SearchMode.OR));
        assertHighlighting("0-8,0-5,0-2", SearchUtils.calculateHighlighting("123abc456", "123abc456 123abc 123", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("123abc456", "\"123 123abc 123abc456\"", SearchUtils.SearchMode.OR));
        assertHighlighting("0-1,10-11,20-21,30-31", SearchUtils.calculateHighlighting("01234567890123456789012345678901", "01", SearchUtils.SearchMode.OR));
        assertHighlighting("0-9,10-19,20-29", SearchUtils.calculateHighlighting("012345678901234567890123456789", "0123456789", SearchUtils.SearchMode.OR));
        assertHighlighting("3-5,13-15,23-25", SearchUtils.calculateHighlighting("123abc456\n123abc456\n123abc456\n", "abc", SearchUtils.SearchMode.OR));

        // AND vs OR
        assertHighlighting("10-12,31-33", SearchUtils.calculateHighlighting("something abc andsomethingelse def...", "abc def ghi", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting("something abc andsomethingelse def...", "\"abc def\"", SearchUtils.SearchMode.OR));
        assertHighlighting("10-12,31-33", SearchUtils.calculateHighlighting("something abc andsomethingelse def...", "abc def", SearchUtils.SearchMode.AND));
        assertHighlighting("", SearchUtils.calculateHighlighting("something abc andsomethingelse def...", "abc def ghi", SearchUtils.SearchMode.AND));

        // list of lines
        List<String> lines = new ArrayList<String>();
        lines.add("12345");
        lines.add("12345");
        lines.add("12345");
        assertHighlighting("1-1,7-7,13-13", SearchUtils.calculateHighlighting(lines, "2", SearchUtils.SearchMode.OR));
        assertHighlighting("1-1,7-7,13-13", SearchUtils.calculateHighlighting(lines, "2", SearchUtils.SearchMode.AND));
        assertHighlighting("1-1,3-3,7-7,9-9,13-13,15-15", SearchUtils.calculateHighlighting(lines, "2 4", SearchUtils.SearchMode.OR));
        assertHighlighting("1-1,3-3,7-7,9-9,13-13,15-15", SearchUtils.calculateHighlighting(lines, "2 4", SearchUtils.SearchMode.AND));
        assertHighlighting("1-1,7-7,13-13", SearchUtils.calculateHighlighting(lines, "2 6", SearchUtils.SearchMode.OR));
        assertHighlighting("", SearchUtils.calculateHighlighting(lines, "2 6", SearchUtils.SearchMode.AND));
    }

    // helper (translates an expected string result into a the list of list)
    private void assertHighlighting(String expectedStr, List<List<Integer>> actual) {
        List<List<Integer>> expected = new ArrayList<List<Integer>>();

        if (expectedStr != null && !expectedStr.trim().isEmpty()) {
            for (String pairs : expectedStr.split(",")) {
                String[] pair = pairs.split("\\-");
                List<Integer> list = new ArrayList<Integer>();
                list.add(Integer.valueOf(pair[0]));
                list.add(Integer.valueOf(pair[1]));
                expected.add(list);
            }
        }

        if (!expected.equals(actual))
            Assert.fail("Was expecting " + expected + " but got " + actual);
    }
}
