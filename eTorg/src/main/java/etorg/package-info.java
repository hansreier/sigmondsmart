/** This application is a demo application containing an e market solution (eTorg)

This version enables calls to databases through Hibernate.
I have tested this version using the MySQL database.
MySQL must be installed and configured seperately.

The scope of most beans is set to request scope (except for the orderbacking bean).
View scope can also be used, but must be included as a customized Spring scope.
Logging facilities log4j is not added.
Exeption handling and user error messages (hibernate in particular) could be better.
Products to select from are hard coded examples in this version.
In this version the code works with either a stand alone Jetty 6 server,
or Jetty 6 included in Eclipse for developing / testing / demo purposes.
Plans are made to embed Jetty to be able to run the shopping basket without having to install 
software like Jetty and MySQL. This can be done with Jetty, but do not know how to do it with MySQL.
If MySQL is installed Hibernate will generate the required tables the first time eTorg is run.
The Eclipse (Helios) IDE can only be run with Jetty 6 embedded (not 7).
I also tried to run on Tomcat 7 and it works. 

The code is not for distribution unless agreed upon with the author.
@author Hans Reier Sigmond
mail: reier-s@online.no
mobile: +4791668863
@version 1.0
*/

package etorg;
