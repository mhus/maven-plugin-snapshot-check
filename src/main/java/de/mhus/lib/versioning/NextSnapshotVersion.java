package de.mhus.lib.versioning;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MString;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(
        name = "next-snapshot-version",
        defaultPhase = LifecyclePhase.VALIDATE,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        inheritByDefault = false,
        threadSafe = true)
public class NextSnapshotVersion extends AbstractMojo  {
    @Parameter(defaultValue = "major")
    String type;

    @Parameter(defaultValue = "target/snapshot-version.txt")
    String outputFile;

    @Parameter(defaultValue = "${project}")
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String version = project.getVersion();
        if (version == null)
            throw new MojoExecutionException("Version not found");

        if (MString.isIndex(version, "-")) {
            version  = MString.beforeIndex(version, "-");
        }

        if (type.equals("bugfix")) {
            String[] parts = version.split("\\.");
            version = parts[0] + "." + parts[1] + "." + (Integer.parseInt(parts[2]) + 1) + "-SNAPSHOT";
        } else
        if (type.equals("minor")) {
            String[] parts = version.split("\\.");
            version = parts[0] + "." + (Integer.parseInt(parts[1]) + 1) + ".0-SNAPSHOT";
        } else
        if (type.equals("major")) {
            String[] parts = version.split("\\.");
            version = (Integer.parseInt(parts[0]) + 1) + ".0.0-SNAPSHOT";
        } else if (type.equals("none")) {
            version = version + "-SNAPSHOT";
        } else {
            throw new MojoExecutionException("Unknown type: " + type);
        }

        File output = new File(outputFile);
        MFile.writeFile(output, version);

    }
}
