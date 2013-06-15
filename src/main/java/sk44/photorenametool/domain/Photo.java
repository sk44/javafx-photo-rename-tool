/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.photorenametool.domain;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sha-Shin.
 *
 * @author sk
 */
public class Photo {

    static String extensionOf(Path file) {
        return FilenameUtils.getExtension(file.getFileName().toString());
    }

    static boolean isPhoto(Path file) {
        String ext = extensionOf(file).toUpperCase();
        return ext.equals("JPG") || ext.equals("JPEG");
    }
    private static final Logger logger = LoggerFactory.getLogger(Photo.class);
    private final Path sourceFile;
    private final String outputFileNamePrefix;
    private final DateTime takenDate;
    private Path destinationFile;

    public Photo(Path sourceFile, String outputFileNamePrefix, int timeDifference) {
        this.sourceFile = sourceFile;
        this.outputFileNamePrefix = outputFileNamePrefix;
        this.takenDate = Exif.takenDateOf(sourceFile).plusHours(timeDifference);
    }

    public Path getDestinationFile() {
        return destinationFile;
    }

    public boolean isTakenDateAvailable() {
        return takenDate != null;
    }

    public void copyTo(Path outputDirectory) {
        destinationFile = createDestinationFilePath(outputDirectory, 0);
        logger.info("copy to: " + destinationFile);
        try {
            Files.copy(sourceFile, destinationFile, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private Path createDestinationFilePath(Path outputDirectory, int duplicateCount) {
        FileSystem fs = FileSystems.getDefault();
        Path result = fs.getPath(outputDirectory.toString(), createOutputFileName(duplicateCount));
        if (Files.exists(result)) {
            return createDestinationFilePath(outputDirectory, duplicateCount + 1);
        }
        return result;
    }

    private String createOutputFileName(int duplicateCount) {
        Objects.requireNonNull(sourceFile);
        Objects.requireNonNull(takenDate);

        return String.format("%s%s%s.%s",
            (isPrefixUse() ? outputFileNamePrefix + "_" : StringUtils.EMPTY),
            takenDate.toString("yyyyMMddHHmmss"),
            (duplicateCount > 0 ? "_" + duplicateCount : StringUtils.EMPTY),
            extensionOf(sourceFile));
    }

    private boolean isPrefixUse() {
        return StringUtils.isNotBlank(outputFileNamePrefix);
    }
}
