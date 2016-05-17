/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx;

import java.util.ArrayList;
import java.util.List;

public class DrugDto {

    protected List<String> _category;
    protected List<String> _subCategory;
    protected List<String> _abbreviation;
    protected List<String> _alternateName;
    protected List<String> _primarySite;
    protected List<String> _nscNum;
    protected String _doNotCode;
    protected String _remarks;
    protected String _histology;
    protected String _name;
    protected String _id;

    public List<String> getCategory() {
        if (_category == null) {
            _category = new ArrayList<>();
        }
        return this._category;
    }

    public List<String> getSubCategory() {
        if (_subCategory == null) {
            _subCategory = new ArrayList<>();
        }
        return this._subCategory;
    }

    public List<String> getAbbreviation() {
        if (_abbreviation == null) {
            _abbreviation = new ArrayList<>();
        }
        return this._abbreviation;
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

    public List<String> getNscNum() {
        if (_nscNum == null) {
            _nscNum = new ArrayList<>();
        }
        return _nscNum;
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

    public String getDoNotCode() {
        return _doNotCode;
    }

    public void setDoNotCode(String value) {
        this._doNotCode = value;
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
        if (!(o instanceof DrugDto))
            return false;
        DrugDto dto = (DrugDto)o;
        return _id.equals(dto._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }
}
