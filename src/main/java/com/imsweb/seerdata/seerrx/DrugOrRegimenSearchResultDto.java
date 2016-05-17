/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerdata.seerrx;

/**
 * This class encapsulates a search result that contains either a drug or a regimen (always only one of those).
 * <p/>
 * The score of this particular result can also be accessed.
 * <p/>
 * Created on Jan 28, 2011 by depryf
 * @author depryf
 */
public class DrugOrRegimenSearchResultDto {

    /**
     * Score for the result
     */
    private Integer _score;

    /**
     * Included drug (could be null)
     */
    private DrugDto _drug;

    /**
     * Included regimen (could be null)
     */
    private RegimenDto _regimen;

    /**
     * Constructor.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @param score score, cannot be null
     * @param drug <code>DrugXmlDto</code>, cannot be null
     */
    public DrugOrRegimenSearchResultDto(Integer score, DrugDto drug) {
        _score = score;
        _drug = drug;
    }

    /**
     * Constructor.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @param score score, cannot be null
     * @param regimen <code>RegimenXmlDto</code>, cannot be null
     */
    public DrugOrRegimenSearchResultDto(Integer score, RegimenDto regimen) {
        _score = score;
        _regimen = regimen;
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
     * Returns the drug associated with this result.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @return a <code>DrugXmlDto</code>, could be null
     */
    public DrugDto getDrug() {
        return _drug;
    }

    /**
     * Returns the regimen associated with this result.
     * <p/>
     * Created on Feb 17, 2011 by depryf
     * @return a <code>RegimenXmlDto</code>, could be null
     */
    public RegimenDto getRegimen() {
        return _regimen;
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
        if (_drug != null)
            return "[D] " + (_drug.getName());
        else if (_regimen != null)
            return "[R] " + _regimen.getName();
        else
            return "?";
    }
}
