/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;

import com.imsweb.seerdata.JsonUtils;
import com.imsweb.seerdata.SearchUtils;
import com.imsweb.seerdata.seerrx.json.DrugsDataJsonDto;
import com.imsweb.seerdata.seerrx.xml.DrugXmlDto;
import com.imsweb.seerdata.seerrx.xml.DrugsDataXmlDto;
import com.imsweb.seerdata.seerrx.xml.RegimenXmlDto;

/**
 * Use an instance of this class to access the utility methods on the SEER*Rx data.
 * <p/>
 * Created on May 8, 2012 by depryf
 * @author depryf
 */
public class SeerRxUtils {

    /**
     * Possible search types for the drugs, regimens and diseases
     */
    public enum SearchType {
        /**
         * <code>DRUGS_ONLY</code>
         */
        DRUGS_ONLY,

        /**
         * <code>REGIMENS_ONLY</code>
         */
        REGIMENS_ONLY,

        /**
         * <code>DRUGS_AND_REGIMENS</code>
         */
        DRUGS_AND_REGIMENS
    }

    /**
     * Unique instance of this class
     */
    private static SeerRxUtils _INSTANCE;

    /**
     * Data for this instance
     */
    protected Map<String, DrugDto> _drugs;
    protected Map<String, RegimenDto> _regimens;
    protected String _lastUpdated;
    protected String _dataStructureVersion;

    /**
     * Returns the unique instance of the SEER*Rx utility class.
     * <p/>
     * Created on May 8, 2012 by depryf
     * @return the unique instance of the SEER*Rx utility class
     */
    public static synchronized SeerRxUtils getInstance() {
        if (_INSTANCE == null)
            throw new RuntimeException("An instance must be registered using the registerInstance() method prior to calling getInstance()");
        return _INSTANCE;
    }

    /**
     * Registers an instance of the data.
     * <p/>
     * Created on May 8, 2012 by depryf
     * @param data the data to initialize from
     */
    public static void registerInstance(DrugsDataXmlDto data) {
        _INSTANCE = new SeerRxUtils(data);
    }

    /**
     * Returns true if an instance has been registered, false otherwise.
     * @return boolean
     */
    public static boolean isInstanceRegistered() {
        return _INSTANCE != null;
    }

    /**
     * Unregisters the current instance of the data.
     */
    public static void unregisterInstance() {
        _INSTANCE = null;
    }

    /**
     * Reads the drugs and regimens data from the provided URL, expects XML format.
     * <p/>
     * The provided stream will be closed when this method returns
     * <p/>
     * Created on Dec 21, 2010 by depryf
     * @param stream <code>InputStream</code> to the data file, cannot be null
     * @return a <code>DrugsDataXmlDto</code>, never null
     * @throws IOException
     */
    public static DrugsDataXmlDto readDrugsData(InputStream stream) throws IOException {
        if (stream == null)
            throw new IOException("Unable to read drugs, target input stream is null");

        try (InputStream is = stream) {
            return (DrugsDataXmlDto)createDrugsXStream().fromXML(is);
        }
        catch (RuntimeException e) {
            throw new IOException("Unable to read drugs", e);
        }
    }

    /**
     * Writes the drugs and regimens data to the provided URL, using XML format.
     * <p/>
     * Created on Dec 21, 2010 by depryf
     * @param stream <code>OutputStream</code> to the data file, cannot be null
     * @param data the <code>DrugsDataXmlDto</code> to write, cannot be null
     * @throws IOException
     */
    public static void writeDrugsData(OutputStream stream, DrugsDataXmlDto data) throws IOException {
        if (data == null)
            throw new IOException("Unable to write NULL drugs");
        if (stream == null)
            throw new IOException("Unable to write drugs, target output stream is null");

        try (Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write(System.lineSeparator());
            createDrugsXStream().toXML(data, writer);
        }
        catch (RuntimeException e) {
            throw new IOException("Unable to write drugs", e);
        }
    }

    private static XStream createDrugsXStream() {
        XStream xstream = new XStream(new PureJavaReflectionProvider(), new Xpp3Driver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out, "    ");
            }
        });
        xstream.autodetectAnnotations(true);
        xstream.alias("drugs", DrugsDataXmlDto.class);

        // setup proper security by limiting what classes can be loaded by XStream
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(new WildcardTypePermission(new String[] {"com.imsweb.seerdata.seerrx.xml.**"}));

        return xstream;
    }

    /**
     * Reads a DrugsDataJsonDto from the given stream
     * @param stream Stream to read data from
     * @return The DrugsDataJsonDto with data
     */
    public static DrugsDataJsonDto readJsonData(InputStream stream) throws IOException {
        try {
            return JsonUtils.getMapper().readValue(stream, DrugsDataJsonDto.class);
        }
        finally {
            stream.close();
        }
    }

    /**
     * Writes the given DrugsDataJsonDto to the stream
     * @param stream Stream to write the data to
     * @param dto Data to write
     */
    public static void writeJswonData(OutputStream stream, DrugsDataJsonDto dto) throws IOException {
        try {
            JsonUtils.getMapper().writeValue(stream, dto);
        }
        finally {
            stream.close();
        }
    }

    /**
     * Constructor.
     * <p/>
     * Created on May 8, 2012 by depryf
     * @param data the data to initialize from
     */
    SeerRxUtils(DrugsDataXmlDto data) {

        // drugs
        _drugs = new HashMap<>();
        for (DrugXmlDto dto : data.getDrug()) {
            DrugDto drug = new DrugDto();

            drug.getCategory().addAll(dto.getCategory());
            drug.getSubCategory().addAll(dto.getSubCategory());
            drug.getAbbreviation().addAll(dto.getAbbreviation());
            drug.getAlternateName().addAll(dto.getAlternateName());
            drug.getPrimarySite().addAll(dto.getPrimarySite());
            drug.getNscNum().addAll(dto.getNscNum());
            drug.setDoNotCode(dto.getDoNotCode());
            drug.setHistology(dto.getHistology());
            drug.setRemarks(dto.getRemarks());
            drug.setName(dto.getName());
            drug.setId(dto.getId());

            _drugs.put(drug.getId(), drug);
        }

        // regimens
        _regimens = new HashMap<>();
        for (RegimenXmlDto dto : data.getRegimen()) {
            RegimenDto regimen = new RegimenDto();

            regimen.getDrug().addAll(dto.getDrug());
            regimen.getAlternateName().addAll(dto.getAlternateName());
            regimen.getPrimarySite().addAll(dto.getPrimarySite());
            regimen.setHistology(dto.getHistology());
            regimen.setRemarks(dto.getRemarks());
            regimen.setRadiation(dto.getRadiation());
            regimen.setName(dto.getName());
            regimen.setId(dto.getId());

            _regimens.put(regimen.getId(), regimen);
        }

        // other properties
        _lastUpdated = data.getLastUpdated();
        _dataStructureVersion = data.getDataStructureVersion();
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
     * Returns teh version of the data structure.
     * @return version of the data structure
     */
    public String getDataStructureVersion() {
        return _dataStructureVersion;
    }

    /**
     * Returns all the drugs (keys are the drug internal identifiers, values are the drugs).
     * <p/>
     * Created on Nov 30, 2010 by depryf
     * @return a map of <code>String</code> : <code>DrugDto</code>
     */
    public Map<String, DrugDto> getAllDrugs() {
        return Collections.unmodifiableMap(_drugs);
    }

    /**
     * Returns all the regimens (keys are the regimen internal identifiers, values are the regimens).
     * <p/>
     * Created on Nov 30, 2010 by depryf
     * @return a map of <code>String</code> : <code>RegimenDto</code>
     */
    public Map<String, RegimenDto> getAllRegimens() {
        return Collections.unmodifiableMap(_regimens);
    }

    /**
     * Searches the drugs and/or the regimens using the provided query string.
     * <p/>
     * The search mode can have three values:
     * <ol>
     * <li><b>DRUGS_ONLY</b>: return only drug objects</li>
     * <li><b>REGIMENS_ONLY</b>: return only regimen object</li>
     * <li><b>DRUGS_AND_REGIMENS</b>: return a combination of drug and regimen object</li>
     * </ol><br>
     * <p/>
     * The provided query string is first split into tokens using spaces, the tokens are kept together if they are enclosed in double-quotes:<br>
     * &nbsp;&nbsp;&nbsp;A B C -> [A, B, C] (three different tokens)<br>
     * &nbsp;&nbsp;&nbsp;"A B C" -> [A B C] (one token)<br>
     * If the string is empty or null, it will not be split into tokens and an empty list of <code>DrugOrRegimenSearchResultDto</code> will be returned.<br><br>
     * Then the requested data (depending on the search mode) is weighted: each object is weighted individually for each token; the weight
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
     * </ul><br>
     * Here is the raw weight for each object type; the larger the weight the more important the field is in the matching process:
     * <h3>Drug</h3>
     * <ul>
     * <li>name: 100</li>
     * <li>alternate name: 50</li>
     * <li>primary site: 80</li>
     * <li>histology: 30</li>
     * <li>remarks: 30</li>
     * <li>abbreviation: 70</li>
     * <li>category: 50</li>
     * <li>subcategory: 30</li>
     * <li>NSC number: 80</li>
     * </ul>
     * <h3>Regimen</h3>
     * <ul>
     * <li>name: 100</li>
     * <li>alternate name: 50</li>
     * <li>primary site: 80</li>
     * <li>histology: 30</li>
     * <li>remarks: 30</li>
     * <li>radiation: 100</li>
     * <li>contained drug name: 10</li>
     * <li>contained drug NSC number: 10</li>
     * </ul>
     * <p/>
     * The final step is to sort the search results according to their final score.
     * <p/>
     * An exception will be thrown if the data has not been initialized.
     * <p/>
     * Created on Dec 23, 2010 by depryf
     * @param queryString the search query string, it will be split into multiple tokens if not empty or null (see <code>SeerToolsUtils</code>)
     * @param searchType search type (see <code>SearchType</code> enumeration)
     * @param searchMode search mode (and/or); seer <code>SearchMode</code>
     * @return a list of <code>DrugOrRegimenSearchResultDto</code>, maybe empty but never null; a <code>DrugOrRegimenSearchResultDto</code> is an object that wraps either a drug or a regimen (only one of those) and a corresponding score
     */
    public List<DrugOrRegimenSearchResultDto> searchDrugsAndRegimens(String queryString, SearchType searchType, SearchUtils.SearchMode searchMode) {
        List<DrugOrRegimenSearchResultDto> results = new ArrayList<>();

        // if the search string is empty or null then return the empty results list
        if (queryString == null || queryString.trim().isEmpty())
            return results;

        // split the search string (taking into account quoted values)
        List<String> searchTexts = SearchUtils.splitSearchString(queryString);

        // do we need to search the drugs?
        if (searchType == SearchType.DRUGS_ONLY || searchType == SearchType.DRUGS_AND_REGIMENS) {
            for (DrugDto dto : _drugs.values()) {
                int score = weightDrug(dto, searchTexts, searchMode);
                if (score > 0)
                    results.add(new DrugOrRegimenSearchResultDto(score, dto));

            }
        }

        // do we need to search the regimens?
        if (searchType == SearchType.REGIMENS_ONLY || searchType == SearchType.DRUGS_AND_REGIMENS) {
            for (RegimenDto dto : _regimens.values()) {
                int score = weightRegimen(dto, searchTexts, searchMode);
                if (score > 0)
                    results.add(new DrugOrRegimenSearchResultDto(score, dto));

            }
        }

        // sort the results by score
        Collections.sort(results, new Comparator<DrugOrRegimenSearchResultDto>() {
            @Override
            public int compare(DrugOrRegimenSearchResultDto o1, DrugOrRegimenSearchResultDto o2) {
                int scoreComp = o1.getScore().compareTo(o2.getScore());
                if (scoreComp != 0)
                    return scoreComp * -1;

                String n1;
                if (o1.getDrug() != null)
                    n1 = o1.getDrug().getName();
                else
                    n1 = o1.getRegimen().getName();

                String n2;
                if (o2.getDrug() != null)
                    n2 = o2.getDrug().getName();
                else
                    n2 = o2.getRegimen().getName();

                return n1.compareToIgnoreCase(n2);
            }
        });

        return results;
    }

    private int weightDrug(DrugDto dto, List<String> terms, SearchUtils.SearchMode searchMode) {
        int score = 0;

        score += SearchUtils.weightField(dto.getName(), 100, terms, searchMode);
        score += SearchUtils.weightField(dto.getAlternateName(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getPrimarySite(), 80, terms, searchMode);
        score += SearchUtils.weightField(dto.getHistology(), 30, terms, searchMode);
        score += SearchUtils.weightField(dto.getRemarks(), 30, terms, searchMode);
        score += SearchUtils.weightField(dto.getAbbreviation(), 70, terms, searchMode);
        score += SearchUtils.weightField(dto.getCategory(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getSubCategory(), 30, terms, searchMode);
        score += SearchUtils.weightField(dto.getNscNum(), 80, terms, searchMode);

        return score;
    }

    private int weightRegimen(RegimenDto dto, List<String> terms, SearchUtils.SearchMode searchMode) {
        int score = 0;

        score += SearchUtils.weightField(dto.getName(), 100, terms, searchMode);
        score += SearchUtils.weightField(dto.getAlternateName(), 50, terms, searchMode);
        score += SearchUtils.weightField(dto.getPrimarySite(), 80, terms, searchMode);
        score += SearchUtils.weightField(dto.getHistology(), 30, terms, searchMode);
        score += SearchUtils.weightField(dto.getRemarks(), 30, terms, searchMode);
        score += SearchUtils.weightField(dto.getRadiation(), 100, terms, searchMode);
        for (String drugId : dto.getDrug()) {
            DrugDto drug = _drugs.get(drugId);
            if (drug != null) {
                score += SearchUtils.weightField(drug.getName(), 10, terms, searchMode);
                score += SearchUtils.weightField(drug.getNscNum(), 10, terms, searchMode);
            }
        }

        return score;
    }
}
