/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb;

import java.util.ArrayList;
import java.util.List;

public class DiseaseDto {

    protected String _primarySite;
    protected List<String> _missingPrimarySiteMessage;
    protected String _primarySiteText;
    protected String _moduleId;
    protected String _definition;
    protected List<String> _alternateName;
    protected List<String> _genetics;
    protected List<String> _diagnosisMethod;
    protected List<String> _immunophenotype;
    protected List<String> _transformTo;
    protected String _transformToText;
    protected List<String> _transformFrom;
    protected String _transformFromText;
    protected List<String> _treatment;
    protected String _abstractorNote;
    protected List<String> _samePrimary;
    protected String _samePrimaryText;
    protected List<String> _icd9Code;
    protected List<String> _icd10Code;
    protected List<String> _icd10CmCode;
    protected List<String> _sign;
    protected List<String> _exam;
    protected List<String> _progression;
    protected List<String> _mortality;
    protected List<String> _diagnosticConfirmation;
    protected List<String> _obsoleteNew;
    protected Boolean _obsolete;
    protected String _codeIcdO1Effective;
    protected String _codeIcdO2Effective;
    protected String _codeIcdO3Effective;
    protected Integer _grade;
    protected String _reportable;
    protected String _codeIcdO1;
    protected String _codeIcdO2;
    protected String _codeIcdO3;
    protected String _name;
    protected String _id;

    public String getPrimarySite() {
        return _primarySite;
    }

    public void setPrimarySite(String value) {
        _primarySite = value;
    }

    public List<String> getMissingPrimarySiteMessage() {
        if (_missingPrimarySiteMessage == null) {
            _missingPrimarySiteMessage = new ArrayList<>();
        }
        return _missingPrimarySiteMessage;
    }

    public void setMissingPrimarySiteMessage(List<String> value) {
        _missingPrimarySiteMessage = value;
    }

    public String getPrimarySiteText() {
        return _primarySiteText;
    }

    public void setPrimarySiteText(String primarySiteText) {
        _primarySiteText = primarySiteText;
    }

    public String getModuleId() {
        return _moduleId;
    }

    public void setModuleId(String value) {
        _moduleId = value;
    }

    public String getDefinition() {
        return _definition;
    }

    public void setDefinition(String value) {
        _definition = value;
    }

    public List<String> getAlternateName() {
        if (_alternateName == null) {
            _alternateName = new ArrayList<>();
        }
        return _alternateName;
    }

    public List<String> getGenetics() {
        if (_genetics == null) {
            _genetics = new ArrayList<>();
        }
        return _genetics;
    }

    public List<String> getDiagnosisMethod() {
        if (_diagnosisMethod == null) {
            _diagnosisMethod = new ArrayList<>();
        }
        return _diagnosisMethod;
    }

    public List<String> getImmunophenotype() {
        if (_immunophenotype == null) {
            _immunophenotype = new ArrayList<>();
        }
        return _immunophenotype;
    }

    public List<String> getTransformTo() {
        if (_transformTo == null) {
            _transformTo = new ArrayList<>();
        }
        return _transformTo;
    }

    public String getTransformToText() {
        return _transformToText;
    }

    public void setTransformToText(String transformToText) {
        _transformToText = transformToText;
    }

    public List<String> getTransformFrom() {
        if (_transformFrom == null) {
            _transformFrom = new ArrayList<>();
        }
        return _transformFrom;
    }

    public String getTransformFromText() {
        return _transformFromText;
    }

    public void setTransformFromText(String transformFromText) {
        _transformFromText = transformFromText;
    }

    public List<String> getTreatment() {
        if (_treatment == null) {
            _treatment = new ArrayList<>();
        }
        return _treatment;
    }

    public String getAbstractorNote() {
        return _abstractorNote;
    }

    public void setAbstractorNote(String value) {
        _abstractorNote = value;
    }

    public List<String> getSamePrimary() {
        if (_samePrimary == null) {
            _samePrimary = new ArrayList<>();
        }
        return this._samePrimary;
    }

    public String getSamePrimaryText() {
        return _samePrimaryText;
    }

    public void setSamePrimaryText(String samePrimaryText) {
        _samePrimaryText = samePrimaryText;
    }

    public List<String> getIcd9Code() {
        if (_icd9Code == null) {
            _icd9Code = new ArrayList<>();
        }
        return _icd9Code;
    }

    public List<String> getIcd10Code() {
        if (_icd10Code == null) {
            _icd10Code = new ArrayList<>();
        }
        return _icd10Code;
    }

    public List<String> getIcd10CmCode() {
        if (_icd10CmCode == null) {
            _icd10CmCode = new ArrayList<>();
        }
        return _icd10CmCode;
    }

    public List<String> getSign() {
        if (_sign == null) {
            _sign = new ArrayList<>();
        }
        return _sign;
    }

    public List<String> getExam() {
        if (_exam == null) {
            _exam = new ArrayList<>();
        }
        return _exam;
    }

    public List<String> getProgression() {
        if (_progression == null) {
            _progression = new ArrayList<>();
        }
        return _progression;
    }

    public List<String> getMortality() {
        if (_mortality == null) {
            _mortality = new ArrayList<>();
        }
        return _mortality;
    }

    public List<String> getDiagnosticConfirmation() {
        if (_diagnosticConfirmation == null) {
            _diagnosticConfirmation = new ArrayList<>();
        }
        return _diagnosticConfirmation;
    }

    public List<String> getObsoleteNew() {
        if (_obsoleteNew == null) {
            _obsoleteNew = new ArrayList<>();
        }
        return _obsoleteNew;
    }

    public Boolean isObsolete() {
        return _obsolete;
    }

    public void setObsolete(Boolean value) {
        _obsolete = value;
    }

    public String getCodeIcdO1Effective() {
        return _codeIcdO1Effective;
    }

    public void setCodeIcdO1Effective(String value) {
        _codeIcdO1Effective = value;
    }

    public String getCodeIcdO2Effective() {
        return _codeIcdO2Effective;
    }

    public void setCodeIcdO2Effective(String value) {
        _codeIcdO2Effective = value;
    }

    public String getCodeIcdO3Effective() {
        return _codeIcdO3Effective;
    }

    public void setCodeIcdO3Effective(String value) {
        _codeIcdO3Effective = value;
    }

    public Integer getGrade() {
        return _grade;
    }

    public void setGrade(Integer value) {
        _grade = value;
    }

    public String getReportable() {
        return _reportable;
    }

    public void setReportable(String value) {
        _reportable = value;
    }

    public String getCodeIcdO1() {
        return _codeIcdO1;
    }

    public void setCodeIcdO1(String value) {
        _codeIcdO1 = value;
    }

    public String getCodeIcdO2() {
        return _codeIcdO2;
    }

    public void setCodeIcdO2(String value) {
        _codeIcdO2 = value;
    }

    public String getCodeIcdO3() {
        return _codeIcdO3;
    }

    public void setCodeIcdO3(String value) {
        _codeIcdO3 = value;
    }

    public String getName() {
        return _name;
    }

    public void setName(String value) {
        _name = value;
    }

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        _id = value;
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DiseaseDto))
            return false;
        DiseaseDto dto = (DiseaseDto)o;
        return _id.equals(dto._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }
}
