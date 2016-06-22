/*
 * Copyright (C) 2015 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb.json;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.imsweb.seerdata.hematodb.DiseaseDto;

public class YearBasedDiseaseDto {

    /**
     * Year-based diseases have a type; some fields only exists for a specific type...
     */
    public enum Type {
        /**
         * Hematopoietic diseases
         */
        HEMATO,
        /**
         * Solid tumor diseases
         */
        SOLID_TUMOR
    }

    /**
     * Identifying information
     */
    @JsonProperty("icdO3_morphology")
    protected String _icdO3Morphology;
    @JsonProperty("primary_site")
    protected List<SiteRange> _primarySite;
    @JsonProperty("primary_site_text")
    protected String _primarySiteText;
    @JsonProperty("site_category")
    protected String _siteCategory;
    @JsonProperty("type")
    protected Type _type;

    /**
     * Obsolete ranges
     */
    @JsonProperty("valid")
    protected YearRange _valid;
    @JsonProperty("obsolete_new_code")
    protected List<String> _obsoleteNewCode;

    /**
     * Shared disease properties
     */
    @JsonProperty("reportable")
    protected List<YearRange> _reportable;
    @JsonProperty("abstractor_note")
    protected List<YearRangeString> _abstractorNote;
    @JsonProperty("treatment")
    protected List<YearRangeString> _treatment;
    @JsonProperty("genetics")
    protected List<YearRangeString> _genetics;
    @JsonProperty("alternate_name")
    protected List<YearRangeString> _alternateName;
    @JsonProperty("definition")
    protected List<YearRangeString> _definition;
    @JsonProperty("icdO2_morphology")
    protected List<String> _icdO2Morphology;
    @JsonProperty("icdO1_morphology")
    protected List<String> _icdO1Morphology;
    @JsonProperty("icd_10cm_code")
    protected List<DateRangeString> _icd10CmCode;
    @JsonProperty("icd_10_code")
    protected List<String> _icd10Code;
    @JsonProperty("icd_9_code")
    protected List<String> _icd9Code;
    @JsonProperty("signs")
    protected List<YearRangeString> _sign;
    @JsonProperty("exams")
    protected List<YearRangeString> _exam;
    @JsonProperty("progression")
    protected List<YearRangeString> _progression;
    @JsonProperty("mortality")
    protected List<YearRangeString> _mortality;

    /**
     * Morphology ranges
     */
    @JsonProperty("icdO3_effective")
    protected YearRange _icdO3Effective;
    @JsonProperty("icdO2_effective")
    protected YearRange _icdO2Effective;
    @JsonProperty("icdO1_effective")
    protected YearRange _icdO1Effective;

    /**
     * Hemato specific
     */
    @JsonProperty("missing_primary_site_message")
    protected List<YearRangeString> _missingPrimarySiteMessage;
    @JsonProperty("grade")
    protected List<YearRangeInteger> _grade;
    @JsonProperty("transform_to")
    protected List<YearRangeString> _transformTo;
    @JsonProperty("transform_to_text")
    protected List<YearRangeString> _transformToText;
    @JsonProperty("transform_from")
    protected List<YearRangeString> _transformFrom;
    @JsonProperty("transform_from_text")
    protected List<YearRangeString> _transformFromText;
    @JsonProperty("immunophenotype")
    protected List<YearRangeString> _immunophenotype;
    @JsonProperty("diagnosis_method")
    protected List<YearRangeString> _diagnosisMethod;
    @JsonProperty("module_id")
    protected List<YearRangeString> _moduleId;
    @JsonProperty("same_primary")
    protected List<YearRangeString> _samePrimaries;
    @JsonProperty("same_primaries_text")
    protected List<YearRangeString> _samePrimaryText;

    /**
     * Solid tumor specific
     */
    @JsonProperty("biomarkers")
    protected List<YearRangeString> _biomarkers;
    @JsonProperty("treatment_text")
    protected List<YearRangeString> _treatmentText;

    /**
     * Disease information
     */
    @JsonProperty("first_published")
    protected String _firstPublished;
    @JsonProperty("id")
    protected String _id;
    @JsonProperty("last_modified")
    protected String _lastModified;
    @JsonProperty("name")
    protected String _name;
    @JsonProperty("version")
    protected String _version;

    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public Integer getGrade(int year) {
        return getYearBasedInteger(year, _grade);
    }

    public void setGrade(List<YearRangeInteger> grade) {
        _grade = grade;
    }

    public List<String> getDiagnosisMethod(int year) {
        return getYearBasedStringList(year, _diagnosisMethod);
    }

    public void setDiagnosisMethod(List<YearRangeString> diagnosisMethod) {
        _diagnosisMethod = diagnosisMethod;
    }

    public String getIcdO3Morphology() {
        return _icdO3Morphology;
    }

    public void setIcdO3Morphology(String icdO3Morphology) {
        _icdO3Morphology = icdO3Morphology;
    }

    public String getPrimarySite() {
        StringBuilder buf = new StringBuilder();
        if (_primarySite != null)
            for (SiteRange range : _primarySite)
                buf.append(range).append(", ");
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 2);
            return buf.toString();
        }
        return null;
    }

    public void setPrimarySite(List<SiteRange> primarySite) {
        _primarySite = primarySite;
    }

    public String getPrimarySiteText() {
        return _primarySiteText;
    }

    public void setPrimarySiteText(String primarySiteText) {
        _primarySiteText = primarySiteText;
    }

    public String getSiteCategory() {
        return _siteCategory;
    }

    public void setSiteCategory(String siteCategory) {
        _siteCategory = siteCategory;
    }

    public Type getType() {
        return _type;
    }

    public void setType(Type type) {
        _type = type;
    }

    public YearRange getValid() {
        return _valid;
    }

    public void setValid(YearRange valid) {
        _valid = valid;
    }

    public List<String> getObsoleteNewCode() {
        return _obsoleteNewCode;
    }

    public void setObsoleteNewCode(List<String> obsoleteNewCode) {
        _obsoleteNewCode = obsoleteNewCode;
    }

    public String getReportable() {
        StringBuilder buf = new StringBuilder();
        if (_reportable != null)
            for (YearRange range : _reportable)
                buf.append(range).append(", ");
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 2);
            return buf.toString();
        }
        return null;
    }

    public void setReportable(List<YearRange> reportable) {
        _reportable = reportable;
    }

    public String getAbstractorNote(Integer year) {
        return getYearBasedString(year, _abstractorNote);
    }

    public void setAbstractorNote(List<YearRangeString> abstractorNote) {
        _abstractorNote = abstractorNote;
    }

    public List<String> getTreatment(Integer year) {
        return getYearBasedStringList(year, _treatment);
    }

    public void setTreatment(List<YearRangeString> treatment) {
        _treatment = treatment;
    }

    public List<String> getGenetics(Integer year) {
        return getYearBasedStringList(year, _genetics);
    }

    public void setGenetics(List<YearRangeString> genetics) {
        _genetics = genetics;
    }

    public List<String> getAlternateName(Integer year) {
        return getYearBasedStringList(year, _alternateName);
    }

    public void setAlternateName(List<YearRangeString> alternateName) {
        _alternateName = alternateName;
    }

    public String getDefinition(Integer year) {
        return getYearBasedString(year, _definition);
    }

    public void setDefinition(List<YearRangeString> definition) {
        _definition = definition;
    }

    public List<String> getIcdO2Morphology() {
        return _icdO2Morphology;
    }

    public void setIcdO2Morphology(List<String> icdO2Morphology) {
        _icdO2Morphology = icdO2Morphology;
    }

    public List<String> getIcdO1Morphology() {
        return _icdO1Morphology;
    }

    public void setIcdO1Morphology(List<String> icdO1Morphology) {
        _icdO1Morphology = icdO1Morphology;
    }

    public List<String> getIcd10CmCode(Integer year) {
        return getDateBasedStringList(year, _icd10CmCode);
    }

    public void setIcd10CmCode(List<DateRangeString> icd10CmCode) {
        _icd10CmCode = icd10CmCode;
    }

    public List<String> getIcd10Code() {
        return _icd10Code;
    }

    public void setIcd10Code(List<String> icd10Code) {
        _icd10Code = icd10Code;
    }

    public List<String> getIcd9Code() {
        return _icd9Code;
    }

    public void setIcd9Code(List<String> icd9Code) {
        _icd9Code = icd9Code;
    }

    public List<String> getSign(Integer year) {
        return getYearBasedStringList(year, _sign);
    }

    public void setSign(List<YearRangeString> sign) {
        _sign = sign;
    }

    public List<String> getExam(Integer year) {
        return getYearBasedStringList(year, _exam);
    }

    public void setExam(List<YearRangeString> exam) {
        _exam = exam;
    }

    public List<String> getProgression(Integer year) {
        return getYearBasedStringList(year, _progression);
    }

    public void setProgression(List<YearRangeString> progression) {
        _progression = progression;
    }

    public List<String> getMortality(Integer year) {
        return getYearBasedStringList(year, _mortality);
    }

    public void setMortality(List<YearRangeString> mortality) {
        _mortality = mortality;
    }

    public YearRange getIcdO3Effective() {
        return _icdO3Effective;
    }

    public void setIcdO3Effective(YearRange icdO3Effective) {
        _icdO3Effective = icdO3Effective;
    }

    public YearRange getIcdO2Effective() {
        return _icdO2Effective;
    }

    public void setIcdO2Effective(YearRange icdO2Effective) {
        _icdO2Effective = icdO2Effective;
    }

    public YearRange getIcdO1Effective() {
        return _icdO1Effective;
    }

    public void setIcdO1Effective(YearRange icdO1Effective) {
        _icdO1Effective = icdO1Effective;
    }

    public List<String> getMissingPrimarySiteMessage(Integer year) {
        return getYearBasedStringList(year, _missingPrimarySiteMessage);
    }

    public void setMissingPrimarySiteMessage(List<YearRangeString> missingPrimarySiteMessage) {
        _missingPrimarySiteMessage = missingPrimarySiteMessage;
    }

    public List<String> getTransformTo(Integer year) {
        return getYearBasedStringList(year, _transformTo);
    }

    public void setTransformTo(List<YearRangeString> transformTo) {
        _transformTo = transformTo;
    }

    public String getTransformToText(Integer year) {
        return getYearBasedString(year, _transformToText);
    }

    public void setTransformToText(List<YearRangeString> transformToText) {
        _transformToText = transformToText;
    }

    public List<String> getTransformFrom(Integer year) {
        return getYearBasedStringList(year, _transformFrom);
    }

    public void setTransformFrom(List<YearRangeString> transformFrom) {
        _transformFrom = transformFrom;
    }

    public String getTransformFromText(Integer year) {
        return getYearBasedString(year, _transformFromText);
    }

    public void setTransformFromText(List<YearRangeString> transformFromText) {
        _transformFromText = transformFromText;
    }

    public List<String> getImmunophenotype(Integer year) {
        return getYearBasedStringList(year, _immunophenotype);
    }

    public void setImmunophenotype(List<YearRangeString> immunophenotype) {
        _immunophenotype = immunophenotype;
    }

    public String getModuleId(Integer year) {
        return getYearBasedString(year, _moduleId);
    }

    public void setModuleId(List<YearRangeString> moduleId) {
        _moduleId = moduleId;
    }

    public List<String> getSamePrimary(Integer year) {
        return getYearBasedStringList(year, _samePrimaries);
    }

    public void setSamePrimary(List<YearRangeString> samePrimaries) {
        _samePrimaries = samePrimaries;
    }

    public String getSamePrimaryText(Integer year) {
        return getYearBasedString(year, _samePrimaryText);
    }

    public void setSamePrimaryText(List<YearRangeString> samePrimaryText) {
        _samePrimaryText = samePrimaryText;
    }

    public List<String> getBiomarkers(Integer year) {
        return getYearBasedStringList(year, _biomarkers);
    }

    public void setBiomarkers(List<YearRangeString> biomarkers) {
        _biomarkers = biomarkers;
    }

    public List<String> getTreatmentText(Integer year) {
        return getYearBasedStringList(year, _treatmentText);
    }

    public void setTreatmentText(List<YearRangeString> treatmentText) {
        _treatmentText = treatmentText;
    }

    public String getFirstPublished() {
        return _firstPublished;
    }

    public void setFirstPublished(String firstPublished) {
        _firstPublished = firstPublished;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getLastModified() {
        return _lastModified;
    }

    public void setLastModified(String lastModified) {
        _lastModified = lastModified;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    /**
     * Returns a list of the values from the DateRangeString list that are valid for the given year (it's considered valid in the whole year if it's valid at any time during that year)
     * @param year Year to get values for
     * @param list List of DateRangeString values
     * @return List of valid String values
     */
    private List<String> getDateBasedStringList(Integer year, List<DateRangeString> list) {
        List<String> result = new ArrayList<>();
        
        Pattern yearPattern = Pattern.compile("\\d\\d\\d\\d.+");

        if (year != null && list != null) {
            YearRange validYearRange = getValid();
            int validStart = validYearRange == null || validYearRange.getStartYear() == null ? 0 : validYearRange.getStartYear();
            int validEnd = validYearRange == null || validYearRange.getEndYear() == null ? 9999 : validYearRange.getEndYear();
            for (DateRangeString val : list) {
                if (val.getValue() != null) {
                    int start = val.getStartDate() == null || !yearPattern.matcher(val.getStartDate()).matches() ? validStart : Integer.valueOf(val.getStartDate().substring(0, 4));
                    int end = val.getEndDate() == null || !yearPattern.matcher(val.getEndDate()).matches() ? validEnd : Integer.valueOf(val.getEndDate().substring(0, 4));
                    if (year >= start && year <= end)
                        result.add(val.getValue());
                }
            }
        }

        return result;
    }
    
    /**
     * Returns a list of the values from the YearRangeString list that are valid for the given year
     * @param year Year to get values for
     * @param list List of YearRangeString values
     * @return List of valid String values
     */
    private List<String> getYearBasedStringList(Integer year, List<YearRangeString> list) {
        List<String> result = new ArrayList<>();

        if (year != null && list != null) {
            YearRange validYearRange = getValid();
            int validStart = validYearRange == null || validYearRange.getStartYear() == null ? 0 : validYearRange.getStartYear();
            int validEnd = validYearRange == null || validYearRange.getEndYear() == null ? 9999 : validYearRange.getEndYear();
            for (YearRangeString val : list) {
                if (val.getValue() != null) {
                    int start = val.getStartYear() == null ? validStart : val.getStartYear();
                    int end = val.getEndYear() == null ? validEnd : val.getEndYear();
                    if (year >= start && year <= end)
                        result.add(val.getValue());
                }
            }
        }

        return result;
    }

    /**
     * Returns an string value from the YearRangeString list that is valid for the given year.
     * @param year Year to get values for
     * @param list List of YearRangeString values
     * @return valid String value
     */
    private String getYearBasedString(Integer year, List<YearRangeString> list) {
        List<String> result = getYearBasedStringList(year, list);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Returns a list of the values from the YearRangeInteger list that are valid for the given year
     * @param year Year to get values for
     * @param list List of YearRangeInteger values
     * @return List of valid Integer values
     */
    private List<Integer> getYearBasedIntegerList(Integer year, List<YearRangeInteger> list) {
        List<Integer> result = new ArrayList<>();

        if (year != null && list != null) {
            YearRange validYearRange = getValid();
            int validStart = validYearRange == null || validYearRange.getStartYear() == null ? 0 : validYearRange.getStartYear();
            int validEnd = validYearRange == null || validYearRange.getEndYear() == null ? 9999 : validYearRange.getEndYear();
            for (YearRangeInteger val : list) {
                if (val.getValue() != null) {
                    int start = val.getStartYear() == null ? validStart : val.getStartYear();
                    int end = val.getEndYear() == null ? validEnd : val.getEndYear();
                    if (year >= start && year <= end)
                        result.add(val.getValue());
                }
            }
        }

        return result;
    }

    /**
     * Returns an integer value from the YearRangeInteger list that is valid for the given year.
     * @param year Year to get values for
     * @param list List of YearRangeInteger values
     * @return valid Integer value
     */
    private Integer getYearBasedInteger(Integer year, List<YearRangeInteger> list) {
        List<Integer> result = getYearBasedIntegerList(year, list);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Returns a DiseaseDto with data for the given year
     * @param year The year to get data for
     * @return DiseaseDto for a single year
     */
    public DiseaseDto getDiseaseDto(Integer year) {
        DiseaseDto disease = new DiseaseDto();

        disease.getMortality().addAll(getMortality(year));
        disease.getProgression().addAll(getProgression(year));
        disease.getExam().addAll(getExam(year));
        disease.getSign().addAll(getSign(year));
        if (getObsoleteNewCode() != null)
            disease.getObsoleteNew().addAll(getObsoleteNewCode());
        disease.setPrimarySite(getPrimarySite());
        disease.setMissingPrimarySiteMessage(getMissingPrimarySiteMessage(year));
        disease.setPrimarySiteText(getPrimarySiteText());
        disease.setModuleId(getModuleId(year));
        disease.setDefinition(getDefinition(year));
        disease.getAlternateName().addAll(getAlternateName(year));
        disease.getGenetics().addAll(getGenetics(year));
        disease.getImmunophenotype().addAll(getImmunophenotype(year));
        disease.getTransformFrom().addAll(getTransformFrom(year));
        disease.setTransformFromText(getTransformFromText(year));
        disease.getTransformTo().addAll(getTransformTo(year));
        disease.setTransformToText(getTransformToText(year));
        disease.getTreatment().addAll(getTreatment(year));
        disease.getDiagnosisMethod().addAll(getDiagnosisMethod(year));
        disease.setAbstractorNote(getAbstractorNote(year));
        disease.getSamePrimary().addAll(getSamePrimary(year));
        disease.setSamePrimaryText(getSamePrimaryText(year));
        if (getIcd9Code() != null)
            disease.getIcd9Code().addAll(getIcd9Code());
        if (getIcd10Code() != null)
            disease.getIcd10Code().addAll(getIcd10Code());
        disease.getIcd10CmCode().addAll(getIcd10CmCode(year));
        disease.setGrade(getGrade(year));
        if ((getValid().getStartYear() == null || getValid().getStartYear() <= year) && (getValid().getEndYear() == null || getValid().getEndYear() >= year))
            disease.setObsolete(false);
        else
            disease.setObsolete(true);
        disease.setReportable(getReportable());
        disease.setCodeIcdO1Effective(getIcdO1Effective() == null ? null : getIcdO1Effective().toString());
        disease.setCodeIcdO2Effective(getIcdO2Effective() == null ? null : getIcdO2Effective().toString());
        disease.setCodeIcdO3Effective(getIcdO3Effective() == null ? null : getIcdO3Effective().toString());
        if (getIcdO1Morphology() != null) {
            StringBuilder buf = new StringBuilder();
            for (String s : getIcdO1Morphology())
                buf.append(s).append(", ");
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 2);
                disease.setCodeIcdO1(buf.toString());
            }
        }
        if (getIcdO2Morphology() != null) {
            StringBuilder buf = new StringBuilder();
            for (String s : getIcdO2Morphology())
                buf.append(s).append(", ");
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 2);
                disease.setCodeIcdO2(buf.toString());
            }
        }
        disease.setCodeIcdO3(getIcdO3Morphology());
        disease.setName(getName());
        disease.setId(getId());

        return disease;
    }
}
