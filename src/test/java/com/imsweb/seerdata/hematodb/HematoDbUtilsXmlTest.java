/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.imsweb.seerdata.SearchUtils.SearchMode;
import com.imsweb.seerdata.TestUtils;
import com.imsweb.seerdata.hematodb.xml.DiseaseXmlDto;
import com.imsweb.seerdata.hematodb.xml.DiseasesDataXmlDto;

public class HematoDbUtilsXmlTest {

    @Test
    public void testReadWrite() throws IOException {
        // start by reading our testing file
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test-2015.xml")) {
            DiseasesDataXmlDto data = HematoDbUtils.readDiseasesData(is);
            Assert.assertNotNull(data.getLastUpdated());
            Assert.assertNotNull(data.getDataStructureVersion());
            Assert.assertEquals(3, data.getDisease().size());
            File file = new File(TestUtils.getWorkingDirectory() + "/build/diseases-tmp.xml");

            // then write it
            try (OutputStream fos = new FileOutputStream(file)) {
                HematoDbUtils.writeDiseasesData(fos, data);
            }

            // make sure all implicit collections are properly map
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                try (StringWriter writer = new StringWriter()) {
                    IOUtils.copy(reader, writer);
                    if (writer.toString().contains("<string>"))
                        Assert.fail("Found string tag, make sure the implicit collections are properly mapped!");
                }
            }

            // and finally read it again and check the values again
            try (InputStream is2 = new FileInputStream(file)) {
                DiseasesDataXmlDto data2 = HematoDbUtils.readDiseasesData(is2);
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
        HematoDbUtils.unregisterInstance();
        Assert.assertFalse(HematoDbUtils.isInstanceRegistered());

        DiseasesDataXmlDto data = new DiseasesDataXmlDto();
        data.setLastUpdated("now");
        data.setDataStructureVersion("1.0");
        data.setApplicableDxYear(2010);
        DiseaseXmlDto dto = new DiseaseXmlDto();
        dto.setId("123");
        dto.setName("Some Name");
        dto.setCodeIcdO3("9000/3");
        dto.setAbstractorNote("Notes");
        data.getDisease().add(dto);

        HematoDbUtils.registerInstance(data);
        Assert.assertTrue(HematoDbUtils.isInstanceRegistered(2010));
        Assert.assertFalse(HematoDbUtils.isInstanceRegistered());
        Assert.assertEquals("now", HematoDbUtils.getInstance(2010).getDateLastUpdated());
        Assert.assertEquals("1.0", HematoDbUtils.getInstance(2010).getDataStructureVersion());
        Assert.assertEquals(2010, HematoDbUtils.getInstance(2010).getApplicableDxYear().intValue());
        Assert.assertEquals("Notes", HematoDbUtils.getInstance(2010).getAllDiseases().get("123").getAbstractorNote());

        HematoDbUtils.unregisterInstance(2010);
        Assert.assertFalse(HematoDbUtils.isInstanceRegistered(2010));

        Assert.assertFalse(HematoDbUtils.isInstanceRegistered(2015));
        HematoDbUtils.registerInstance(HematoDbUtils.readDiseasesData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test-2015.xml")));
        Assert.assertTrue(HematoDbUtils.isInstanceRegistered(2015));
        Assert.assertFalse(HematoDbUtils.getInstance(2015).getAllDiseases().isEmpty());
        Assert.assertNotNull(HematoDbUtils.getInstance(2015).getDateLastUpdated());
        Assert.assertEquals("2.0", HematoDbUtils.getInstance(2015).getDataStructureVersion());
        Assert.assertEquals(2015, HematoDbUtils.getInstance(2015).getApplicableDxYear().intValue());

        // make sure same primary references are valid
        for (DiseaseDto disease : HematoDbUtils.getInstance(2015).getAllDiseases().values())
            for (String s : disease.getSamePrimary())
                if (!HematoDbUtils.getInstance(2015).getAllDiseases().containsKey(s))
                    Assert.fail("Disease #" + disease.getId() + " has a bad same primary reference: " + s);

        boolean thrown = false;
        try {
            HematoDbUtils.getInstance(2015).searchDiseases("acute \"leukemia with\"", SearchMode.OR, 2014);
        }
        catch (RuntimeException e) {
            thrown = true;
        }
        if (!thrown)
            Assert.fail("Exception was not thrown for incompatible years.");

        Assert.assertFalse(HematoDbUtils.getInstance(2015).searchDiseases("acute \"leukemia with\"", SearchMode.OR, 2015).isEmpty());
        Assert.assertFalse(HematoDbUtils.getInstance(2015).searchDiseases("acute \"leukemia with\"", SearchMode.AND, 2015).isEmpty());

        HematoDbUtils.unregisterInstance(2015);
        Assert.assertFalse(HematoDbUtils.isInstanceRegistered(2015));
    }

    @Test
    public void testSearch() {

        DiseasesDataXmlDto data = new DiseasesDataXmlDto();
        data.setLastUpdated("2010-06-15");
        data.setApplicableDxYear(2010);
        DiseaseXmlDto dto = new DiseaseXmlDto();
        dto.setId("123abc");
        dto.setName("ZZZ1");
        dto.setCodeIcdO3("ZZZ2");
        dto.setAbstractorNote("ZZZ3");
        dto.setCodeIcdO1("CODE1, CODE2");
        data.getDisease().add(dto);
        HematoDbUtils.registerInstance(data);

        try {
            Assert.assertNotNull(HematoDbUtils.getInstance(2010).getAllDiseases().get("123abc"));
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("ZZZ", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("ZZZ1", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("ZZZ2", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("ZZZ3", SearchMode.AND, 2010).isEmpty());

            // AND vs OR
            HematoDbUtils.getInstance(2010).getAllDiseases().get("123abc").setName("XXX AAA BBB");
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("XXX", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("AAA", SearchMode.OR, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("BBB", SearchMode.OR, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance(2010).searchDiseases("XXX AAA", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance(2010).searchDiseases("AAA XXX", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("XXX yyy", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("AAA yyy", SearchMode.OR, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(2400, HematoDbUtils.getInstance(2010).searchDiseases("XXX AAA BBB", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + 2*contains
            Assert.assertEquals(2400, HematoDbUtils.getInstance(2010).searchDiseases("BBB AAA XXX", SearchMode.OR, 2010).get(0).getScore().intValue()); // starts with + 2*contains

            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("XXX", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("AAA", SearchMode.AND, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(800, HematoDbUtils.getInstance(2010).searchDiseases("BBB", SearchMode.AND, 2010).get(0).getScore().intValue()); // contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance(2010).searchDiseases("XXX AAA", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertEquals(1600, HematoDbUtils.getInstance(2010).searchDiseases("AAA XXX", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + contains
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("XXX yyy", SearchMode.AND, 2010).isEmpty()); // not all terms found
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("AAA yyy", SearchMode.AND, 2010).isEmpty()); // not all terms found
            Assert.assertEquals(2400, HematoDbUtils.getInstance(2010).searchDiseases("XXX AAA BBB", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + 2*contains
            Assert.assertEquals(2400, HematoDbUtils.getInstance(2010).searchDiseases("BBB AAA XXX", SearchMode.AND, 2010).get(0).getScore().intValue()); // starts with + 2*contains

            // quoted vs non-quoted
            HematoDbUtils.getInstance(2010).getAllDiseases().get("123abc").setName("val1 val2 val3 val4");
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val2", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 val2 val3 val4", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 val2 val3 val4 val5", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("\"val1 val2 val3 val4\"", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 \"val2 val3\" val4", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 \"val2 val3\" val4 val5", SearchMode.OR, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 \"val1 val5\" val4", SearchMode.OR, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("\"val4 val3 val2 val1\"", SearchMode.OR, 2010).isEmpty());

            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val2", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 val2 val3 val4", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("val1 val2 val3 val4 val5", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("\"val1 val2 val3 val4\"", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("val1 \"val2 val3\" val4", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("val1 \"val2 val3\" val4 val5", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("val1 \"val1 val5\" val4", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("\"val4 val3 val2 val1\"", SearchMode.AND, 2010).isEmpty());

            // test searching on ICD-O-1, which is a list of values represented by a CSV string
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("CODE1", SearchMode.AND, 2010).isEmpty());
            Assert.assertFalse(HematoDbUtils.getInstance(2010).searchDiseases("CODE2", SearchMode.AND, 2010).isEmpty());
            Assert.assertTrue(HematoDbUtils.getInstance(2010).searchDiseases("CODE1, CODE2", SearchMode.AND, 2010).isEmpty());
        }
        finally {
            HematoDbUtils.unregisterInstance(2010);
        }
    }

    @Test
    public void testMultiplePrimaries() throws IOException {

        HematoDbUtils.registerInstance(HematoDbUtils.readDiseasesData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test-2015.xml")));

        try {
            boolean thrown = false;
            try {
                HematoDbUtils.getInstance(2015).isMultiplePrimaries("9737/3", "9870/3", 2014);
            }
            catch (RuntimeException e) {
                thrown = true;
            }
            if (!thrown)
                Assert.fail("Exception was not thrown for incompatible years.");

            // multiple-primaries - check codes that DO exist
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation("9870/3"));
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation("9737/3"));

            // multiple-primaries - check codes that DONT exist
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation("0001"));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation("9500"));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation("9591/3"));

            // multiple-primaries - check an empty value code
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation(""));

            // multiple-primaries - check a NULL value code
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isValidIcdCodeForMultiplePrimariesCalculation(null));

            // multiple-primaries - codes that ARE same primaries
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isMultiplePrimaries("9737/3", "9870/3", 2015));
            // multiple-primaries - codes that ARE NOT same primaries
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries("9870/3", "9805/3", 2015));
            // multiple-primaries - code that exsit against a null value code, vice versa
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries("9870/3", null, 2015));
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries(null, "9870/3", 2015));
            // multiple-primaries - code that exists against an empty value code, vice versa
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries("9870/3", "", 2015));
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries("", "9870/3", 2015));
            // multiple-primaries - null and empty value codes
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries(null, null, 2015));
            Assert.assertTrue(HematoDbUtils.getInstance(2015).isMultiplePrimaries("", "", 2015));
        }
        finally {
            HematoDbUtils.unregisterInstance(2015);
        }
    }

    @Test
    public void testIsAcuteTransformation() throws IOException {
        HematoDbUtils.registerInstance(HematoDbUtils.readDiseasesData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test-2015.xml")));
        try {
            boolean thrown = false;
            try {
                HematoDbUtils.getInstance(2015).isAcuteTransformation("9737/3", "9870/3", 2014);
            }
            catch (RuntimeException e) {
                thrown = true;
            }
            if (!thrown)
                Assert.fail("Exception was not thrown for incompatible years.");

            // Invalid codes
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation(null, null, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9737/3", null, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation(null, "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("  ", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("8000/3", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9737/3", "9737/03", 2015));

            Assert.assertTrue(HematoDbUtils.getInstance(2015).isAcuteTransformation("9737/3", "9870/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9870/3", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9870/3", "9805/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9805/3", "9870/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9805/3", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isAcuteTransformation("9737/3", "9805/3", 2015));
        }
        finally {
            HematoDbUtils.unregisterInstance(2015);
        }
    }

    @Test
    public void testIsChronicTransformation() throws IOException {
        HematoDbUtils.registerInstance(HematoDbUtils.readDiseasesData(Thread.currentThread().getContextClassLoader().getResourceAsStream("diseases-data-test-2015.xml")));
        try {
            boolean thrown = false;
            try {
                HematoDbUtils.getInstance(2015).isChronicTransformation("9737/3", "9870/3", 2014);
            }
            catch (RuntimeException e) {
                thrown = true;
            }
            if (!thrown)
                Assert.fail("Exception was not thrown for incompatible years.");

            // Invalid codes
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation(null, null, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9737/3", null, 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation(null, "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("  ", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("8000/3", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9870/03", "9737/3", 2015));

            Assert.assertTrue(HematoDbUtils.getInstance(2015).isChronicTransformation("9870/3", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9737/3", "9870/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9870/3", "9805/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9805/3", "9870/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9805/3", "9737/3", 2015));
            Assert.assertFalse(HematoDbUtils.getInstance(2015).isChronicTransformation("9737/3", "9805/3", 2015));
        }
        finally {
            HematoDbUtils.unregisterInstance(2015);
        }
    }
}