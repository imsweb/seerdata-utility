/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.seerdata;

import java.io.File;

public class TestUtils {

    public static File getWorkingDirectory() {
        // have to do this to make the test run properly in IntelliJ...
        return new File(System.getProperty("user.dir").replace(".idea\\modules", ""));
    }

}
