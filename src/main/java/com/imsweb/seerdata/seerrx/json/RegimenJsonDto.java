/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"type"})
public class RegimenJsonDto {

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

    @JsonProperty("radiation")
    private String _radiation;

    private String _remarks;

    @JsonProperty("alternate_name")
    private List<String> _alternateName;

    @JsonProperty("primary_site")
    private List<String> _primarySite;

    @JsonProperty("drugs")
    private List<String> _drug;

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

    public String getRadiation() {
        return _radiation;
    }

    public void setRadiation(String radiation) {
        _radiation = radiation;
    }

    public String getRemarks() {
        return _remarks;
    }

    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public List<String> getAlternateName() {
        return _alternateName;
    }

    public void setAlternateName(List<String> alternateName) {
        _alternateName = alternateName;
    }

    public List<String> getPrimarySite() {
        return _primarySite;
    }

    public void setPrimarySite(List<String> primarySite) {
        _primarySite = primarySite;
    }

    public List<String> getDrug() {
        return _drug;
    }

    public void setDrug(List<String> drug) {
        _drug = drug;
    }
}
