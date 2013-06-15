/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.photorenametool.domain;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author sk
 */
public class Exif {

    /**
     * Get taken date from Exif.
     *
     * @param imageFilePath file path to get taken date
     * @return taken date or null(if failed to get taken by Exif)
     */
    public static DateTime takenDateOf(Path photoFilePath) {
        if (photoFilePath == null) {
            return null;
        }
        try (InputStream in = Files.newInputStream(photoFilePath)) {
            Metadata metadata = JpegMetadataReader.readMetadata(in);
            Directory exifDirectory = metadata.getDirectory(ExifIFD0Directory.class);
            if (exifDirectory == null) {
                return null;
            }
            String dateTime = exifDirectory.getString(ExifIFD0Directory.TAG_DATETIME);
            if (dateTime == null) {
                return null;
            }
            return DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss").parseDateTime(dateTime);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
