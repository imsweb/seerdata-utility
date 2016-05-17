/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YearRangeString extends YearRange {

    @JsonProperty("value")
    protected String _value;

    public YearRangeString() {
    }

    public YearRangeString(Integer startYear, Integer endYear, String value) {
        super(startYear, endYear);
        _value = value;
    }

    public String getValue() {
        return _value;
    }
}
