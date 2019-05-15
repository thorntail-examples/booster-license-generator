# Thorntail Examples License Generator

Example usage:

```bash
$ mvn clean package -Dexample.project.dir=/path/to/thorntail-rest-http
```

## `example.project.dir`

Path to the example project directory, e.g.:

```bash
-Dexample.project.dir=/path/to/thorntail-rest-http
```

## `external.license.service` (optional)

URL of an external license service that can provide license information.
If not set, no external license service is used.

```bash
-Dexternal.license.service=http://.../find-license-check-record-and-license-info
```

## Community and product variants of the example

The license generator will automatically determine whether the example is a product variant or not, based on the project directory name.
If the directory name ends with `-redhat`, it's considered product variant, otherwise community.
This information is used for selecting which Maven repository should be used (JBoss.org Maven repo or Red Hat Maven repo).

## Snowdrop license generator

This project uses the awesome [Snowdrop license generator](https://github.com/snowdrop/licenses-generator).
Be sure to check it out!
