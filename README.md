# Thorntail Booster License Generator

Example usage:

```bash
$ mvn clean package -Dbooster.project.dir=/path/to/thorntail-rest-http -Dbooster.name="Thorntail - REST API Level 0" -Dbooster.version=2.4.0-redhat-1 -Dbooster.assembly.name=thorntail-rest-http -Dbooster.product.build=true
```

## booster.project.dir

Path to the booster project directory, e.g.:

```bash
-Dbooster.project.dir=/path/to/thorntail-rest-http
```

NOTE: The dir content is copied to `target/generated-project`. `src/test/` and `target` subdirs are skipped.

## booster.name

The name used in licenses.html report, e.g.:

```bash
-Dbooster.name="Thorntail - REST API Level 0"
```

## booster.version

The version used in licenses.html report, e.g.:

```bash
-Dbooster.version=2.4.0-redhat-1
```

## booster.assembly.name

The name of the generated archive, e.g.:

```bash
-Dbooster.assembly.name=thorntail-rest-http
```

results in `target/thorntail-rest-http-licenses.zip`.

## booster.product.build

Boolean indicating whether this is a product variant of the booster or not, e.g.:

```bash
-Dbooster.product.build=true
```

## booster.repo.url

Additional repo used in `custom-settings.xml`.

Typically doesn't have to be set, as the correct value is inferred from `booster.product.build`:

- https://repository.jboss.org/nexus/content/groups/public if `booster.product.build` is not set, or is set to `false`
- https://maven.repository.redhat.com/ga/ when `booster.product.build` is set to `true`
