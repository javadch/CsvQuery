CsvMonster
==========

**CsvMonster** is a Java library to query CSV and TSV files in an easy and fast manner. Using its API, client applications can define:

1. the fields as in the file. 
2. the data attributes (projection) as needed by the client application. The attributes can map directly to the filed or be any arbitrary transformation expression ro convert one (or more of the fields) to the desired attribute
3. the filtering criteria to eliminate non matching records. The filtering criteria work on the attributes not on the fields
4. the ordering of the result set by registering the attributes and the soring direction
5. the paging of the final resultset by setting skip and take values

The library uses the provided query constrcution information to create a customized data reader class and a minimalistic data object class. The reader is used to perform file operations, object materialization, filtering, ordering and limiting the result set. The data object is used as a row container so that each row of the result set is represented by one data object.
Both the classes are created and compiled on the fly, so the client application needs the JDK in addition to the JRE. The library has been developed based on the lambda expressions introduced in Java 8 and will not work on any previous version. There is a dependency upon [Rythm Template Engine](http://rythmengine.org)

Features I am working on:

1. Adding aggregation functions to the query construction API
2. Removing/ Minimizing the dependency (ies)
3. Adding metadata to the returned result to make its usage a little bit simpler. for example column names and their data types.

Note 1) This library is a part of a bigger work [XQt](https://github.com/javadch/XQt) I am doing towards my PhD, so it may not be following the best practices of programming. Also not designed for UI data binding and so on.
For example:
1. The data reader simply jumps over any record that does not have enough information, does not convert properly as mentioned in the attribute mapping and so on.
2. The data objects are not Plain Old Java Objects they are Naked Old (or maybe new) Java Objects, as they are using public fields and have not traditional getter/ setter methods. This is for performance reasons but may cause some automatic binding issues!
 
Note 2) Just as a matter of emphasize: THE SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED. Read the detailed information in the license file.