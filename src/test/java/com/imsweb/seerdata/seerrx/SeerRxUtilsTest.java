/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx;

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

import com.imsweb.seerdata.SearchUtils;
import com.imsweb.seerdata.seerrx.json.DrugsDataJsonDto;
import com.imsweb.seerdata.seerrx.xml.DrugXmlDto;
import com.imsweb.seerdata.seerrx.xml.DrugsDataXmlDto;
import com.imsweb.seerdata.seerrx.xml.RegimenXmlDto;

public class SeerRxUtilsTest {

    @Test
    public void testReadWriteXml() throws IOException {
        // start by reading our testing file
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("drugs-data-test.xml")) {
            DrugsDataXmlDto data = SeerRxUtils.readDrugsData(is);
            Assert.assertNotNull(data.getLastUpdated());
            Assert.assertNotNull(data.getDataStructureVersion());
            Assert.assertEquals(2, data.getDrug().size());
            Assert.assertEquals(1, data.getRegimen().size());
            File file = new File(System.getProperty("user.dir") + "/build/drugs-tmp.xml");

            // then write it
            try (OutputStream fos = new FileOutputStream(file)) {
                SeerRxUtils.writeDrugsData(fos, data);
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
                DrugsDataXmlDto data2 = SeerRxUtils.readDrugsData(is2);
                Assert.assertNotNull(data2.getLastUpdated());
                Assert.assertNotEquals(data.getDataStructureVersion(), data2.getLastUpdated());
                Assert.assertNotNull(data2.getDataStructureVersion());
                Assert.assertEquals(data.getDataStructureVersion(), data2.getDataStructureVersion());
                Assert.assertEquals(data.getDrug().size(), data2.getDrug().size());
                Assert.assertEquals(data.getRegimen().size(), data2.getRegimen().size());
            }
        }
    }

    @Test
    public void testReadWriteJson() throws IOException {
        // start by reading our testing file
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("drugs-data-test.json")) {
            DrugsDataJsonDto data = SeerRxUtils.readJsonData(is);
            Assert.assertNotNull(data.getLastUpdated());
            Assert.assertNotNull(data.getDataStructureVersion());
            Assert.assertEquals(1, data.getDrugs().size());
            Assert.assertEquals(1, data.getRegimens().size());
            File file = new File(System.getProperty("user.dir") + "/build/drugs-tmp.json");

            // then write it
            try (OutputStream fos = new FileOutputStream(file)) {
                SeerRxUtils.writeJswonData(fos, data);
            }

            // and finally read it again and check the values again
            try (InputStream is2 = new FileInputStream(file)) {
                DrugsDataJsonDto data2 = SeerRxUtils.readJsonData(is2);
                Assert.assertNotNull(data2.getLastUpdated());
                Assert.assertNotEquals(data.getDataStructureVersion(), data2.getLastUpdated());
                Assert.assertNotNull(data2.getDataStructureVersion());
                Assert.assertEquals(data.getDataStructureVersion(), data2.getDataStructureVersion());
                Assert.assertEquals(data.getDrugs().size(), data2.getDrugs().size());
                Assert.assertEquals(data.getRegimens().size(), data2.getRegimens().size());
            }
        }
    }

    @Test
    public void testInitialization() {
        DrugsDataXmlDto data = new DrugsDataXmlDto();
        data.setLastUpdated("now");
        data.setDataStructureVersion("1.0");

        DrugXmlDto drug = new DrugXmlDto();
        drug.setId("123");
        drug.setName("Drug 123");
        drug.getNscNum().add("456");
        drug.getAlternateName().add("Other Drug 123");
        data.getDrug().add(drug);

        RegimenXmlDto regimen = new RegimenXmlDto();
        regimen.setId("789");
        regimen.setName("Regiment 789");
        regimen.getDrug().add("123");
        data.getRegimen().add(regimen);

        Assert.assertFalse(SeerRxUtils.isInstanceRegistered());
        SeerRxUtils.registerInstance(data);
        Assert.assertTrue(SeerRxUtils.isInstanceRegistered());
        Assert.assertEquals("now", SeerRxUtils.getInstance().getDateLastUpdated());
        Assert.assertEquals("1.0", SeerRxUtils.getInstance().getDataStructureVersion());

        SeerRxUtils.unregisterInstance();
        Assert.assertFalse(SeerRxUtils.isInstanceRegistered());
    }

    @Test
    public void testSearch() {

        DrugsDataXmlDto data = new DrugsDataXmlDto();
        DrugXmlDto dto = new DrugXmlDto();
        dto.setId("123abc");
        dto.setName("ZZZ1");
        dto.getNscNum().add("ZZZ2");
        dto.getAlternateName().add("ZZZ3");
        data.getDrug().add(dto);
        RegimenXmlDto dto2 = new RegimenXmlDto();
        dto2.setId("9999");
        dto2.setName("ZZZ9");
        dto2.getDrug().add("123abc");
        data.getRegimen().add(dto2);
        SeerRxUtils.registerInstance(data);

        try {
            Assert.assertNotNull(SeerRxUtils.getInstance().getAllDrugs().get("123abc"));
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ", SeerRxUtils.SearchType.DRUGS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ1", SeerRxUtils.SearchType.DRUGS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ2", SeerRxUtils.SearchType.DRUGS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ3", SeerRxUtils.SearchType.DRUGS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertNotNull(SeerRxUtils.getInstance().getAllRegimens().get("9999"));
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ9", SeerRxUtils.SearchType.REGIMENS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ1", SeerRxUtils.SearchType.REGIMENS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertFalse(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ2", SeerRxUtils.SearchType.REGIMENS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
            Assert.assertTrue(SeerRxUtils.getInstance().searchDrugsAndRegimens("ZZZ3", SeerRxUtils.SearchType.REGIMENS_ONLY, SearchUtils.SearchMode.AND).isEmpty());
        }
        finally {
            SeerRxUtils.unregisterInstance();
        }
    }
}
