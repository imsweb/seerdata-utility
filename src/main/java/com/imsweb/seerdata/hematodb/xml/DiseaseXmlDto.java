
package com.imsweb.seerdata.hematodb.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("disease")
public class DiseaseXmlDto {

    @XStreamAsAttribute
    private String id;

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("code-icdO3")
    private String codeIcdO3;

    @XStreamAsAttribute
    @XStreamAlias("code-icdO3-effective")
    private String codeIcdO3Effective;

    @XStreamAsAttribute
    @XStreamAlias("code-icdO2")
    private String codeIcdO2;

    @XStreamAsAttribute
    @XStreamAlias("code-icdO2-effective")
    private String codeIcdO2Effective;

    @XStreamAsAttribute
    @XStreamAlias("code-icdO1")
    private String codeIcdO1;

    @XStreamAsAttribute
    @XStreamAlias("code-icdO1-effective")
    private String codeIcdO1Effective;

    @XStreamAsAttribute
    private String reportable;

    @XStreamAsAttribute
    private Integer grade;

    @XStreamAsAttribute
    private Boolean obsolete;

    @XStreamImplicit(itemFieldName = "obsolete-new")
    private List<String> obsoleteNew;

    @XStreamAlias("primary-site")
    private String primarySite;

    @XStreamImplicit(itemFieldName = "missing-primary-site-message")
    private List<String> missingPrimarySiteMessage;

    @XStreamAlias("primary-site-text")
    private String primarySiteText;

    @XStreamAlias("module-id")
    private String moduleId;

    @XStreamImplicit(itemFieldName = "alternate-name")
    private List<String> alternateName;

    private String definition;

    @XStreamAlias("abstractor-note")
    private String abstractorNote;

    @XStreamImplicit(itemFieldName = "diagnosis-method")
    private List<String> diagnosisMethod;

    @XStreamImplicit(itemFieldName = "genetics")
    private List<String> genetics;

    @XStreamImplicit(itemFieldName = "immunophenotype")
    private List<String> immunophenotype;

    @XStreamImplicit(itemFieldName = "treatment")
    private List<String> treatment;

    @XStreamImplicit(itemFieldName = "transform-to")
    private List<String> transformTo;

    @XStreamAlias("transform-to-text")
    private String transformToText;

    @XStreamImplicit(itemFieldName = "transform-from")
    private List<String> transformFrom;

    @XStreamAlias("transform-from-text")
    private String transformFromText;

    @XStreamImplicit(itemFieldName = "same-primary")
    private List<String> samePrimary;

    @XStreamAlias("same-primary-text")
    private String samePrimaryText;

    @XStreamImplicit(itemFieldName = "icd-9-code")
    private List<String> icd9Code;

    @XStreamImplicit(itemFieldName = "icd-10-code")
    private List<String> icd10Code;

    @XStreamImplicit(itemFieldName = "icd-10-cm-code")
    private List<String> icd10CmCode;

    @XStreamImplicit(itemFieldName = "sign")
    private List<String> sign;

    @XStreamImplicit(itemFieldName = "exam")
    private List<String> exam;

    @XStreamImplicit(itemFieldName = "progression")
    private List<String> progression;

    @XStreamImplicit(itemFieldName = "mortality")
    private List<String> mortality;

    public DiseaseXmlDto() {
        obsoleteNew = new ArrayList<>();
        missingPrimarySiteMessage = new ArrayList<>();
        alternateName = new ArrayList<>();
        diagnosisMethod = new ArrayList<>();
        genetics = new ArrayList<>();
        immunophenotype = new ArrayList<>();
        treatment = new ArrayList<>();
        transformTo = new ArrayList<>();
        transformFrom = new ArrayList<>();
        samePrimary = new ArrayList<>();
        icd9Code = new ArrayList<>();
        icd10Code = new ArrayList<>();
        icd10CmCode = new ArrayList<>();
        sign = new ArrayList<>();
        exam = new ArrayList<>();
        progression = new ArrayList<>();
        mortality = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeIcdO3() {
        return codeIcdO3;
    }

    public void setCodeIcdO3(String codeIcdO3) {
        this.codeIcdO3 = codeIcdO3;
    }

    public String getCodeIcdO3Effective() {
        return codeIcdO3Effective;
    }

    public void setCodeIcdO3Effective(String codeIcdO3Effective) {
        this.codeIcdO3Effective = codeIcdO3Effective;
    }

    public String getCodeIcdO2() {
        return codeIcdO2;
    }

    public void setCodeIcdO2(String codeIcdO2) {
        this.codeIcdO2 = codeIcdO2;
    }

    public String getCodeIcdO2Effective() {
        return codeIcdO2Effective;
    }

    public void setCodeIcdO2Effective(String codeIcdO2Effective) {
        this.codeIcdO2Effective = codeIcdO2Effective;
    }

    public String getCodeIcdO1() {
        return codeIcdO1;
    }

    public void setCodeIcdO1(String codeIcdO1) {
        this.codeIcdO1 = codeIcdO1;
    }

    public String getCodeIcdO1Effective() {
        return codeIcdO1Effective;
    }

    public void setCodeIcdO1Effective(String codeIcdO1Effective) {
        this.codeIcdO1Effective = codeIcdO1Effective;
    }

    public String getReportable() {
        return reportable;
    }

    public void setReportable(String reportable) {
        this.reportable = reportable;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Boolean getObsolete() {
        return obsolete;
    }

    public void setObsolete(Boolean obsolete) {
        this.obsolete = obsolete;
    }

    public List<String> getObsoleteNew() {
        return obsoleteNew;
    }

    public void setObsoleteNew(List<String> obsoleteNew) {
        this.obsoleteNew = obsoleteNew;
    }

    public String getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(String primarySite) {
        this.primarySite = primarySite;
    }

    public List<String> getMissingPrimarySiteMessage() {
        return missingPrimarySiteMessage;
    }

    public void setMissingPrimarySiteMessage(List<String> missingPrimarySiteMessage) {
        this.missingPrimarySiteMessage = missingPrimarySiteMessage;
    }

    public String getPrimarySiteText() {
        return primarySiteText;
    }

    public void setPrimarySiteText(String primarySiteText) {
        this.primarySiteText = primarySiteText;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<String> getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(List<String> alternateName) {
        this.alternateName = alternateName;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getAbstractorNote() {
        return abstractorNote;
    }

    public void setAbstractorNote(String abstractorNote) {
        this.abstractorNote = abstractorNote;
    }

    public List<String> getDiagnosisMethod() {
        return diagnosisMethod;
    }

    public void setDiagnosisMethod(List<String> diagnosisMethod) {
        this.diagnosisMethod = diagnosisMethod;
    }

    public List<String> getGenetics() {
        return genetics;
    }

    public void setGenetics(List<String> genetics) {
        this.genetics = genetics;
    }

    public List<String> getImmunophenotype() {
        return immunophenotype;
    }

    public void setImmunophenotype(List<String> immunophenotype) {
        this.immunophenotype = immunophenotype;
    }

    public List<String> getTreatment() {
        return treatment;
    }

    public void setTreatment(List<String> treatment) {
        this.treatment = treatment;
    }

    public List<String> getTransformTo() {
        return transformTo;
    }

    public void setTransformTo(List<String> transformTo) {
        this.transformTo = transformTo;
    }

    public String getTransformToText() {
        return transformToText;
    }

    public void setTransformToText(String transformToText) {
        this.transformToText = transformToText;
    }

    public List<String> getTransformFrom() {
        return transformFrom;
    }

    public void setTransformFrom(List<String> transformFrom) {
        this.transformFrom = transformFrom;
    }

    public String getTransformFromText() {
        return transformFromText;
    }

    public void setTransformFromText(String transformFromText) {
        this.transformFromText = transformFromText;
    }

    public List<String> getSamePrimary() {
        return samePrimary;
    }

    public void setSamePrimary(List<String> samePrimary) {
        this.samePrimary = samePrimary;
    }

    public String getSamePrimaryText() {
        return samePrimaryText;
    }

    public void setSamePrimaryText(String samePrimaryText) {
        this.samePrimaryText = samePrimaryText;
    }

    public List<String> getIcd9Code() {
        return icd9Code;
    }

    public void setIcd9Code(List<String> icd9Code) {
        this.icd9Code = icd9Code;
    }

    public List<String> getIcd10Code() {
        return icd10Code;
    }

    public void setIcd10Code(List<String> icd10Code) {
        this.icd10Code = icd10Code;
    }

    public List<String> getIcd10CmCode() {
        return icd10CmCode;
    }

    public void setIcd10CmCode(List<String> icd10CmCode) {
        this.icd10CmCode = icd10CmCode;
    }

    public List<String> getSign() {
        return sign;
    }

    public void setSign(List<String> sign) {
        this.sign = sign;
    }

    public List<String> getExam() {
        return exam;
    }

    public void setExam(List<String> exam) {
        this.exam = exam;
    }

    public List<String> getProgression() {
        return progression;
    }

    public void setProgression(List<String> progression) {
        this.progression = progression;
    }

    public List<String> getMortality() {
        return mortality;
    }

    public void setMortality(List<String> mortality) {
        this.mortality = mortality;
    }
}
