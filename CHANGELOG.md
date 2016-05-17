## SEER Data Utility Library Version History

**Changes in version 1.5**

 - Fixed some getters from the YearBasedDiseaseDto class so they return a single String instead of a list of String to agree with the XML data model.
 - Move all year-based entities into a "json" package.
 - Renamed package "com.imsweb.data" into "com.imsweb.seerdata"; made this change as part of moving the library to GitHub.

**Changes in version 1.4**

 - Added support for year-based disease entities and for reading/writing year-based heme data from JSON files.
 - Renamed field 'recurrence' into 'progression' on disease.
 - Changed field 'doNotCode' from Boolean to String on drug.

**Changes in version 1.3**

 - Changed the HematoDB utility class to handle the data per DX year; it is still compatible with a set of data for all years, but that will be deprecated eventually.

**Changes in version 1.2**

 - Fixed an exception in Multiple Primaries Calculator due to the fact that some diseases don't have an ICD-O-3 code in the new data.

**Changes in version 1.1**

 - Changed the missing site message from a single string to a list of string.

**Changes in version 1.0**

 - Utility library split from SEER*Utils into its own project.

**Legacy changes**

 - [SEER*Utils v4.9  ]  Moved all related classes from "com.imsweb.seerutils.tools" to "com.imsweb.data". Moved utilities methods from SeerToolsUtils to SearchUtils.
 - [SEER*Utils v4.9  ]  Removed HematoDB and SEER*Rx XML data files; those are now available on the SEER website.
 - [SEER*Utils v4.9  ]  Added primary-site-text, transform-to-text, transform-from-text and same-primary-text fields to the DiseaseDto entity.
 - [SEER*Utils v4.9  ]  Changed the data structure of the SEER*Rx drug and regimen objects; removed the diseases and drugs XML data file since they became stale too quickly and required too many releases of the software.
 - [SEER*Utils v4.8  ]  Updated HematoDB data from the SEER API website.
 - [SEER*Utils v4.7  ]  Added some missing fields to the HeamtoDB diseases; there were other changes to that module since the data now comes from SEER API.
 - [SEER*Utils v4.5.3]  Modified the survival time calculation according to the new requirements released on the SEER website.
 - [SEER*Utils v4.5.1]  Updated SEER*Rx data to version 2013-08-01.
 - [SEER*Utils v4.2.9]  Updated HematoDB 2010 data to 2013-02-05.
 - [SEER*Utils v4.2.8]  New Heme data from NCI.
 - [SEER*Utils v4.2.8]  Added new field in Heme data for missing site message.
 - [SEER*Utils v4.2.7]  Updated SEER*Rx data to version 2013-01-18.
 - [SEER*Utils v4.2.4]  New Solid Tumors Data.
 - [SEER*Utils v4.2.3]  Updated HematoDB 2012 data with latest version provided by NCI.
 - [SEER*Utils v4.2.3]  Fixed missing same primary combination in HematoDB 2010 data.
 - [SEER*Utils v4.1.1]  Solid Tumors - added treatment notes (new field).
 - [SEER*Utils v3.0  ]  Fixed grade in Module field.
 - [SEER*Utils v3.0  ]  Another fix for Heme data.
 - [SEER*Utils v3.0  ]  Added DX method to disease 9680.
 - [SEER*Utils v3.0  ]  Fixed wrong transformation in Heme data.
 - [SEER*Utils v3.0  ]  Added missing same primaries info in HematoDB data.
 - [SEER*Utils v3.0  ]  Added support several versions of the HematoDB data.
 - [SEER*Utils v2.3.8]  Fixed NPE in multiple primary calculator method.
 - [SEER*Utils v2.3.7]  Updated HematoDB data to version 2012-05-07.
 - [SEER*Utils v2.3.7]  Changed Heme DB XML structure - split ICD9/10 codes and labels.
 - [SEER*Utils v2.3.6]  Updated HematoDB data to version 2012-05-03.
 - [SEER*Utils v2.3.2]  Added Grade field to HematoDB data.
 - [SEER*Utils v2.3.1]  Updated SEER*Rx data to version 2012-03-19.
 - [SEER*Utils v2.3  ]  Updated SEER*Rx data to version 2012-03-12.
 - [SEER*Utils v2.3  ]  Fixed bug in calculateHighlighting method.
 - [SEER*Utils v2.3  ]  Made some modification to the seertools search algorithms.
 - [SEER*Utils v2.2  ]  Providing twice the same histology should return SAME PRIMARY.
 - [SEER*Utils v2.2  ]  Fixed bug in one of the SEER*Rx tags.
 - [SEER*Utils v2.2  ]  Added new field to solid tumors: subsites.
 - [SEER*Utils v2.2  ]  Removed extra fields from disease object.
 - [SEER*Utils v2.1  ]  Added solid tumor data.
 - [SEER*Utils v2.1  ]  Renamed some methods in SeerToolsData.
 - [SEER*Utils v1.2  ]  Simplified data structure of XML data file for the HematDB diseases.
 - [SEER*Utils v1.0  ]  Added NSC to the searched fields for regimens.
 - [SEER*Utils v1.0  ]  Removed Chemical Name from drugs.



