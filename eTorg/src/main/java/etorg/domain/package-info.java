/**
This package contains the domain model classes of the eTorg demo application.
The contents in this demo version is only temporary and not complete as a domain model.
The domain model is used directly by the database services.
JPA annotations and some hibernate extended annotations are used.
The domain model classes are also contained in the GUI backing beans.
The purpose of this is mainly to avoid defining classes and fields twice. 
Some methods and attributes are only used by the GUI and not by the database layer.
These must be marked with the @Transient annotation.

@author Hans Reier Sigmond
@version 1.0
*/
package etorg.domain;