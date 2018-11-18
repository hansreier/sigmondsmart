/**
This package contains the database services (interfaces) of the eTorg demo application.
The implementation is in a sub catalogue.
A database service is always implemented as a transaction and annotated with @Transactional.
The database services calls objects of the DAO layer within one transaction.
Error must be catched in the layer calling this database service layer, and not inside this layer.
@author Hans Reier Sigmond
@version 1.0
*/
package etorg.service;