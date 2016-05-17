/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YearRangeInteger extends YearRange {

    @JsonProperty("value")
    protected Integer _value;

    public YearRangeInteger() {
    }

    public YearRangeInteger(Integer startYear, Integer endYear, Integer value) {
        super(startYear, endYear);
        _value = value;
    }

    public Integer getValue() {
        return _value;
    }
}