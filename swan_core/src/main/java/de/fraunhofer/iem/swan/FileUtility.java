package de.fraunhofer.iem.swan;

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

import org.apache.commons.io.FileUtils;

/**
 * This class can extract resource entries from the JAR file that this
 * application is located in. Also it keeps track of extracted files to delete
 * them when the application is exiting.
 * 
 * @author Ingo Budde (Fraunhofer IEM)
 *
 */
public class FileUtility {

	/**
	 * List of temporary files that have been created and must be deleted before
	 * exiting the application.
	 */
	private List<File> tempFiles = new ArrayList<File>();

	/**
	 * List of temporary directories that have been created and must be deleted
	 * before exiting the application.
	 */
	private List<File> tempDirs = new ArrayList<File>();

	/**
	 * Finds and provides the bundled directory with the given resource location
	 * inside this application's jar file. If it is not directly accessable from the
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
		if (resourceLocation.startsWith("/")) {
			resourceLocation = resourceLocation.substring(1);
		}
		if ("jar".equals(url.getProtocol())) {
			String[] splitted = url.getPath().split("\\!");
			splitted = splitted[0].split("file:/");
			String jarPath = splitted[splitted.length - 1];
			File jarFile = new File(jarPath);
			if (jarFile.exists()) {
				JarFile jar = null;
				try {
					jar = new JarFile(jarFile.getAbsolutePath());
					Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
					while (entries.hasMoreElements()) {
						String name = entries.nextElement().getName();
						if (name.startsWith(resourceLocation) && !name.endsWith("/")) {
							String destinationName = name.substring(resourceLocation.length());
							File destination = new File(tempDir, destinationName);
							InputStream stream = FileUtility.class.getResourceAsStream("/" + name);
							FileUtils.copyInputStreamToFile(stream, destination);
						}
					}
				} finally {
					if (jar != null) {
						jar.close();
					}
				}
			}
		}
		return tempDir;
	}

	/**
	 * Finds and provides the bundled file with the given resource location inside
	 * this application's jar file. If it is not directly accessable from the
	 * filesystem, it is copied to a temporary location.
	 * 
	 * @param resourceLocation The location of the file within the jar file.
	 * @return The File describing the location of the provided file.
	 * @throws IOException When an error occurs while copying the file.
	 */
	public File getResourceFile(String resourceLocation) throws IOException {
		return getResourceFile(resourceLocation, null);
	}

	/**
	 * Finds and provides the bundled file with the given resource location inside
	 * this application's jar file. If it is not directly accessable from the
	 * filesystem, it is copied to a temporary location.
	 * 
	 * @param resourceLocation The location of the file within the jar file.
	 * @param destinationFile  The destination to place the file, in case it needs
	 *                         to be copied. If <code>null</code>, a new temporary
	 *                         file is created and used.
	 * @return The File describing the location of the provided file.
	 * @throws IOException When an error occurs while copying the file.
	 */
	public File getResourceFile(String resourceLocation, File destinationFile) throws IOException {
		File localFile = getLocalResource(resourceLocation);
		if (localFile != null) {
			return localFile;
		}
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

		return destinationFile;
	}

	/**
	 * Returns the jar resource from the local filesystem, if it exists.
	 * 
	 * @param resourceLocation The location of the file within the jar file.
	 * @return The File from the local filesystem, or <code>null</code>, if no such
	 *         file exists.
	 */
	private File getLocalResource(String resourceLocation) {
		URL url = FileUtility.class.getResource(resourceLocation);
		if ("file".equals(url.getProtocol())) {
			return new File(url.getFile());
		}
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
