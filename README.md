# MyStore
This is a distributed in-memory  map.


/// for compilling a fat jar
mvn clean compile assembly:single

/// for automatic test generation
mvn -DmemoryInMB=2000 -Dcores=2 evosuite:generate evosuite:export  test
