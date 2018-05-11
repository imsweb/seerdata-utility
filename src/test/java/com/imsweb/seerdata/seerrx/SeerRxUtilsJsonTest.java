/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.seerdata.SearchUtils;
import com.imsweb.seerdata.TestUtils;
import com.imsweb.seerdata.seerrx.json.DrugJsonDto;
import com.imsweb.seerdata.seerrx.json.DrugsDataJsonDto;
import com.imsweb.seerdata.seerrx.json.RegimenJsonDto;

public class SeerRxUtilsJsonTest {

    @Test
    public void testReadWrite() throws IOException {
        // start by reading our testing file
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("drugs-data-test.json")) {
            DrugsDataJsonDto data = SeerRxUtils.readJsonData(is);
            Assert.assertNotNull(data.getLastUpdated());
            Assert.assertNotNull(data.getDataStructureVersion());
            Assert.assertEquals(1, data.getDrugs().size());
            Assert.assertEquals(1, data.getRegimens().size());
            File file = new File(TestUtils.getWorkingDirectory(), "/build/drugs-tmp.json");

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
        DrugsDataJsonDto data = new DrugsDataJsonDto();
        data.setLastUpdated("now");
        data.setDataStructureVersion("1.0");

        DrugJsonDto drug = new DrugJsonDto();
        drug.setId("123");
        drug.setName("Drug 123");
        drug.setNscNum(Collections.singletonList("456"));
        drug.setAlternateName(Collections.singletonList("Other Drug 123"));
        data.setDrugs(Collections.singletonList(drug));

        RegimenJsonDto regimen = new RegimenJsonDto();
        regimen.setId("789");
        regimen.setName("Regiment 789");
        regimen.setDrug(Collections.singletonList("123"));
        data.setRegimens(Collections.singletonList(regimen));

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

        DrugsDataJsonDto data = new DrugsDataJsonDto();
        DrugJsonDto dto = new DrugJsonDto();
        dto.setId("123abc");
        dto.setName("ZZZ1");
        dto.setNscNum(Collections.singletonList("ZZZ2"));
        dto.setAlternateName(Collections.singletonList("ZZZ3"));
        data.setDrugs(Collections.singletonList(dto));
        RegimenJsonDto dto2 = new RegimenJsonDto();
        dto2.setId("9999");
        dto2.setName("ZZZ9");
        dto2.setDrug(Collections.singletonList("123abc"));
        data.setRegimens(Collections.singletonList(dto2));
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
