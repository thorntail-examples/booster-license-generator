package io.thorntail.examples.license.generator;

import com.google.common.collect.ImmutableMap;
import me.snowdrop.licenses.properties.GeneratorProperties;

import java.util.Map;
import java.util.Optional;

public class ThorntailGeneratorProperties extends GeneratorProperties {
    private final boolean isRedHat;

    public ThorntailGeneratorProperties(boolean isRedHat) {
        this.isRedHat = isRedHat;
    }

    @Override
    public Map<String, String> getRepositories() {
        if (isRedHat) {
            return ImmutableMap.of(
                    "Maven Central", "https://repo.maven.apache.org/maven2/",
                    "Red Hat Maven Repository", "https://maven.repository.redhat.com/ga/"
            );
        } else {
            return ImmutableMap.of(
                    "Maven Central", "https://repo.maven.apache.org/maven2/",
                    "JBoss.org Maven Repository", "https://repository.jboss.org/nexus/content/groups/public"
            );
        }
    }

    @Override
    public Optional<String> getLicenseServiceUrl() {
        String licenseServiceUrl = System.getProperty("external.license.service");
        if (licenseServiceUrl == null || licenseServiceUrl.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(licenseServiceUrl);
    }

    @Override
    public String getAliasesFilePath() {
        return "license-aliases.json";
    }

    @Override
    public String getExceptionsFilePath() {
        return "license-exceptions.json";
    }
}
