/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.seerdata.SearchUtils.SearchMode;
import com.imsweb.seerdata.TestUtils;
import com.imsweb.seerdata.hematodb.json.YearBasedDataDto;
import com.imsweb.seerdata.hematodb.json.YearBasedDiseaseDto;
import com.imsweb.seerdata.hematodb.json.YearRange;
import com.imsweb.seerdata.hematodb.json.YearRangeString;

public class HematoDbUtilsJsonTest {

    @Test
    public void testReadWrite() throws IOException {
        // start by reading our testing file
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test.json")) {
            YearBasedDataDto data = HematoDbUtils.readYearBasedDiseaseData(is);
            Assert.assertNotNull(data.getLastUpdated());
            Assert.assertNotNull(data.getDataStructureVersion());
            Assert.assertEquals(3, data.getDisease().size());
            File file = new File(TestUtils.getWorkingDirectory() + "/build/diseases-tmp.json");

            // then write it
            try (OutputStream fos = new FileOutputStream(file)) {
                HematoDbUtils.writeYearBasedDiseaseData(fos, data);
            }

            // and finally read it again and check the values again
            try (InputStream is2 = new FileInputStream(file)) {
                YearBasedDataDto data2 = HematoDbUtils.readYearBasedDiseaseData(is2);
                Assert.assertNotNull(data2.getLastUpdated());
                Assert.assertNotEquals(data.getDataStructureVersion(), data2.getLastUpdated());
                Assert.assertNotNull(data2.getDataStructureVersion());
                Assert.assertEquals(data.getDataStructureVersion(), data2.getDataStructureVersion());
                Assert.assertEquals(data.getDisease().size(), data2.getDisease().size());
            }
        }
    }

    @Test
    public void testInitialization() throws IOException {

        Assert.assertFalse(HematoDbUtils.isInstanceRegistered());
        HematoDbUtils.registerInstance(HematoDbUtils.readYearBasedDiseaseData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test.json")));
        Assert.assertTrue(HematoDbUtils.isInstanceRegistered());
        Assert.assertFalse(HematoDbUtils.getInstance().getAllYearBasedDiseases().isEmpty());
        Assert.assertNotNull(HematoDbUtils.getInstance().getDateLastUpdated());
        Assert.assertNotNull(HematoDbUtils.getInstance().getDataStructureVersion());

        // make sure same primary references are valid
        for (YearBasedDiseaseDto disease : HematoDbUtils.getInstance().getAllYearBasedDiseases().values()) {
            for (String s : disease.getSamePrimary(2015))
                if (!HematoDbUtils.getInstance().getAllYearBasedDiseases().containsKey(s))
                    Assert.fail("Disease #" + disease.getId() + " has a bad same primary reference: " + s);
            for (String s : disease.getSamePrimary(2010))
                if (!HematoDbUtils.getInstance().getAllYearBasedDiseases().containsKey(s))
                    Assert.fail("Disease #" + disease.getId() + " has a bad same primary reference: " + s);
            //The same-primaries values for 2009 should all be invalid
            for (String s : disease.getSamePrimary(2009))
                if (HematoDbUtils.getInstance().getAllYearBasedDiseases().containsKey(s))
                    Assert.fail("Disease #" + disease.getId() + " has a good same primary reference: " + s);
        }

        //Test searching- current year
        Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("acute \"leukemia with\"", SearchMode.OR).isEmpty());
        Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("acute \"leukemia with t\"", SearchMode.AND).isEmpty());
        List<DiseaseSearchResultDto> results = HematoDbUtils.getInstance().searchDiseases("acute \"leukemia with\"", SearchMode.AND);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals("9805/3", results.get(0).getDisease().getCodeIcdO3());
        Assert.assertNull(results.get(0).getDisease().getGrade());
        Assert.assertEquals(0, results.get(0).getDisease().getSamePrimary().size());

        //Searching results for 2009
        results = HematoDbUtils.getInstance().searchDiseases("acute \"leukemia with\"", SearchMode.AND, 2009);
        Assert.assertEquals((Integer)9, results.get(0).getDisease().getGrade());
        Assert.assertEquals(0, results.get(0).getDisease().getSamePrimary().size());

        //Searching results for 2001
        results = HematoDbUtils.getInstance().searchDiseases("acute \"leukemia with\"", SearchMode.AND, 2001);
        Assert.assertEquals((Integer)5, results.get(0).getDisease().getGrade());
        Assert.assertEquals(1, results.get(0).getDisease().getSamePrimary().size());

        HematoDbUtils.unregisterInstance();
        Assert.assertFalse(HematoDbUtils.isInstanceRegistered());
    }

    @Test
    public void testSearch() {

        YearBasedDataDto data = new YearBasedDataDto();
        data.setLastUpdated("2010-06-15");
        YearBasedDiseaseDto dto = new YearBasedDiseaseDto();
        dto.setId("123abc");
        dto.setName("ZZZ1");
        dto.setValid(new YearRange(null, null));
        dto.setIcdO3Effective(new YearRange(2010, 2010));
        dto.setIcdO3Morphology("ZZZ2");
        dto.setIcdO1Effective(new YearRange(2010, 2010));
        dto.setIcdO1Morphology(Arrays.asList("CODE1", "CODE2"));
        dto.setAbstractorNote(Collections.singletonList(new YearRangeString(2010, 2010, "ZZZ3")));
        data.getDisease().add(dto);
        HematoDbUtils.registerInstance(data);

        try {
            Assert.assertNotNull(HematoDbUtils.getInstance().getAllYearBasedDiseases().get("123abc"));
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("ZZZ", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("ZZZ1", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("ZZZ2", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("ZZZ3", SearchMode.AND, 2010).isEmpty());

            // AND vs OR
            dto.setName("XXX AAA BBB");
            HematoDbUtils.registerInstance(data);
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("XXX", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("AAA", SearchMode.OR, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("BBB", SearchMode.OR, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance().searchDiseases("XXX AAA", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance().searchDiseases("AAA XXX", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("XXX yyy", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("AAA yyy", SearchMode.OR, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(2400, HematoDbUtils.getInstance().searchDiseases("XXX AAA BBB", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + 2*contains
            Assert.assertEquals(2400, HematoDbUtils.getInstance().searchDiseases("BBB AAA XXX", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + 2*contains

            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("XXX", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("AAA", SearchMode.AND, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(800, HematoDbUtils.getInstance().searchDiseases("BBB", SearchMode.AND, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance().searchDiseases("XXX AAA", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance().searchDiseases("AAA XXX", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("XXX yyy", SearchMode.AND, 2010).isEmpty()); // not all terms found
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("AAA yyy", SearchMode.AND, 2010).isEmpty()); // not all terms found
            Assert.assertEquals(2400, HematoDbUtils.getInstance().searchDiseases("XXX AAA BBB", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + 2*contains
            Assert.assertEquals(2400, HematoDbUtils.getInstance().searchDiseases("BBB AAA XXX", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + 2*contains

            // quoted vs non-quoted
            dto.setName("val1 val2 val3 val4");
            HematoDbUtils.registerInstance(data);
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val2", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 val2 val3 val4", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 val2 val3 val4 val5", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("\"val1 val2 val3 val4\"", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 \"val2 val3\" val4", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 \"val2 val3\" val4 val5", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 \"val1 val5\" val4", SearchMode.OR, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("\"val4 val3 val2 val1\"", SearchMode.OR, 2010).isEmpty());

            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val2", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 val2 val3 val4", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("val1 val2 val3 val4 val5", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("\"val1 val2 val3 val4\"", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("val1 \"val2 val3\" val4", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("val1 \"val2 val3\" val4 val5", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("val1 \"val1 val5\" val4", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("\"val4 val3 val2 val1\"", SearchMode.AND, 2010).isEmpty());

            // test searching on ICD-O-1, which is a list of values represented by a CSV string
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("CODE1", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance().searchDiseases("CODE2", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance().searchDiseases("CODE1, CODE2", SearchMode.AND, 2010).isEmpty());
        }
        finally {
            HematoDbUtils.unregisterInstance();
        }
    }

    @Test
    public void testMultiplePrimaries() throws IOException {
        HematoDbUtils.registerInstance(HematoDbUtils.readYearBasedDiseaseData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test.json")));

        try {

            // multiple-primaries - check codes that DO exist
            Assert.assertTrue(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation("9870/3"));
            Assert.assertTrue(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation("9737/3"));

            // multiple-primaries - check codes that DONT exist
            Assert.assertFalse(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation("0001"));
            Assert.assertFalse(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation("9500"));
            Assert.assertFalse(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation("9591/3"));

            // multiple-primaries - check an empty value code
            Assert.assertFalse(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation(""));

            // multiple-primaries - check a NULL value code
            Assert.assertFalse(HematoDbUtils.getInstance().isValidIcdCodeForMultiplePrimariesCalculation(null));

            // multiple-primaries - codes that ARE same primaries
            Assert.assertFalse(HematoDbUtils.getInstance().isMultiplePrimaries("9737/3", "9870/3", 2015, 2015));
            // multiple-primaries - codes that ARE NOT same primaries
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries("9870/3", "9805/3", 2015, 2015));
            // multiple-primaries - code that exsit against a null value code, vice versa
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries("9870/3", null, 2015, 2015));
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries(null, "9870/3", 2015, 2015));
            // multiple-primaries - code that exists against an empty value code, vice versa
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries("9870/3", "", 2015, 2015));
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries("", "9870/3", 2015, 2015));
            // multiple-primaries - null and empty value codes
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries(null, null, 2015, 2015));
            Assert.assertTrue(HematoDbUtils.getInstance().isMultiplePrimaries("", "", 2015, 2015));
        }
        finally {
            HematoDbUtils.unregisterInstance();
        }
    }

    @Test
    public void testIsAcuteTransformation() throws IOException {
        HematoDbUtils.registerInstance(HematoDbUtils.readYearBasedDiseaseData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test.json")));
        try {

            // Invalid codes
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation(null, null, 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9737/3", null, 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation(null, "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("  ", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("8000/3", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9737/3", "9737/03", 2015, 2015));

            Assert.assertTrue(HematoDbUtils.getInstance().isAcuteTransformation("9737/3", "9870/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9870/3", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9870/3", "9805/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9805/3", "9870/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9805/3", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isAcuteTransformation("9737/3", "9805/3", 2015, 2015));
        }
        finally {
            HematoDbUtils.unregisterInstance();
        }
    }

    @Test
    public void testIsChronicTransformation() throws IOException {
        HematoDbUtils.registerInstance(HematoDbUtils.readYearBasedDiseaseData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test.json")));
        try {

            // Invalid codes
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation(null, null, 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9737/3", null, 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation(null, "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("  ", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("8000/3", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9870/03", "9737/3", 2015, 2015));

            Assert.assertTrue(HematoDbUtils.getInstance().isChronicTransformation("9870/3", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9737/3", "9870/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9870/3", "9805/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9805/3", "9870/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9805/3", "9737/3", 2015, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance().isChronicTransformation("9737/3", "9805/3", 2015, 2015));
        }
        finally {
            HematoDbUtils.unregisterInstance();
        }
    }
}
