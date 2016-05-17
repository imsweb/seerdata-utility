/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import com.imsweb.seerdata.SearchUtils;
import com.imsweb.seerdata.SearchUtils.SearchMode;
import com.imsweb.seerdata.hematodb.json.YearBasedDataDto;
import com.imsweb.seerdata.hematodb.json.YearBasedDiseaseDto;
import com.imsweb.seerdata.hematodb.xml.DiseaseXmlDto;
import com.imsweb.seerdata.hematodb.xml.DiseasesDataXmlDto;

/**
 * Use an instance of this class to access the utility methods on the HematoDB data.
 * <p>
 * The year parameter for getInstance() is used to get a specific XML instance. It should be null to get the JSON instance.
 * The year parameter for the other methods is used to get results for a specific year. For XML data, this year must be the same as the applicable DX year.
 * <p/>
 * Created on May 8, 2012 by depryf
 * @author depryf
 */
public class HematoDbUtils {

    private static final DateFormat _DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Mapper for reading and writing JSON files
     */
    private static final ObjectMapper _MAPPER = new ObjectMapper();

    static {
        _DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

        //from seerapi-client-java
        _MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        _MAPPER.setSerializationInclusion(Include.NON_NULL);
        // set Date objects to output in readable customized format
        _MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        _MAPPER.setDateFormat(_DATE_FORMAT);

        //This is so that the mapper will only look at the variables when reading/writing
        _MAPPER.setVisibilityChecker(_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withSetterVisibility(Visibility.NONE)
                .withCreatorVisibility(Visibility.NONE));
    }

    /**
     * Cached instances per DX year
     */
    private static Map<Integer, HematoDbUtils> _INSTANCES = new HashMap<>();

    /**
     * Data for this instance
     */
    protected Map<String, YearBasedDiseaseDto> _yearBasedDiseases;
    protected Map<String, DiseaseDto> _diseases;
    protected Map<String, DiseaseDto> _diseasesPerYear;
    protected String _lastUpdated;
    protected String _dataStructureVersion;
    protected Integer _applicableDxYear;

    /**
     * Returns the instance of the utility class corresponding to the default version.
     * <br/><br/>
     * The default version is the one that doesn't corresponding to a specific DX year; note that such a version
     * is very inaccurate and has been deprecated. You should consider using only the DX-year-specific data.
     * Created on May 8, 2012 by depryf
     * @return the instance of the utility class corresponding to the default version
     */
    public static synchronized HematoDbUtils getInstance() {
        return getInstance(null);
    }

    /**
     * Returns the instance of the utility class corresponding to the requested DX year.
     * @param dxYear DX year of the instance to return, if null, the "default" version will be returned.
     * <br/><br/>
     * The default version is the one that doesn't corresponding to a specific DX year; note that such a version
     * is very inaccurate and has been deprecated. You should consider using only the DX-year-specific data
     * @return the requested instance, never null. An exception is thrown if the instance is not found
     */
    public static synchronized HematoDbUtils getInstance(Integer dxYear) {
        if (_INSTANCES == null)
            throw new RuntimeException("An instance must be registered using the registerInstance() method prior to calling getInstance()");
        return _INSTANCES.get(dxYear);
    }

    /**
     * Registers an instance of the data.
     * Created on Nov 27, 2013 by depryf
     * @param data data to register
     */
    public static void registerInstance(DiseasesDataXmlDto data) {
        _INSTANCES.put(data.getApplicableDxYear(), new HematoDbUtils(data));
    }

    /**
     * Registers an instance of the data
     * @param data data to register
     */
    public static void registerInstance(YearBasedDataDto data) {
        _INSTANCES.put(null, new HematoDbUtils(data));
    }

    /**
     * Returns true if a "default" instance has been registered, false otherwise.
     * <br/><br/>
     * The default version is the one that doesn't corresponding to a specific DX year; note that such a version
     * is very inaccurate and has been deprecated. You should consider using only the DX-year-specific data.
     * @return boolean
     */
    public static boolean isInstanceRegistered() {
        return isInstanceRegistered(null);
    }

    /**
     * Returns true if an instance has been registered for the provided dxYear, false otherwise.
     * @param dxYear DX year of the instance to check, can be null
     * @return boolean
     */
    public static boolean isInstanceRegistered(Integer dxYear) {
        return _INSTANCES.get(dxYear) != null;
    }

    /**
     * Unregisters the current "default" instance of the data.
     * <br/><br/>
     * The default version is the one that doesn't corresponding to a specific DX year; note that such a version
     * is very inaccurate and has been deprecated. You should consider using only the DX-year-specific data.
     */
    public static void unregisterInstance() {
        unregisterInstance(null);
    }

    /**
     * Unregisters the current "default" instance of the data.
     * @param dxYear DX year of the instance to unregister, can be null
     */
    public static void unregisterInstance(Integer dxYear) {
        _INSTANCES.remove(dxYear);
    }

    /**
     * Reads the HematoDB diseases data from the provided URL, expects XML format.
     * <p/>
     * The provided stream will be closed when this method returns
     * <p/>
     * Created on Dec 21, 2010 by depryf
     * @param stream <code>InputStream</code> to the data file, cannot be null
     * @return a <code>DiseasesDataXmlDto</code>, never null
     * @throws IOException
     */
    public static DiseasesDataXmlDto readDiseasesData(InputStream stream) throws IOException {
        if (stream == null)
            throw new IOException("Unable to read diseases, target input stream is null");

        try (InputStream is = stream) {
            return (DiseasesDataXmlDto)createDiseasesXStream().fromXML(is);
        }
        catch (RuntimeException e) {
            throw new IOException("Unable to read diseases", e);
        }
    }

    /**
     * Writes the HematoDB diseases data to the provided URL, using XML format.
     * <p/>
     * Created on Dec 21, 2010 by depryf
     * @param stream <code>OutputStream</code> to the data file, cannot be null
     * @param data the <code>DiseasesDataXmlDto</code> to write, cannot be null
     * @throws IOException
     */
    @SuppressWarnings("ConstantConditions")
    public static void writeDiseasesData(OutputStream stream, DiseasesDataXmlDto data) throws IOException {
        if (data == null)
            throw new IOException("Unable to write NULL diseases");
        if (stream == null)
            throw new IOException("Unable to write diseases, target output stream is null");

        try (Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write(System.lineSeparator());
            createDiseasesXStream().toXML(data, writer);
        }
        catch (RuntimeException e) {
            throw new IOException("Unable to write diseases", e);
        }
    }

    private static XStream createDiseasesXStream() {
        XStream xstream = new XStream(new PureJavaReflectionProvider(), new StaxDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out, "    ");
            }
        });
        xstream.autodetectAnnotations(true);
        xstream.alias("diseases", DiseasesDataXmlDto.class);
        return xstream;
    }

    /**
     * Reads a YearBasedDataDto from the given stream
     * @param stream Stream to read data from
     * @return The YearBasedDiseaseDto with data
     * @throws IOException
     */
    public static YearBasedDataDto readYearBasedDiseaseData(InputStream stream) throws IOException {
        YearBasedDataDto userData = _MAPPER.readValue(stream, YearBasedDataDto.class);
        stream.close();
        return userData;
    }

    /**
     * Writes the given YearBasedDtaDto to the stream
     * @param stream Stream to write the data to
     * @param dto Data to write
     */
    public static void writeYearBasedDiseaseData(OutputStream stream, YearBasedDataDto dto) throws IOException {
        _MAPPER.writeValue(stream, dto);
        stream.close();
    }

    /**
     * Constructor.
     * @param data Data to initialize from
     */
    HematoDbUtils(YearBasedDataDto data) {
        _yearBasedDiseases = new HashMap<>();
        for (YearBasedDiseaseDto dto : data.getDisease())
            _yearBasedDiseases.put(dto.getId(), dto);

        _dataStructureVersion = data.getDataStructureVersion();
        _lastUpdated = data.getLastUpdated();

        _diseasesPerYear = new LRUMap<>(500);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Nov 27, 2013 by depryf
     * @param data data data to initialize from
     */
    HematoDbUtils(DiseasesDataXmlDto data) {

        // diseases
        _diseases = new HashMap<>();
        for (DiseaseXmlDto dto : data.getDisease()) {
            DiseaseDto disease = new DiseaseDto();

            disease.getMortality().addAll(dto.getMortality());
            disease.getProgression().addAll(dto.getProgression());
            disease.getExam().addAll(dto.getExam());
            disease.getSign().addAll(dto.getSign());
            disease.getObsoleteNew().addAll(dto.getObsoleteNew());
            disease.setPrimarySite(dto.getPrimarySite());
            disease.setMissingPrimarySiteMessage(dto.getMissingPrimarySiteMessage());
            disease.setPrimarySiteText(dto.getPrimarySiteText());
            disease.setModuleId(dto.getModuleId());
            disease.setDefinition(dto.getDefinition());
            disease.getAlternateName().addAll(dto.getAlternateName());
            disease.getGenetics().addAll(dto.getGenetics());
            disease.getImmunophenotype().addAll(dto.getImmunophenotype());
            disease.getTransformFrom().addAll(dto.getTransformFrom());
            disease.setTransformFromText(dto.getTransformFromText());
            disease.getTransformTo().addAll(dto.getTransformTo());
            disease.setTransformToText(dto.getTransformToText());
            disease.getTreatment().addAll(dto.getTreatment());
            disease.getDiagnosisMethod().addAll(dto.getDiagnosisMethod());
            disease.setAbstractorNote(dto.getAbstractorNote());
            disease.getSamePrimary().addAll(dto.getSamePrimary());
            disease.setSamePrimaryText(dto.getSamePrimaryText());
            disease.getIcd9Code().addAll(dto.getIcd9Code());
            disease.getIcd10Code().addAll(dto.getIcd10Code());
            disease.getIcd10CmCode().addAll(dto.getIcd10CmCode());
            disease.setGrade(dto.getGrade());
            disease.setObsolete(dto.getObsolete());
            disease.setReportable(dto.getReportable());
            disease.setCodeIcdO1Effective(dto.getCodeIcdO1Effective());
            disease.setCodeIcdO2Effective(dto.getCodeIcdO2Effective());
            disease.setCodeIcdO3Effective(dto.getCodeIcdO3Effective());
            disease.setCodeIcdO1(dto.getCodeIcdO1());
            disease.setCodeIcdO2(dto.getCodeIcdO2());
            disease.setCodeIcdO3(dto.getCodeIcdO3());
            disease.setName(dto.getName());
            disease.setId(dto.getId());

            _diseases.put(disease.getId(), disease);
        }

        // other properties
        _lastUpdated = data.getLastUpdated();
        _dataStructureVersion = data.getDataStructureVersion();
        _applicableDxYear = data.getApplicableDxYear();
    }

    /**
     * Returns the data date last updated.
     * <p/>
     * Created on Nov 27, 2013 by depryf
     * @return the data date last updated
     */
    public String getDateLastUpdated() {
        return _lastUpdated;
    }

    /**
     * Returns the version of the data structure.
     * @return version of the data structure
     */
    public String getDataStructureVersion() {
        return _dataStructureVersion;
    }

    /**
     * Returns  the application DX year for this instance of the data.
     * @return application year, can be null
     */
    public Integer getApplicableDxYear() {
        return _applicableDxYear;
    }

    /**
     * Returns all the diseases (keys are the disease internal identifiers, values are the diseases).
     * <p/>
     * Created on Nov 30, 2010 by depryf
     * @return a map of <code>String</code> : <code>DiseaseDto</code>
     */
    public Map<String, DiseaseDto> getAllDiseases() {
        return Collections.unmodifiableMap(_diseases);
    }

    /**
     * @return Map of YearBasedDiseaseDtos, with the ids as keys
     */
    public Map<String, YearBasedDiseaseDto> getAllYearBasedDiseases() {
        return Collections.unmodifiableMap(_yearBasedDiseases);
    }

    /**
     * Searches the list of diseases for the current year
     * @param queryString the search query string, it will be split into multiple tokens if not empty or null
     * @param searchMode search mode (and/or)
     * @return List of DiseaseSearchResultDto
     */
    public List<DiseaseSearchResultDto> searchDiseases(String queryString, SearchMode searchMode) {
        return searchDiseases(queryString, searchMode, Calendar.getInstance().get(Calendar.YEAR));
    }

    /**
     * Searches the diseases using the provided query string.
     * <p/>
     * The provided query string is first split into tokens using spaces, the tokens are kept together if they are enclosed in double-quotes:<br>
     * &nbsp;&nbsp;&nbsp;A B C -> [A, B, C] (three different tokens)<br>
     * &nbsp;&nbsp;&nbsp;"A B C" -> [A B C] (one token)<br>
     * If the string is empty or null, it will not be split into tokens and an empty list of <code>DiseaseSearchResultDto</code> will be returned.<br><br>
     * Then the requested data is weighted: each object is weighted individually for each token; the weight
     * of each token is summed and the object is added to the results if that total weight is greater than 0.
     * <p/>
     * The search follows the following rules:
     * <ul>
     * <li>the matching is not case-sensitive</li>
     * <li>if a field is repeated (for example a drug can have many generic names), the weight of each individual sub-field is summed</li>
     * <li>if the entire field (or sub-field) matches the searched term, the raw weight is multiplied by 25</li>
     * <li>if the field (or sub-field) contains the full search term as a word, the raw weight is multiplied by 8</li>
     * <li>if the field (or sub-field) contains the search term as the starting of a word, the raw weight is multiplied by 4</li>
     * <li>if the field (or sub-field) contains the search terms, the raw weight is not multiplied</li>
     * </ul><br>
     * Here is the raw weight for each property; the larger the weight the more important the field is in the matching process:
     * <ul>
     * <li>name: 100</li>
     * <li>ICD-O-3 morphology code: 100</li>
     * <li>ICD-O-2 morphology code: 80</li>
     * <li>ICD-O-1 morphology code: 80</li>
     * <li>Primary Site: 80</li>
     * <li>ICD-9 code: 50</li>
     * <li>ICD-10 code: 50</li>
     * <li>ICD-10 CM code: 50</li>
     * <li>Definition: 50</li>
     * <li>Alternate Names: 30</li>
     * <li>Abstracting Note: 10</li>
     * <li>Treatment</li>: 10</li>
     * <li>Genetics: 10</li>
     * <li>Immunophenotype: 10</li>
     * <li>Diagnosis Method: 10</li>
     * <li>Sign: 10</li>
     * <li>Exam: 10</li>
     * <li>Recurrence: 10</li>
     * <li>Mortality: 10</li>
     * <li>Module ID: 10</li>
     * <li>Missing Primary Site Message: 5</li>
     * </ul>
     * <p/>
     * The final step is to sort the search results according to their final score.
     * <p/>
     * Created on Dec 23, 2010 by depryf
     * @param queryString the search query string, it will be split into multiple tokens if not empty or null (see <code>SeerToolsUtils</code>)
     * @param searchMode search mode (and/or); seer <code>SearchMode</code>
     * @param year the requested DX year, no result will be returned if this is null
     * @return a list of <code>DiseaseSearchResultDto</code>, maybe empty but never null; a <code>DiseaseSearchResultDto</code> is an object that wraps a disease and a corresponding score
     */
    public List<DiseaseSearchResultDto> searchDiseases(String queryString, SearchMode searchMode, Integer year) {
        List<DiseaseSearchResultDto> results = new ArrayList<>();

        //If the requested year doesn't match the data's applicable year, there can be no results
        if (_applicableDxYear != null && !_applicableDxYear.equals(year))
            throw new RuntimeException("The requested DX year does not match the applicable DX year.");

        // if the search string is empty or null then return the empty results list
        if (queryString == null || queryString.trim().isEmpty() || year == null)
            return results;

        // split the search string (taking into account quoted values)
        List<String> searchTexts = SearchUtils.splitSearchString(queryString);

        // calculate the weights
        if (_diseases != null) {
            for (DiseaseDto dto : _diseases.values()) {
                int score = weightDisease(dto, searchTexts, searchMode);
                if (score > 0)
                    results.add(new DiseaseSearchResultDto(score, dto));
            }
        }
        else if (_yearBasedDiseases != null) {
            for (YearBasedDiseaseDto dto : _yearBasedDiseases.values()) {
                DiseaseDto data = _diseasesPerYear.get(getLruId(dto.getId(), year));
                if (data == null) {
                    data = dto.getDiseaseDto(year);
                    _diseasesPerYear.put(getLruId(dto.getId(), year), data);
                }
                int score = weightDisease(data, searchTexts, searchMode);
                if (score > 0)
                    results.add(new DiseaseSearchResultDto(score, data));
            }
        }

        // sort the results by score
        Collections.sort(results, new Comparator<DiseaseSearchResultDto>() {
            @Override
            public int compare(DiseaseSearchResultDto o1, DiseaseSearchResultDto o2) {
                int scoreComp = o1.getScore().compareTo(o2.getScore());
                if (scoreComp != 0)
                    return scoreComp * -1;

                return o1.getDisease().getName().compareToIgnoreCase(o2.getDisease().getName());
            }
        });

        return results;
    }

    private int weightDisease(DiseaseDto dto, List<String> terms, SearchMode searchMode) {
        int score = 0;

        score += SearchUtils.weightField(dto.getName(), 100, terms, searchMode);
        score += SearchUtils.weightField(dto.getCodeIcdO3(), 100, terms, searchMode);
        score += SearchUtils.weightField(dto.getCodeIcdO2(), 80, terms, searchMode);
        score += SearchUtils.weightField(dto.getCodeIcdO1(), 80, terms, searchMode);
        score += SearchUtils.weightField(dto.getPrimarySite(), 80, terms, searchMode);
        score += SearchUtils.weightField(dto.getIcd9Code(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getIcd10Code(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getIcd10CmCode(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getDefinition(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getAlternateName(), 30, terms, searchMode);
        score += SearchUtils.weightField(dto.getPrimarySiteText(), 20, terms, searchMode);
        score += SearchUtils.weightField(dto.getAbstractorNote(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getTreatment(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getGenetics(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getImmunophenotype(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getDiagnosisMethod(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getSign(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getExam(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getProgression(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getMortality(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getModuleId(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getTransformFromText(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getTransformToText(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getSamePrimaryText(), 10, terms, searchMode);
        score += SearchUtils.weightField(dto.getMissingPrimarySiteMessage(), 5, terms, searchMode);

        return score;
    }

    /**
     * Returns true if the provided code is valid for calculating multiple primaries, false otherwise.
     * <p/>
     * Technical details: this methods just checks that an ICD-O-3 disease exists for the given code (codes usually have the format "9999/9")
     * Created on Dec 22, 2010 by depryf
     * @param code code to validate
     * @return true if the provided code is valid, false otherwise
     */
    public boolean isValidIcdCodeForMultiplePrimariesCalculation(String code) {
        if (code == null)
            return false;

        if (_diseases != null) {
            for (DiseaseDto disease : _diseases.values())
                if (code.equals(disease.getCodeIcdO3()))
                    return true;
        }
        else if (_yearBasedDiseases != null) {
            for (YearBasedDiseaseDto disease : _yearBasedDiseases.values())
                if (code.equals(disease.getIcdO3Morphology()))
                    return true;
        }

        return false;
    }

    /**
     * @param leftCode Left code to compare
     * @param rightCode Right code to compare
     * @return Returns true if the codes are cultiple primaries
     */
    public boolean isMultiplePrimaries(String leftCode, String rightCode) {
        return isMultiplePrimaries(leftCode, rightCode, Calendar.getInstance().get(Calendar.YEAR));
    }

    /**
     * Compares the two ICD-O-3 morphology codes and determine whether they are multiple primaries or not.
     * <p/>
     * Codes should have the format "9999/9".
     * <p/>
     * If any of the codes is not a valid code (see isValidIcdCodeForMultiplePrimariesCalculation()), true is returned (different primaries).
     * <p/>
     * If both codes are the same, false is returned (same primaries).
     * <p/>
     * Created on Dec 22, 2010 by depryf
     * @param leftCode left code to compare
     * @param rightCode right code to compare
     * @param year The requested DX year, cannot be null
     * @return true if the code are multiple primaries, false otherwise
     */
    public boolean isMultiplePrimaries(String leftCode, String rightCode, Integer year) {
        if (year == null)
            throw new RuntimeException("Year is required.");

        //If the requested year doesn't match the data's applicable year, there can be no results
        if (_applicableDxYear != null && !_applicableDxYear.equals(year))
            throw new RuntimeException("The requested DX year does not match the applicable DX year.");

        // get left disease
        if (leftCode == null)
            return true;
        leftCode = leftCode.trim();
        if (leftCode.isEmpty())
            return true;
        DiseaseDto leftDisease = null;
        if (_diseases != null) {
            for (DiseaseDto disease : _diseases.values()) {
                if (leftCode.equals(disease.getCodeIcdO3())) {
                    leftDisease = disease;
                    break;
                }
            }
        }
        else if (_yearBasedDiseases != null) {
            for (YearBasedDiseaseDto disease : _yearBasedDiseases.values()) {
                if (leftCode.equals(disease.getIcdO3Morphology())) {
                    leftDisease = _diseasesPerYear.get(getLruId(disease.getId(), year));
                    if (leftDisease == null) {
                        leftDisease = disease.getDiseaseDto(year);
                        _diseasesPerYear.put(getLruId(disease.getId(), year), leftDisease);
                    }
                    break;
                }
            }
        }
        if (leftDisease == null)
            return true;

        // get the right disease
        if (rightCode == null)
            return true;
        rightCode = rightCode.trim();
        if (rightCode.isEmpty())
            return true;
        DiseaseDto rightDisease = null;
        if (_diseases != null) {
            for (DiseaseDto disease : _diseases.values()) {
                if (rightCode.equals(disease.getCodeIcdO3())) {
                    rightDisease = disease;
                    break;
                }
            }
        }
        else if (_yearBasedDiseases != null) {
            for (YearBasedDiseaseDto disease : _yearBasedDiseases.values()) {
                if (rightCode.equals(disease.getIcdO3Morphology())) {
                    rightDisease = _diseasesPerYear.get(getLruId(disease.getId(), year));
                    if (rightDisease == null) {
                        rightDisease = disease.getDiseaseDto(year);
                        _diseasesPerYear.put(getLruId(disease.getId(), year), rightDisease);
                    }
                    break;
                }
            }
        }
        if (rightDisease == null)
            return true;

        if (leftDisease.equals(rightDisease))
            return false;

        return !leftDisease.getSamePrimary().contains(rightDisease.getId()) && !rightDisease.getSamePrimary().contains((leftDisease.getId()));
    }

    /**
     * Returns the key for the LRU map
     * @param id
     * @param year
     * @return
     */
    private String getLruId(String id, Integer year) {
        return id + "#" + year;
    }

    /**
     * LRU Map to cache DiseaseDto created from YearBasedDiseaseDto
     * Copied from seerutils SeerLRUCache
     * @param <A> Key
     * @param <B> Value
     */
    private static class LRUMap<A, B> extends LinkedHashMap<A, B> {

        private final int _maxEntries;

        /**
         * Constructor.
         * @param maxEntries the maximum number of entries to keep in the cache
         */
        public LRUMap(int maxEntries) {
            super(maxEntries + 1, 1.0f, true);
            _maxEntries = maxEntries;
        }

        /**
         * Returns <tt>true</tt> if this <code>LruCache</code> has more entries than the maximum specified when it was
         * created.
         * <p/>
         * <p>
         * This method <em>does not</em> modify the underlying <code>Map</code>; it relies on the implementation of
         * <code>LinkedHashMap</code> to do that, but that behavior is documented in the JavaDoc for
         * <code>LinkedHashMap</code>.
         * </p>
         * @param eldest <code>Entry</code> in question; this implementation doesn't care what it is, since the implementation is only dependent on the size of the cache
         * @return <tt>true</tt> if the oldest
         * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<A, B> eldest) {
            return size() > _maxEntries;
        }
    }
}
