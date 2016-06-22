/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateRange {

    @JsonProperty("start")
    protected String _startDate;
    @JsonProperty("end")
    protected String _endDate;

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    public DateRange() {
    }

    public DateRange(String startDate, String endDate) {
        _startDate = startDate;
        _endDate = endDate;
    }

    public String getStartDate() {
        return _startDate;
    }

    public String getEndDate() {
        return _endDate;
    }
    
    @Override
    public String toString() {
        if (_startDate != null && _endDate != null)
            return _startDate + " - " + _endDate;
        else if (_startDate != null)
            return _startDate + " and later";
        else if (_endDate != null)
            return _endDate + " and earlier";
        else
            return "";
    }
}
