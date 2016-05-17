/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SiteRange {

    @JsonProperty("low")
    protected String _low;
    @JsonProperty("high")
    protected String _high;

    public SiteRange() {
    }

    public SiteRange(String low, String high) {
        _low = low;
        _high = high;
    }

    public String getLow() {
        return _low;
    }

    public String getHigh() {
        return _high;
    }

    @Override
    public String toString() {
        if (_low.equals(_high))
            return _low;
        else
            return _low + "-" + _high;
    }
}
