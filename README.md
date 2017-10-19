# WildFly Swarm Booster License Generator

Example usage:

````bash
$ mvn clean package -Dbooster.pom.file=/path/to/wfswarm-rest-http/pom-redhat.xml -Dbooster.name="WildFly Swarm Booster - REST API Level 0 Mission" -Dbooster.version=7.0.0-redhat-3 -Dbooster.assembly.name=wfswarm-rest-http -Dbooster.product.build=rest-http
````

## booster.pom.file

Path to the booster `pom.xml`, e.g.:

````bash
-Dbooster.pom.file=/path/to/wfswarm-rest-http/pom-redhat.xml
````

NOTE: The pom file is always copied to `target/generated-project/pom.xml`.

## booster.project.dir

Path to the booster project directory, e.g.:

````bash
-Dbooster.project.dir=/path/to/wfswarm-rest-http-secured
````

NOTE 1: The dir content is copied to `target/generated-project`. `src/test/` and `target` subdirs are skipped.

NOTE 2: If specified, `booster.pom.file` property is ignored.

## booster.name

The name used in licenses.html report, e.g.:

````bash
-Dbooster.name="WildFly Swarm Booster - REST API Level 0 Mission"
````

## booster.version

The version used in licenses.html report, e.g.:

````bash
-Dbooster.version=7.0.0-redhat-3
````

## booster.assembly.name

The name of the generated archive, e.g.:

````bash
-Dbooster.assembly.name=wfswarm-rest-http
````

results in `target/wfswarm-rest-http-licenses.zip`.

## booster.repo.url

Additional repo used in `custom-settings.xml`.
https://repository.jboss.org/nexus/content/groups/public by default , https://maven.repository.redhat.com/ga/ for product builds (activated by `-Dbooster.product.build`).
