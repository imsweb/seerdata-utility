/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerdata.hematodb;

/**
 * This class encapsulates a search result that contains a diseases.
 * <p/>
 * The score of this particular result can also be accessed.
 * <p/>
 * Created on Jan 28, 2011 by depryf
 * @author depryf
 */
public class DiseaseSearchResultDto {

    /**
     * Score for the result
     */
    private Integer _score;

    /**
     * Included disease (could be null)
     */
    private DiseaseDto _disease;

    /**
     * Constructor.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @param score score, cannot be null
     * @param disease <code>DiseaseDto</code>, cannot be null
     */
    public DiseaseSearchResultDto(Integer score, DiseaseDto disease) {
        _score = score;
        _disease = disease;
    }

    /**
     * Returns the score.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @return an <code>Integer</code>, never null
     */
    public Integer getScore() {
        return _score;
    }

    /**
     * Returns the disease associated with this result.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @return a <code>DiseaseDto</code>, could be null
     */
    public DiseaseDto getDisease() {
        return _disease;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); // all search object are unique (using identity equality)...
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        // this is what will be shown in the GUI, do not change this code...
        if (_disease != null)
            return _disease.getName();
        else
            return "?";
    }
}
