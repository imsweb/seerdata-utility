## SEER Data Utility Library Version History

**Changes in version 1.16**

- Added new diagnosticConfirmation field to the Disease class.
- Updated XStream library from version 1.4.15 to 1.4.17.
- Updated JACKSON libraries from version 2.12.1 to version 2.12.4.

**Changes in version 1.15**

- Updated XStream library from version 1.4.11.1 to 1.4.15.
- Updated JACKSON libraries from version 2.10.2 to version 2.12.1.

**Changes in version 1.14**

- Updated XStream library from version 1.4.10 to 1.4.11.1.
- Updated JACKSON libraries from version 2.8.10 to version 2.10.2.

**Changes in version 1.13**

- Change a few methods in HematoDbUtils related to transformation; this was needed so this library can be used as a provider for MPH rules.

**Changes in version 1.12**

- Change a few methods in HematoDbUtils so they take two years as parameters instead of a single one.
- Removed support for reading/writing XML, JSON is the only format supported now.

**Changes in version 1.11**

- Forgot a JSON annotation in the regimen DTO!

**Changes in version 1.10**

- Added support for reading and writing drugs in JSON.

**Changes in version 1.9**

- Added "source" field to YearBasedDiseaseDto.

**Changes in version 1.8**

- Updated JACKSON libraries from version 2.4.4 to version 2.8.10. 

**Changes in version 1.7**

- Added a proper security environment to XStream by limiting the classes that it can create when loading XML files.
- Updated XStream library from version 1.4.7 to 1.4.10.

**Changes in version 1.6**

- Changed the HematoDB utility class to add methods to determine if one disease is acute or chronic transformation of the other.
- Changed ICD-O-1 and ICD-O-2 codes on the diseases to become a list of codes instead of a single code.
- Changed ICD-10CM codes to be tied to a full date (yyyy-MM-dd).

**Changes in version 1.5.1**

- Fixed a wrong mapping with XStreams that wrote some disease collections with the tag "string" instead of the actual one.

**Changes in version 1.5**

- Now using XStreams instead of JAXB for all XML-related operations.
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
- [SEER*Utils v4.1.1]  Solid Tumors- added treatment notes (new field).
- [SEER*Utils v3.0  ]  Fixed grade in Module field.
- [SEER*Utils v3.0  ]  Another fix for Heme data.
- [SEER*Utils v3.0  ]  Added DX method to disease 9680.
- [SEER*Utils v3.0  ]  Fixed wrong transformation in Heme data.
- [SEER*Utils v3.0  ]  Added missing same primaries info in HematoDB data.
- [SEER*Utils v3.0  ]  Added support several versions of the HematoDB data.
- [SEER*Utils v2.3.8]  Fixed NPE in multiple primary calculator method.
- [SEER*Utils v2.3.7]  Updated HematoDB data to version 2012-05-07.
- [SEER*Utils v2.3.7]  Changed Heme DB XML structure- split ICD9/10 codes and labels.
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



