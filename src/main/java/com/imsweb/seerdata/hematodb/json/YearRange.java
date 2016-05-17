/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YearRange {

    @JsonProperty("start")
    protected Integer _startYear;
    @JsonProperty("end")
    protected Integer _endYear;

    public YearRange() {
    }

    public YearRange(Integer startYear, Integer endYear) {
        _startYear = startYear;
        _endYear = endYear;
    }

    public Integer getStartYear() {
        return _startYear;
    }

    public Integer getEndYear() {
        return _endYear;
    }
    
    @Override
    public String toString() {
        if (_startYear != null && _endYear != null)
            return _startYear + "-" + _endYear;
        else if (_startYear != null)
            return _startYear + " and later";
        else if (_endYear != null)
            return _endYear + " and earlier";
        else
            return "";
    }
}
