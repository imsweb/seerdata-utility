/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.seerdata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

    private static final DateFormat _DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Mapper for reading and writing JSON files
     */
    private static final ObjectMapper _MAPPER = new ObjectMapper();

    static {
        _DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

        //from seerapi-client-java
        _MAPPER.setSerializationInclusion(Include.NON_NULL);
        // set Date objects to output in readable customized format
        _MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        _MAPPER.setDateFormat(_DATE_FORMAT);

        //This is so that the mapper will only look at the variables when reading/writing
        _MAPPER.setVisibility(_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withSetterVisibility(Visibility.NONE)
                .withCreatorVisibility(Visibility.NONE));
    }

    public static ObjectMapper getMapper() {
        return _MAPPER;
    }
}
