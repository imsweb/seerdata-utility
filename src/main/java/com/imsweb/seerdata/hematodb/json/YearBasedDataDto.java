/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YearBasedDataDto {

    @JsonProperty("diseases")
    protected List<YearBasedDiseaseDto> _disease;
    @JsonProperty("lastUpdated")
    protected String _lastUpdated;
    @JsonProperty("dataStructureVersion")
    protected String _dataStructureVersion;

    public List<YearBasedDiseaseDto> getDisease() {
        if (_disease == null) {
            _disease = new ArrayList<>();
        }
        return _disease;
    }

    public void setDisease(List<YearBasedDiseaseDto> disease) {
        _disease = disease;
    }

    public String getLastUpdated() {
        return _lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        _lastUpdated = lastUpdated;
    }

    public String getDataStructureVersion() {
        return _dataStructureVersion;
    }

    public void setDataStructureVersion(String dataStructureVersion) {
        _dataStructureVersion = dataStructureVersion;
    }
}
