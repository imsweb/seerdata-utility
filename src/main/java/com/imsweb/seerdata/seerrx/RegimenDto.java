/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx;

import java.util.ArrayList;
import java.util.List;

public class RegimenDto {

    protected List<String> _drug;
    protected List<String> _alternateName;
    protected List<String> _primarySite;
    protected String _remarks;
    protected String _histology;
    protected String _radiation;
    protected String _name;
    protected String _id;

    public List<String> getDrug() {
        if (_drug == null) {
            _drug = new ArrayList<>();
        }
        return this._drug;
    }

    public List<String> getAlternateName() {
        if (_alternateName == null) {
            _alternateName = new ArrayList<>();
        }
        return this._alternateName;
    }

    public List<String> getPrimarySite() {
        if (_primarySite == null) {
            _primarySite = new ArrayList<>();
        }
        return this._primarySite;
    }

    public String getRemarks() {
        return this._remarks;
    }

    public void setRemarks(String value) {
        this._remarks = value;
    }

    public String getHistology() {
        return this._histology;
    }

    public void setHistology(String value) {
        this._histology = value;
    }

    public String getRadiation() {
        return this._radiation;
    }

    public void setRadiation(String value) {
        this._radiation = value;
    }

    public String getName() {
        return _name;
    }

    public void setName(String value) {
        this._name = value;
    }

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        this._id = value;
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RegimenDto))
            return false;
        RegimenDto dto = (RegimenDto)o;
        return _id.equals(dto._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }
}
