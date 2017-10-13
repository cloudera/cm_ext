Cloudera Schema Validator
==========

This is a command-line tool that helps to 
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
Make sure a Java JRE/SDK is installed and in the path.

```bash
$ java -jar target/validator.jar <mode> <filename> [options]
```

Command-line Arguments
--------------------
For the latest info see sources of com.cloudera.cli.validator.components.CommandLineOptions 

## Mode 

Exactly one of the following can be specified

| Mode Argument         | Description           |
|--------               |------                 |
| -sdl                  | The SDL to validate   |
| -mdl                  | The MDL to validate   |
| -parcel-json          | The parcel.json file to validate          |
| -alternatives-json    | The alternatives.json file to validate    |
| -permissions-json     | The permissions.json file to validate     |
| -manifest-json        | The manifest.json file to validate        |
| -parcel-dir           | The parcel directory to validate          |
| -parcel               | The parcel file to validate               |


## Additional options

| Option                | Description   |
|-------                |------------   |
| -service-type-list    | List containing additional valid service types, space separated  |
| -service-type-file    | A file which defines additional valid service types as per -service-type-list |
| -strict-mode          | Do strict validation of SDL and MDL files that rejects any unrecognized elements. Parcel files are always strictly validated |
