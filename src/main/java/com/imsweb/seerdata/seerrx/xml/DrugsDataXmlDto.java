
package com.imsweb.seerdata.seerrx.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("drugs")
public class DrugsDataXmlDto {

    @XStreamAsAttribute
    @XStreamAlias("last-updated")
    private String lastUpdated;

    @XStreamAsAttribute
    @XStreamAlias("data-structure-version")
    private String dataStructureVersion;

    @XStreamImplicit
    private List<DrugXmlDto> drug;

    @XStreamImplicit
    private List<RegimenXmlDto> regimen;

    public DrugsDataXmlDto() {
        drug = new ArrayList<>();
        regimen = new ArrayList<>();
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDataStructureVersion() {
        return dataStructureVersion;
    }

    public void setDataStructureVersion(String dataStructureVersion) {
        this.dataStructureVersion = dataStructureVersion;
    }

    public List<DrugXmlDto> getDrug() {
        return drug;
    }

    public void setDrug(List<DrugXmlDto> drug) {
        this.drug = drug;
    }

    public List<RegimenXmlDto> getRegimen() {
        return regimen;
    }

    public void setRegimen(List<RegimenXmlDto> regimen) {
        this.regimen = regimen;
    }
}
