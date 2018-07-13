# MyStore
This is a distributed in-memory  map.

## Building the project
for compiling a fat jar: 
```
$ mvn clean compile assembly:single
```


## Automatic test generation
```
$ mvn -DmemoryInMB=2000 -Dcores=2 evosuite:generate evosuite:export  test
```
