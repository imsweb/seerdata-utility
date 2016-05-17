
package com.imsweb.seerdata.seerrx.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("regimen")
public class RegimenXmlDto {

    @XStreamAsAttribute
    private String id;

    @XStreamAsAttribute
    private String name;

    @XStreamImplicit(itemFieldName = "alternate-name")
    private List<String> alternateName;

    @XStreamImplicit(itemFieldName = "primary-site")
    private List<String> primarySite;

    private String histology;

    private String radiation;

    private String remarks;

    @XStreamImplicit(itemFieldName = "drug")
    protected List<String> drug;

    public RegimenXmlDto() {
        alternateName = new ArrayList<>();
        primarySite = new ArrayList<>();
        drug = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(List<String> alternateName) {
        this.alternateName = alternateName;
    }

    public List<String> getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(List<String> primarySite) {
        this.primarySite = primarySite;
    }

    public String getHistology() {
        return histology;
    }

    public void setHistology(String histology) {
        this.histology = histology;
    }

    public String getRadiation() {
        return radiation;
    }

    public void setRadiation(String radiation) {
        this.radiation = radiation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<String> getDrug() {
        return drug;
    }

    public void setDrug(List<String> drug) {
        this.drug = drug;
    }
}
