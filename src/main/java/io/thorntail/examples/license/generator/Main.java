package io.thorntail.examples.license.generator;

import me.snowdrop.licenses.LicensesGenerator;
import me.snowdrop.licenses.LicensesGeneratorException;
import me.snowdrop.licenses.properties.GeneratorProperties;
import me.snowdrop.licenses.utils.Gav;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws LicensesGeneratorException, IOException {
        Path projectDir = Paths.get(Objects.requireNonNull(System.getProperty("example.project.dir"),
                "Missing 'example.project.dir' system property"));

        GeneratorProperties config = new ThorntailGeneratorProperties(isRedHat(projectDir));
        LicensesGenerator licensesGenerator = new LicensesGenerator(config);

        LOG.info("Building Maven project " + projectDir);
        buildMavenProject(projectDir, config);

        LOG.info("Finding all artifacts in Maven repositories inside Thorntail uberjars");
        Set<Gav> gavs = findAllArtifactsInAllThorntailUberjars(projectDir);

        LOG.info("Finding all Maven dependencies");
        gavs.addAll(licensesGenerator.findGavs().inMavenProject(projectDir.resolve("pom.xml")));

        gavs.stream()
                .sorted(Comparator.comparing(Gav::toString))
                .forEach(gav -> LOG.info(gav.toString()));

        LOG.info("Generating licenses");
        licensesGenerator.generateLicensesForGavs(gavs, projectDir.resolve("src").resolve("licenses").toString());
    }

    private static boolean isRedHat(Path projectDir) {
        return projectDir.getFileName().toString().endsWith("-redhat");
    }

    private static Set<Gav> findAllArtifactsInAllThorntailUberjars(Path projectDir) throws IOException {
        try (Stream<Path> dir = Files.walk(projectDir)) {
            return dir.filter(p -> Files.isRegularFile(p))
                    .filter(p -> p.toString().endsWith("-thorntail.jar"))
                    .flatMap(Main::findAllArtifactsInThorntailUberjar)
                    .collect(Collectors.toSet());
        }
    }

    private static Stream<Gav> findAllArtifactsInThorntailUberjar(Path uberjarPath) {
        LOG.info("Looking into " + uberjarPath);
        JavaArchive uberjar = ShrinkWrap.createFromZipFile(JavaArchive.class, uberjarPath.toFile());
        return uberjar.getContent()
                .values()
                .stream()
                .filter(node -> node.getPath().get().startsWith("/m2repo/"))
                .filter(node -> node.getAsset() != null)
                .map(node -> {
                    String groupId;
                    String artifactId;
                    String version;
                    String type;

                    String path = node.getPath().get();

                    type = path.substring(path.lastIndexOf('.') + 1);
                    path = path.substring("/m2repo/".length(), path.lastIndexOf('/'));

                    version = path.substring(path.lastIndexOf('/') + 1);
                    path = path.substring(0, path.lastIndexOf('/'));

                    artifactId = path.substring(path.lastIndexOf('/') + 1);
                    path = path.substring(0, path.lastIndexOf('/'));

                    groupId = path.replace('/', '.');

                    LOG.fine("Found " + groupId + ":" + artifactId + ":" + version + ":" + type);
                    return new Gav(groupId, artifactId, version, type);
                });
    }

    private static void buildMavenProject(Path projectDir, GeneratorProperties config) throws IOException {
        Path settingsXml = createSettingsXml(config);
        settingsXml.toFile().deleteOnExit();

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(projectDir.resolve("pom.xml").toFile());
        request.setGoals(Arrays.asList("clean", "install"));
        request.setMavenOpts("-DskipTests=true");
        request.setBatchMode(true);
        request.setUserSettingsFile(settingsXml.toFile());

        Invoker invoker = new DefaultInvoker();
        InvocationResult result;
        try {
            result = invoker.execute(request);
        } catch (MavenInvocationException e) {
            throw new IllegalStateException("Build failed.", e);
        }
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Build failed.");
        }
    }

    private static Path createSettingsXml(GeneratorProperties config) throws IOException {
        Path result = Files.createTempFile("thorntail-license-generator-maven-settings", ".xml");

        StringBuilder content = new StringBuilder()
                .append("<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n")
                .append("          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
                .append("          xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd\" >\n")
                .append("\n")
                .append("    <profiles>\n")
                .append("        <profile>\n")
                .append("            <id>license-generator</id>\n")
                .append("            <repositories>\n");
        for (Map.Entry<String, String> repo : config.getRepositories().entrySet()) {
            content
                    .append("                <repository>\n")
                    .append("                    <id>").append(repo.getKey()).append("</id>\n")
                    .append("                    <url>").append(repo.getValue()).append("</url>\n")
                    .append("                </repository>\n");
        }
        content
                .append("            </repositories>\n")
                .append("            <pluginRepositories>\n");
        for (Map.Entry<String, String> repo : config.getRepositories().entrySet()) {
            content
                    .append("                <pluginRepository>\n")
                    .append("                    <id>").append(repo.getKey()).append("</id>\n")
                    .append("                    <url>").append(repo.getValue()).append("</url>\n")
                    .append("                </pluginRepository>\n");
        }
        content
                .append("            </pluginRepositories>\n")
                .append("        </profile>\n")
                .append("    </profiles>\n")
                .append("    <activeProfiles>\n")
                .append("        <activeProfile>license-generator</activeProfile>\n")
                .append("    </activeProfiles>\n")
                .append("</settings>\n");

        Files.write(result, content.toString().getBytes(StandardCharsets.UTF_8));
        return result;
    }
}
