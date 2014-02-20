Cloudera Schema Validator
==========

The Cloudera Schema Validator helps to 
make sure parcel and CSD metadata files are valid.

Requirements
------------

* Java 6/7
* Maven 3 (to build)


Building the validator
----------------------

```bash
$ mvn install
```

Running the validator
---------------------

Building the validator creates an executable jar file. 
Make sure a java jre is installed and in the path.

```bash
$ java -jar target/validator.jar <arguments>
```

