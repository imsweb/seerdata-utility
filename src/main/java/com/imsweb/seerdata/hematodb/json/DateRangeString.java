/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateRangeString extends DateRange {

    @JsonProperty("value")
    protected String _value;

    public DateRangeString() {
    }

    public DateRangeString(String startDate, String endDate, String value) {
        super(startDate, endDate);
        _value = value;
    }

    public String getValue() {
        return _value;
    }
}
