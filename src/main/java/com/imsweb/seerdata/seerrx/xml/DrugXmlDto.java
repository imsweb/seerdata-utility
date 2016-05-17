
package com.imsweb.seerdata.seerrx.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("drug")
public class DrugXmlDto {

    @XStreamAsAttribute
    private String id;

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("do-not-code")
    private String doNotCode;

    @XStreamImplicit(itemFieldName = "alternate-name")
    private List<String> alternateName;

    @XStreamImplicit(itemFieldName = "abbreviation")
    private List<String> abbreviation;

    @XStreamImplicit(itemFieldName = "category")
    private List<String> category;

    @XStreamImplicit(itemFieldName = "sub-category")
    private List<String> subCategory;

    @XStreamImplicit(itemFieldName = "nsc-num")
    private List<String> nscNum;

    @XStreamImplicit(itemFieldName = "primary-site")
    private List<String> primarySite;

    private String histology;

    private String remarks;
    
    public DrugXmlDto() {
        alternateName = new ArrayList<>();
        abbreviation = new ArrayList<>();
        category = new ArrayList<>();
        subCategory = new ArrayList<>();
        nscNum = new ArrayList<>();
        primarySite = new ArrayList<>();
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

    public String getDoNotCode() {
        return doNotCode;
    }

    public void setDoNotCode(String doNotCode) {
        this.doNotCode = doNotCode;
    }

    public List<String> getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(List<String> alternateName) {
        this.alternateName = alternateName;
    }

    public List<String> getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(List<String> abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(List<String> subCategory) {
        this.subCategory = subCategory;
    }

    public List<String> getNscNum() {
        return nscNum;
    }

    public void setNscNum(List<String> nscNum) {
        this.nscNum = nscNum;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
