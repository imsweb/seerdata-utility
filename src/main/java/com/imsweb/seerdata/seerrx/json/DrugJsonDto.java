/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"type"})
public class DrugJsonDto {

    @JsonProperty("id")
    private String _id;

    @JsonProperty("name")
    private String _name;

    @JsonProperty("first_published")
    protected String _firstPublished;

    @JsonProperty("last_modified")
    protected String _lastModified;

    @JsonProperty("version")
    protected String _version;

    @JsonProperty("histology")
    private String _histology;

    @JsonProperty("remarks")
    private String _remarks;

    @JsonProperty("do_not_code")
    private String _doNotCode;

    @JsonProperty("evs_id")
    private String _evsId;

    @JsonProperty("nsc_number")
    private List<String> _nscNum;

    @JsonProperty("primary_site")
    private List<String> _primarySite;

    @JsonProperty("alternate_name")
    private List<String> _alternateName;

    @JsonProperty("abbreviation")
    private List<String> _abbreviation;

    @JsonProperty("category")
    private List<String> _category;

    @JsonProperty("subcategory")
    private List<String> _subCategory;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getFirstPublished() {
        return _firstPublished;
    }

    public void setFirstPublished(String firstPublished) {
        _firstPublished = firstPublished;
    }

    public String getLastModified() {
        return _lastModified;
    }

    public void setLastModified(String lastModified) {
        _lastModified = lastModified;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public String getHistology() {
        return _histology;
    }

    public void setHistology(String histology) {
        _histology = histology;
    }

    public String getRemarks() {
        return _remarks;
    }

    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public String getDoNotCode() {
        return _doNotCode;
    }

    public void setDoNotCode(String doNotCode) {
        _doNotCode = doNotCode;
    }

    public String getEvsId() {
        return _evsId;
    }

    public void setEvsId(String evsId) {
        _evsId = evsId;
    }

    public List<String> getNscNum() {
        return _nscNum;
    }

    public void setNscNum(List<String> nscNum) {
        _nscNum = nscNum;
    }

    public List<String> getPrimarySite() {
        return _primarySite;
    }

    public void setPrimarySite(List<String> primarySite) {
        _primarySite = primarySite;
    }

    public List<String> getAlternateName() {
        return _alternateName;
    }

    public void setAlternateName(List<String> alternateName) {
        _alternateName = alternateName;
    }

    public List<String> getAbbreviation() {
        return _abbreviation;
    }

    public void setAbbreviation(List<String> abbreviation) {
        _abbreviation = abbreviation;
    }

    public List<String> getCategory() {
        return _category;
    }

    public void setCategory(List<String> category) {
        _category = category;
    }

    public List<String> getSubCategory() {
        return _subCategory;
    }

    public void setSubCategory(List<String> subCategory) {
        _subCategory = subCategory;
    }
}
