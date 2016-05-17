
package com.imsweb.seerdata.hematodb.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("diseases")
public class DiseasesDataXmlDto {

    @XStreamAsAttribute
    @XStreamAlias("last-updated")
    private String lastUpdated;

    @XStreamAsAttribute
    @XStreamAlias("data-structure-version")
    private String dataStructureVersion;

    @XStreamAsAttribute
    @XStreamAlias("applicable-dx-year")
    private Integer applicableDxYear;

    @XStreamImplicit
    private List<DiseaseXmlDto> disease;

    public DiseasesDataXmlDto() {
        disease = new ArrayList<>();
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

    public Integer getApplicableDxYear() {
        return applicableDxYear;
    }

    public void setApplicableDxYear(Integer applicableDxYear) {
        this.applicableDxYear = applicableDxYear;
    }

    public List<DiseaseXmlDto> getDisease() {
        return disease;
    }

    public void setDisease(List<DiseaseXmlDto> disease) {
        this.disease = disease;
    }
}
