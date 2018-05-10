/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DrugsDataJsonDto {

    @JsonProperty("dataStructureVersion")
    private String _dataStructureVersion;

    @JsonProperty("lastUpdated")
    private String _lastUpdated;

    @JsonProperty("drugs")
    private List<DrugJsonDto> _drugs;

    @JsonProperty("regimens")
    private List<RegimenJsonDto> _regimens;

    public String getDataStructureVersion() {
        return _dataStructureVersion;
    }

    public void setDataStructureVersion(String dataStructureVersion) {
        _dataStructureVersion = dataStructureVersion;
    }

    public String getLastUpdated() {
        return _lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        _lastUpdated = lastUpdated;
    }

    public List<DrugJsonDto> getDrugs() {
        return _drugs;
    }

    public void setDrugs(List<DrugJsonDto> drugs) {
        _drugs = drugs;
    }

    public List<RegimenJsonDto> getRegimens() {
        return _regimens;
    }

    public void setRegimens(List<RegimenJsonDto> regimens) {
        _regimens = regimens;
    }
}
