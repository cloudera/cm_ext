Cloudera Schema Validator Maven Plugin
==========

The Cloudera Schema Validator Maven Plugin helps to 
make sure CSD metadata files are valid.

Requirements
------------

* Java 6/7
* Maven 3


Building the validator plugin
-----------------------------

```bash
$ mvn install
```

Using the validator in pom.xml
------------------------------

```xml
  <!-- ... -->
  <build>
    <plugins>
      <plugin>
        <groupId>com.cloudera.enterprise</groupId>
        <artifactId>schema-validator-maven-plugin</artifactId>
        <version>5.3.0</version>
        <executions>
          <execution>
            <phase>test</phase>
            <goals>
              <goal>validate</goal>
            </goals>
            <configuration>
              <sourceDirectory>src</sourceDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  <!-- ... -->
```