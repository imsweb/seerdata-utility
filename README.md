# SEER Data Utility Library

[![Build Status](https://travis-ci.org/imsweb/layout.svg?branch=master)](https://travis-ci.org/imsweb/seerdata-utility)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.imsweb/layout/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.imsweb/seerdata-utility)

This project contains the Java entities and search utility methods for the HematoDB diseases and SEER*Rx drugs and regimens.

The library does NOT contain the actual data, only the entities to handle it. The data can be downloaded from the [SEER API](https://api.seer.cancer.gov).

## Features

* Reading/writing drugs and regimens from/to XML and searching that data using a weighted algorithm.
* Reading/writing diseases from/to XML for a given DX year and searching that data using a weighted algorithm.
* Reading/writing year-based diseases from/to JSON and searching that data using a weighted algorithm.

## Download

The library is available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.imsweb%22%20AND%20a%3A%seerdata-utility%22).

To include it to your Maven or Gradle project, use the group ID `com.imsweb` and the artifact ID `seerdata-utility`.

You can check out the [release page](https://github.com/imsweb/seerdata-utility/releases) for a list of the releases and their changes.

## Usage

Checkout the SeerRxUtils class for the drugs/regimens and the HematoDbUtils class for the diseases.
 
## About SEER

This library was developed through the [SEER](http://seer.cancer.gov/) program.

The Surveillance, Epidemiology and End Results program is a premier source for cancer statistics in the United States.
The SEER program collects information on incidence, prevalence and survival from specific geographic areas representing
a large portion of the US population and reports on all these data plus cancer mortality data for the entire country.