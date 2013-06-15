/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.photorenametool.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk44.photorenametool.util.Action;

/**
 *
 * @author sk
 */
public class PhotoDirectoryWalker {

    private static int MAX_DIRECTORY_DEPTH = 1;
    private static final Logger logger = LoggerFactory.getLogger(PhotoDirectoryWalker.class);

    public static PhotoDirectoryWalker create(String sourceDirectoryName,
        String outputDirectoryName, String prefix, int timeDifference, Action<String> notifyMessage) {

        if (StringUtils.isBlank(sourceDirectoryName) || StringUtils.isBlank(outputDirectoryName)) {
            throw new IllegalArgumentException("source dir and output dir must not blank.");
        }
        Path sourceDirPath = new File(sourceDirectoryName).toPath();
        Path outputPath = new File(outputDirectoryName).toPath();
        return new PhotoDirectoryWalker(sourceDirPath, outputPath, prefix, timeDifference, notifyMessage);
    }
    private final Path sourceDirectory;
    private final Path outputDirectory;
    private final String prefix;
    private final int timeDifference;
    private final Action<String> notifyMessage;

    public PhotoDirectoryWalker(Path sourceDirectory, Path outputDirectory, String prefix, int timeDifference, Action<String> notifyMessage) {
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
        this.prefix = prefix;
        this.timeDifference = timeDifference;
        this.notifyMessage = notifyMessage;
    }

    public void execute() {
        try {
            if (Files.exists(outputDirectory) == false) {
                Files.createDirectories(outputDirectory);
                notifyMessage.execute(outputDirectory + " created.");
            }
            Files.walkFileTree(sourceDirectory,
                EnumSet.noneOf(FileVisitOption.class),
                MAX_DIRECTORY_DEPTH,
                new PhotoFileVisitor(outputDirectory, prefix, timeDifference, notifyMessage));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
}
