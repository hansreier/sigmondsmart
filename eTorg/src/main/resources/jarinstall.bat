cd c:\git\work\eTorg\scr\main\resources
call mvn install:install-file -Dfile=db2jcc-10.5.jar -DgroupId=com.ibm.db2 -DartifactId=db2jcc -Dversion=10.5 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=nosqljson-1.0.jar -DgroupId=com.ibm.db2 -DartifactId=nosqljson -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true