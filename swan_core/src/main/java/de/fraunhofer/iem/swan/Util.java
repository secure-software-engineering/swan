package de.fraunhofer.iem.swan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iem.swan.IFeature.Type;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.AbstractSootFeature;
import soot.SootMethod;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * Checks whether there are semantic errors in the given set of methods and
	 * tries to filter out duplicates by merging.
	 * 
	 * @param methods The set of method definitions to check
	 * @param init    The set of methods that should not be in the final set
	 * @return The purged set of methods without duplicates
	 */
	public static Set<Method> sanityCheck(Set<Method> methods, Set<Method> init) {
		Map<String, Method> signatureToMethod = new HashMap<String, Method>();
		for (Method m1 : methods) {
			String sig = m1.getSignature();
			Method m2 = signatureToMethod.get(sig);
			if (m2 == null)
				signatureToMethod.put(sig, m1);
			else if (!m1.equals(m2)) {
				m2.addCategoriesTrained(m1.getCategoriesTrained());
				signatureToMethod.put(sig, m2);
			}
		}

		Set<Method> ret = new HashSet<Method>();
		for (Method m : signatureToMethod.values()) {
			if (!init.contains(m))
				ret.add(m);
		}

		logger.info("Sanity check complete: merged " + (methods.size() - ret.size()) + " entries.");

		return ret;
	}

	public static void printStatistics(String message, Set<Method> methods) {
		Map<Category, Integer> counters = new HashMap<Category, Integer>();
		for (Method am : methods) {
			for (Category category : am.getCategoriesTrained()) {
				if (counters.containsKey(category)) {
					int counter = counters.get(category);
					counters.put(category, ++counter);
				} else
					counters.put(category, 1);
			}
		}

		logger.info(message + ": [methods, " + methods.size() + "]");
		for (Category counter : counters.keySet())
			logger.info(", [" + counter.toString() + ", " + counters.get(counter) + "]");
		logger.info(".");
	}

	public static Set<String> getAllClassesFromDirectory(String dir) throws IOException {
		Set<String> classes = new HashSet<String>();
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].getName().endsWith(".jar"))
					classes.addAll(getAllClassesFromJar(listOfFiles[i].getAbsolutePath()));
			}
		}
		return classes;
	}

	private static Set<String> getAllClassesFromJar(String jarFile) throws IOException {
		Set<String> classes = new HashSet<String>();
		ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				String className = entry.getName().replace('/', '.');
				className = className.substring(0, className.length() - ".class".length());
				if (className.contains("$"))
					className = className.substring(0, className.indexOf("$") - 1);
				classes.add(className);
			}
		}
		zip.close();
		return classes;
	}

	public static String buildCP(String dir) {
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		StringBuilder sb = new StringBuilder();
		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].getName().endsWith(".jar") || listOfFiles[i].getName().endsWith(".apk")) {
					if (sb.length() > 0) {
						sb.append(System.getProperty("path.separator"));
					}
					sb.append(listOfFiles[i].getAbsolutePath().toString());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Creates artificial annotations for non-overridden methods in subclasses. If
	 * the class A implements some method foo() which is marked as e.g. a source and
	 * class B extends A, but does not overwrite foo(), B.foo() must also be a
	 * source.
	 * 
	 * @param methods The list of method for which to create subclass annotations
	 * @param cp      The classpath to use.
	 */
	public static void createSubclassAnnotations(final Set<Method> methods, final String cp) {
		int copyCount = -1;
		int totalCopyCount = 0;
		while (copyCount != 0) {
			copyCount = 0;
			for (Method am : methods) {
				// Check whether one of the parent classes is already annotated
				AbstractSootFeature asf = new AbstractSootFeature(cp) {

					@Override
					public Type appliesInternal(Method method) {
						// This already searches up the class hierarchy until we
						// find a match for the requested method.
						SootMethod parentMethod = getSootMethod(method);
						if (parentMethod == null)
							return Type.NOT_SUPPORTED;

						// If we have found the method in a base class and not in
						// the current one, we can copy our current method's
						// annotation to this base class. (copy-down)
						boolean copied = false;
						if (!parentMethod.getDeclaringClass().getName().equals(method.getClassName())) {
							// Get the data object for the parent method
							Method parentMethodData = findMethod(parentMethod);
							if (parentMethodData == null)
								return Type.NOT_SUPPORTED;

							// If we have annotations for both methods, they must match
							if (parentMethodData.isAnnotated() && method.isAnnotated())
								if (!parentMethodData.getCategoriesTrained().equals(method.getCategoriesTrained()))
									throw new RuntimeException(
											"Categories mismatch for " + parentMethodData + " and " + method);

							// If we only have annotations for the parent method, but not for
							// the current one, we copy it down
							if (parentMethodData.isAnnotated() && !method.isAnnotated()) {
								method.setCategoriesTrained(parentMethodData.getCategoriesTrained());
								copied = true;
							}

							// If we only have annotations for the current method, but not for
							// the parent one, we can copy it up
							if (!parentMethodData.isAnnotated() && method.isAnnotated()) {
								parentMethodData.setCategoriesTrained(method.getCategoriesTrained());
								copied = true;
							}
						}
						return copied ? Type.TRUE : Type.FALSE;
					}

					private Method findMethod(SootMethod sm) {
						Method smData = new Method(sm);
						for (Method am : methods)
							if (am.equals(smData))
								return am;
						return null;
					}
				};
				if (asf.applies(am) == Type.TRUE) {
					copyCount++;
					totalCopyCount++;
				}
			}
		}
		logger.info("Created automatic annotations starting from " + totalCopyCount + " methods.");
	}

	public static Set<String> getFiles(String fileInDirectory) throws IOException {
		String directory = fileInDirectory.substring(0, fileInDirectory.lastIndexOf(File.separator));
		String fileName = fileInDirectory.substring(fileInDirectory.lastIndexOf(File.separator) + 1,
				fileInDirectory.length());
		Set<String> files = new HashSet<String>();
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".txt")
					&& !listOfFiles[i].getName().endsWith("_" + Category.NONE + ".txt")
					&& !listOfFiles[i].getName().equals(fileName)) {
				files.add(listOfFiles[i].getCanonicalPath());
			}
		}
		return files;
	}

}
