/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.photorenametool.domain;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import sk44.photorenametool.util.Action;

/**
 *
 * @author sk
 */
class PhotoFileVisitor extends SimpleFileVisitor<Path> {

    private final Path outputDirectory;
    private final String prefix;
    private final int timeDifference;
    private final Action<String> notifyMessage;

    public PhotoFileVisitor(Path outputDirectory, String prefix, int timeDifference, Action<String> notifyMessage) {
        this.outputDirectory = outputDirectory;
        this.prefix = prefix;
        this.timeDifference = timeDifference;
        this.notifyMessage = notifyMessage;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (Files.isDirectory(file)) {
            return FileVisitResult.CONTINUE;
        }

        notifyMessage.execute("Source: " + file.toAbsolutePath());

        if (Photo.isPhoto(file) == false) {
            notifyMessage.execute("SKIP - not photo.");
            return FileVisitResult.CONTINUE;
        }

        Photo photo = new Photo(file, prefix, timeDifference);
        if (photo.isTakenDateAvailable()) {
            photo.copyTo(outputDirectory);
            notifyMessage.execute("Output: " + photo.getDestinationFile());
        } else {
            notifyMessage.execute("SKIP - taken date is not available.");
        }

        return FileVisitResult.CONTINUE;
    }
}
