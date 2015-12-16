Cloudera Manager Extensions
===========================

Documentation and tools for creating Cloudera Manager extensions

* Tools are in this git repo
 * Validator for parcels and CSDs
 * make_manifest script to create a manifest for a parcel repository
* Documentation is in the [wiki](../../wiki)

Requirements
------------

* validator
  * Java 7
  * Maven 3 (to build)
* make_manifest
  * Python 2.7/3.3 or higher

Running the Validator
---------------------

Building the validator creates an executable jar file. Make sure a java jre 
is installed and in the path.

```bash
$ mvn install
$ cd validator
$ java -jar target/validator.jar <arguments>
```

Running make_manifest
---------------------

```bash
$ python make_manifest/make_manifest.py <path to directory>
```

All source in this repository is [Apache-Licensed](LICENSE.txt).

