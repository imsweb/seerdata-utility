/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.imsweb.seerdata.JsonUtils;
import com.imsweb.seerdata.SearchUtils;
import com.imsweb.seerdata.SearchUtils.SearchMode;
import com.imsweb.seerdata.hematodb.json.YearBasedDataDto;
import com.imsweb.seerdata.hematodb.json.YearBasedDiseaseDto;

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

    private static final Pattern _CSV_LIST_PATTERN = Pattern.compile(",");

    /**
     * Cached instances per DX year
     */
    private static final Map<Integer, HematoDbUtils> _INSTANCES = new HashMap<>();

    /**
     * Data for this instance
     */
    protected Map<String, YearBasedDiseaseDto> _yearBasedDiseases; // diseases keyed by disease ID
    protected Map<String, DiseaseDto> _diseasesPerYear; // LRU cache for a view of a given disease for a specific year
    protected String _lastUpdated;
    protected String _dataStructureVersion;

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
     * Reads a YearBasedDataDto from the given stream
     * @param stream Stream to read data from
     * @return The YearBasedDiseaseDto with data
     */
    public static YearBasedDataDto readYearBasedDiseaseData(InputStream stream) throws IOException {
        YearBasedDataDto userData = JsonUtils.getMapper().readValue(stream, YearBasedDataDto.class);
        stream.close();
        return userData;
    }

    /**
     * Writes the given YearBasedDtaDto to the stream
     * @param stream Stream to write the data to
     * @param dto Data to write
     */
    public static void writeYearBasedDiseaseData(OutputStream stream, YearBasedDataDto dto) throws IOException {
        JsonUtils.getMapper().writeValue(stream, dto);
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

        // if the search string is empty or null then return the empty results list
        if (queryString == null || queryString.trim().isEmpty() || year == null)
            return results;

        // split the search string (taking into account quoted values)
        List<String> searchTexts = SearchUtils.splitSearchString(queryString);

        // calculate the weights
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

        // sort the results by score
        results.sort((o1, o2) -> {
            int scoreComp = o1.getScore().compareTo(o2.getScore());
            if (scoreComp != 0)
                return scoreComp * -1;

            return o1.getDisease().getName().compareToIgnoreCase(o2.getDisease().getName());
        });

        return results;
    }

    private int weightDisease(DiseaseDto dto, List<String> terms, SearchMode searchMode) {
        int score = 0;

        score += SearchUtils.weightField(dto.getName(), 100, terms, searchMode);
        score += SearchUtils.weightField(dto.getCodeIcdO3(), 100, terms, searchMode);
        score += SearchUtils.weightField(createListFromCsvString(dto.getCodeIcdO2()), 80, terms, searchMode);
        score += SearchUtils.weightField(createListFromCsvString(dto.getCodeIcdO1()), 80, terms, searchMode);
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

    private List<String> createListFromCsvString(String value) {
        if (value == null || value.isEmpty())
            return null;
        return Arrays.asList(_CSV_LIST_PATTERN.split(value));
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

        for (YearBasedDiseaseDto disease : _yearBasedDiseases.values())
            if (code.equals(disease.getIcdO3Morphology()))
                return true;

        return false;
    }

    /**
     * @param leftCode Left code to compare
     * @param rightCode Right code to compare
     * @return Returns true if the codes are multiple primaries using the current year
     * @deprecated Use {@code isMultiplePrimaries(String leftCode, String rightCode, Integer leftYear, Integer rightYear)}
     */
    @Deprecated
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
     * @param year The requested DX year, cannot be null; the year is used for both codes
     * @return true if the code are multiple primaries, false otherwise
     * @deprecated Use {@code isMultiplePrimaries(String leftCode, String rightCode, Integer leftYear, Integer rightYear)}
     */
    @Deprecated
    public boolean isMultiplePrimaries(String leftCode, String rightCode, Integer year) {
        return isMultiplePrimaries(leftCode, rightCode, year, year);
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
     * @param leftYear The left DX year, cannot be null
     * @param rightYear The right DX year, cannot be null
     * @return true if the code are multiple primaries, false otherwise
     */
    public boolean isMultiplePrimaries(String leftCode, String rightCode, Integer leftYear, Integer rightYear) {
        if (leftYear == null)
            throw new RuntimeException("Left year is required.");
        if (rightYear == null)
            throw new RuntimeException("Right year is required.");

        // get left disease
        if (leftCode == null)
            return true;
        leftCode = leftCode.trim();
        if (leftCode.isEmpty())
            return true;
        DiseaseDto leftDisease = null;
        for (YearBasedDiseaseDto disease : _yearBasedDiseases.values()) {
            if (leftCode.equals(disease.getIcdO3Morphology())) {
                leftDisease = _diseasesPerYear.get(getLruId(disease.getId(), leftYear));
                if (leftDisease == null) {
                    leftDisease = disease.getDiseaseDto(leftYear);
                    _diseasesPerYear.put(getLruId(disease.getId(), leftYear), leftDisease);
                }
                break;
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
        for (YearBasedDiseaseDto disease : _yearBasedDiseases.values()) {
            if (rightCode.equals(disease.getIcdO3Morphology())) {
                rightDisease = _diseasesPerYear.get(getLruId(disease.getId(), rightYear));
                if (rightDisease == null) {
                    rightDisease = disease.getDiseaseDto(rightYear);
                    _diseasesPerYear.put(getLruId(disease.getId(), rightYear), rightDisease);
                }
                break;
            }
        }
        if (rightDisease == null)
            return true;

        if (leftDisease.equals(rightDisease))
            return false;

        return !leftDisease.getSamePrimary().contains(rightDisease.getId()) && !rightDisease.getSamePrimary().contains((leftDisease.getId()));
    }

    /**
     * Compares the two ICD-O-3 morphology codes and determine whether the "from" code can transform to the "to" code. Uses the current year.
     * <p/>
     * Codes should have the format "9999/9".
     * <p/>
     * Created on July 6, 2016 by Sewbesew Bekele
     * @param fromCode "from" code
     * @param toCode "left" code
     * @return true if the left code can transform to the right code; uses the current year
     * @deprecated Use {@code canTransformTo(String leftCode, String rightCode, Integer leftYear, Integer rightYear)}
     */
    @Deprecated
    public boolean canTransformTo(String fromCode, String toCode) {
        return canTransformTo(fromCode, toCode, Calendar.getInstance().get(Calendar.YEAR));
    }

    /**
     * Compares the two ICD-O-3 morphology codes and determine whether the "from" code can transform to the "to" code.
     * <p/>
     * Codes should have the format "9999/9".
     * <p/>
     * Created on July 6, 2016 by Sewbesew Bekele
     * @param fromCode "from" code
     * @param toCode "left" code
     * @param year The requested DX year, cannot be null. This year is used for both codes.
     * @return true if the left code can transform to the right code
     * @deprecated Use {@code canTransformTo(String leftCode, String rightCode, Integer leftYear, Integer rightYear)}
     */
    @Deprecated
    public boolean canTransformTo(String fromCode, String toCode, Integer year) {
        return canTransformTo(fromCode, toCode, year, year);
    }

    /**
     * Compares the two ICD-O-3 morphology codes and determine whether the "from" code can transform to the "to" code.
     * <p/>
     * Codes should have the format "9999/9".
     * <p/>
     * Created on July 6, 2016 by Sewbesew Bekele
     * @param fromCode "from" code
     * @param toCode "left" code
     * @param fromYear The left DX year, cannot be null
     * @param toYear The right DX year, cannot be null
     * @return true if the left code can transform to the right code
     */
    public boolean canTransformTo(String fromCode, String toCode, Integer fromYear, Integer toYear) {
        if (fromYear == null)
            throw new RuntimeException("Left year is required.");
        if (toYear == null)
            throw new RuntimeException("Right year is required.");

        // get "from" disease
        if (fromCode == null)
            return false;
        fromCode = fromCode.trim();
        if (fromCode.isEmpty())
            return false;
        DiseaseDto fromDisease = null;
        for (YearBasedDiseaseDto disease : _yearBasedDiseases.values()) {
            if (fromCode.equals(disease.getIcdO3Morphology())) {
                fromDisease = _diseasesPerYear.get(getLruId(disease.getId(), fromYear));
                if (fromDisease == null) {
                    fromDisease = disease.getDiseaseDto(fromYear);
                    _diseasesPerYear.put(getLruId(disease.getId(), fromYear), fromDisease);
                }
                break;
            }
        }
        if (fromDisease == null)
            return false;

        // get "to" disease
        if (toCode == null)
            return false;
        toCode = toCode.trim();
        if (toCode.isEmpty())
            return false;
        DiseaseDto toDisease = null;
        for (YearBasedDiseaseDto disease : _yearBasedDiseases.values()) {
            if (toCode.equals(disease.getIcdO3Morphology())) {
                toDisease = _diseasesPerYear.get(getLruId(disease.getId(), toYear));
                if (toDisease == null) {
                    toDisease = disease.getDiseaseDto(toYear);
                    _diseasesPerYear.put(getLruId(disease.getId(), toYear), toDisease);
                }
                break;
            }
        }
        if (toDisease == null)
            return false;

        return fromDisease.getTransformTo().contains(toDisease.getId()) || toDisease.getTransformFrom().contains(fromDisease.getId());
    }

    /**
     * Returns the key for the LRU map
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
