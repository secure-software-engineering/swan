package de.fraunhofer.iem.swan.cli;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class can extract resource entries from the JAR file that this
 * application is located in. Also it keeps track of extracted files to delete
 * them when the application is exiting.
 *
 * @author Ingo Budde (Fraunhofer IEM)
 */
public class FileUtility {

    /**
     * List of temporary files that have been created and must be deleted before
     * exiting the application.
     */
    private List<File> tempFiles = new ArrayList<>();

    /**
     * List of temporary directories that have been created and must be deleted
     * before exiting the application.
     */
    private List<File> tempDirs = new ArrayList<>();

    /**
     * Finds and provides the bundled directory with the given resource location
     * inside this application's jar file. If it is not directly accessible from the
     * filesystem, it is copied to a temporary location.
     *
     * @param resourceLocation The location of the directory within the jar file.
     * @return The File describing the location of the provided directory.
     * @throws IOException When an error occurs while copying the directory.
     */
    public File getResourceDirectory(String resourceLocation) throws IOException {

        File localFile = getLocalResource(resourceLocation);

        if (localFile != null) {
            return localFile;
        }

        // Create Temporary Directory
        Path path = Files.createTempDirectory("swan");
        File tempDir = path.toFile();
        this.tempDirs.add(tempDir);

        // Find JAR Entries
        URL url = FileUtility.class.getResource(resourceLocation);
        if (resourceLocation.startsWith(File.separator)) {
            resourceLocation = resourceLocation.substring(1);
        }

        assert url != null;
        if (url.getProtocol().equals("jar")) {

            String[] jarFilePath = url.getPath().split("!")[0].split("file:");

            File jarFile = new File(jarFilePath[jarFilePath.length - 1]);

            if (jarFile.exists()) {

                try (JarFile jar = new JarFile(jarFile)) {

                    Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar

                    while (entries.hasMoreElements()) {

                        JarEntry entry = entries.nextElement();

                        if (entry.getName().contains(resourceLocation) && !entry.getName().endsWith(File.separator)) {

                            String destinationName = entry.getName().substring(resourceLocation.length());
                            File destination = new File(tempDir, destinationName);
                            InputStream stream = FileUtility.class.getResourceAsStream(File.separator + entry.getName());
                            FileUtils.copyInputStreamToFile(stream, destination);
                        }
                    }
                }
            }
        }
        return tempDir;
    }

    /**
     * Finds and provides the bundled file with the given resource location inside
     * this application's jar file. If it is not directly accessible from the
     * filesystem, it is copied to a temporary location.
     *
     * @param resourceLocation The location of the file within the jar file.
     * @return The File describing the location of the provided file.
     * @throws IOException When an error occurs while copying the file.
     */
    public File getResourceFile(String resourceLocation, File destinationFile) throws IOException {

        File localFile = getLocalResource(resourceLocation);

        if (localFile != null) {
            return localFile;
        } else {
            String[] segments = resourceLocation.split("\\/|\\\\");
            String filename = segments[segments.length - 1];

            if (destinationFile == null) {

                Path path = Files.createTempFile("swan", filename);
                File tempFile = path.toFile();
                destinationFile = tempFile;
                this.tempFiles.add(tempFile);
            }

            InputStream stream = FileUtility.class.getResourceAsStream(resourceLocation);
            FileUtils.copyToFile(stream, destinationFile);
        }
        return destinationFile;
    }

    /**
     * Returns the jar resource from the local filesystem, if it exists.
     *
     * @param resourceLocation The location of the file within the jar file.
     * @return The File from the local filesystem, or <code>null</code>, if no such
     * file exists.
     */
    private File getLocalResource(String resourceLocation) {

        URL url = FileUtility.class.getResource(resourceLocation);
        assert url != null;

        if (url.getProtocol().equals("file")) {
            return new File(url.getFile());
        } else
            return null;
    }

    /**
     * Cleans up this object and deletes all temporary files and directies. This
     * should be called before the application exists, or after an analysis is done.
     */
    public void dispose() {
        for (File directory : tempDirs) {
            try {
                FileUtils.deleteDirectory(directory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (File file : tempFiles) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
