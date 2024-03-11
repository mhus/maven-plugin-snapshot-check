package de.mhus.lib.versioning;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MDate;
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
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

@Mojo(
        name = "next-release-version",
        defaultPhase = LifecyclePhase.VALIDATE,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        inheritByDefault = false,
        threadSafe = true)
public class NextReleaseVersion extends AbstractMojo {

    Logger log = Logger.getLogger(NextReleaseVersion.class.getCanonicalName());
    @Parameter(defaultValue = "simple", property = "type")
    String type;

    @Parameter(defaultValue = "target/release-version.txt", property = "outputFile")
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

        if (type.equals("date")) {
            version = version + "-" + MDate.toFileFormat(new Date());
        }

        if (type.equals("git")) {
            File headFile = new File(".git/HEAD");
            if (!headFile.exists())
                throw new MojoExecutionException("No git repository found");
            String head = MFile.readFile(headFile).split(" ",2 )[1].trim();
            File refFile = new File(".git/" + head);
            if (!refFile.exists())
                throw new MojoExecutionException("No git repository head found");
            String ref = MFile.readFile(refFile).trim();
            version = version + "-" + ref;
        }

        File output = new File(outputFile);
        output.getParentFile().mkdirs();
        log.info("Release version: " + version + " -> " + output.getAbsolutePath());
        MFile.writeFile(output, version);

    }

}
