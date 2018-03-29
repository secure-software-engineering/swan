package de.fraunhofer.iem.mois;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.fraunhofer.iem.mois.data.Category;
import de.fraunhofer.iem.mois.data.Constants;
import de.fraunhofer.iem.mois.data.Method;

public class Writer {

	private final Set<Method> testMethods;

	public Writer(Set<Method> testMethods) {
		this.testMethods = testMethods;
	}

	public void writeResultsToFiles(String targetFileName, Set<Method> methods, Set<Category> categories)
			throws IOException {
		Map<Category, Integer> counters = new HashMap<Category, Integer>();
		BufferedWriter wr = null;
		
		File file = new File(targetFileName.substring(0, targetFileName.lastIndexOf(File.separator)));
		if(!file.exists())
			file.mkdir();
		
		try {
			wr = new BufferedWriter(new FileWriter(targetFileName));

			for (Category type : categories) {
				String fileName = appendFileName(targetFileName, "_" + type.toString());
				wr = new BufferedWriter(new FileWriter(fileName));
				for (Method am : methods) {
					Category category = am.getCategoryClassified();
					if (!testMethods.contains(am) || category == null)
						continue;
					if (category == type) {
						if (counters.containsKey(category)) {
							int counter = counters.get(category);
							counters.put(category, ++counter);
						} else
							counters.put(am.getCategoryClassified(), 1);
						wr.write(am.toString() + "\n");
					}
				}
				wr.flush();
				wr.close();

				if (counters.get(type) != null)
					System.out.println(counters.get(type) + " " + type.toString() + " written to file: " + fileName);
			}
		} finally {
			if (wr != null)
				wr.close();
		}
	}

	private String appendFileName(String targetFileName, String string) {
		int pos = targetFileName.lastIndexOf(".");
		return targetFileName.substring(0, pos) + string + targetFileName.substring(pos);
	}

	public void printResultsTXT(Set<Method> methods, Set<String> tmpFiles, String outputFile) throws IOException {

		Map<Category, Integer> counters = new HashMap<Category, Integer>();
		BufferedWriter wr = null;
		File file = new File(outputFile.substring(0, outputFile.lastIndexOf(File.separator)));
		if(!file.exists())
			file.mkdir();
		
		wr = new BufferedWriter(new FileWriter(outputFile));
		
		
		for (Method method : methods) {
			if (method.getCategoriesClassified().isEmpty())
				continue;
			StringBuilder sb = new StringBuilder();
			sb.append(method.getSignature());
			sb.append(" -> ");

			for (Category category : method.getCategoriesClassified()) {
				sb.append("_");
				sb.append(category.toString().toUpperCase());
				sb.append("_");

				if (counters.containsKey(category)) {
					int counter = counters.get(category);
					counters.put(category, ++counter);
				} else
					counters.put(category, 1);
			}
			wr.write(sb.toString() + "\n");
		}

		wr.flush();
		wr.close();

		System.out.println(methods.size() + " total methods written to file: " + outputFile);
		for (Category type : counters.keySet())
			if (counters.get(type) != null)
				System.out.println(counters.get(type) + " " + type.toString() + " written to file: " + outputFile);
	}

	/* Outputs results as a JSON file. */
	@SuppressWarnings("unchecked")
	public void printResultsJSON(Set<Method> methods, Set<String> tmpFiles, String outputFile) throws IOException {

		Map<Category, Integer> counters = new HashMap<Category, Integer>();
		
		
		File file = new File(outputFile.substring(0, outputFile.lastIndexOf(File.separator)));
		if(!file.exists())
			file.mkdir();
		
		BufferedWriter wr = null;
		wr = new BufferedWriter(new FileWriter(outputFile));

		int count = 0;
		JSONArray arr = new JSONArray();

		HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

		for (Method method : methods) {

			JSONObject obj = new JSONObject();
			if (method.getCategoriesClassified().isEmpty())
				continue;

			obj.put(Constants.NAME, method.getClassName().toString());
			obj.put(Constants.RETURN_TYPE, method.getReturnType());
			obj.put(Constants.PARAMETERS, method.getParameters());
			obj.put(Constants.DATA_IN, "");
			obj.put(Constants.DATA_OUT, "");
			obj.put(Constants.SECURITY_LEVEL, "");
			obj.put(Constants.DISCOVERY, "automatic");
			obj.put(Constants.FRAMEWORK, "");
			obj.put(Constants.LINK, "");

			JSONArray types = new JSONArray();
			JSONArray cwes = new JSONArray();

			for (Category category : method.getCategoriesClassified()) {

				if (category.isCwe())
					cwes.add(category.toString());
				else
					types.add(category.toString());

				if (counters.containsKey(category)) {
					int counter = counters.get(category);
					counters.put(category, ++counter);
				} else
					counters.put(category, 1);
			}
			obj.put(Constants.TYPE, types);
			obj.put(Constants.CWE, cwes);
			obj.put(Constants.COMMENT, "");

			map.put("method" + count, obj);
			arr.add(map.get("method" + count));
		}

		JSONObject parent = new JSONObject();
		parent.put(Constants.METHOD, arr);

		wr.write(parent.toJSONString());
		wr.flush();
		wr.close();
	}

	public void writeResultsQWEL(Set<Method> methods, String outputFile) throws IOException {
		String path = outputFile.substring(0, outputFile.lastIndexOf('\\')+1);
		BufferedWriter w = null;
		try {

			for (Category cat : Category.values()) {
				if(!cat.isCwe())
					continue;
				
				String fullPath = path + cat.toString() + ".qwel";
				File statText = new File(fullPath);
				File file = new File(fullPath.substring(0, outputFile.lastIndexOf(File.separator)));
				if(!file.exists())
					file.mkdir();
				
	            FileOutputStream is = new FileOutputStream(statText);
	            OutputStreamWriter osw = new OutputStreamWriter(is);    
	            w = new BufferedWriter(osw);
	            
	            for(Category catType : Category.values()) {
	            	if(catType.isCwe())
	            		continue;
	            	StringBuilder sb = new StringBuilder();
					sb.append("methods ");
					sb.append(cat.toString());
					sb.append("_");
					sb.append(catType.toString());
					sb.append(" [ \n");
					
	            	for (Method method : methods) {
						if (method.getCategoriesClassified().isEmpty())
							continue;
						for (Category category : method.getCategoriesClassified()) {
							if(category.toString().equals(catType.toString())) {
								String methodSig = method.getSignature().substring(1, method.getSignature().length()-1);
								String sig = methodSig.substring(methodSig.indexOf(' ')+1, methodSig.lastIndexOf(' ')) + " " 
											+ methodSig.substring(0,methodSig.indexOf(' ')-1) + "." 
										+ methodSig.substring(methodSig.lastIndexOf(' ')+1);
								sb.append(sig);
								sb.append("; \n");
							}
						}
					}
	            	sb.append("]");
					w.write(sb.toString() + "\n");
	            }
				

				w.flush();
				w.close();
				}
		} finally {
			if (w != null)
				w.close();
		}
	}
	
	public void writeResultsSoot(Set<Method> methods, String outputFile) throws IOException {
		String path = outputFile.substring(0, outputFile.lastIndexOf('\\')+1);
		BufferedWriter w = null;
		try {

			for (Category cat : Category.values()) {
				if(!cat.isCwe())
					continue;
				
				String fullPath = path + cat.toString() + "_Soot.txt";
				File statText = new File(fullPath);
				File file = new File(fullPath.substring(0, outputFile.lastIndexOf(File.separator)));
				if(!file.exists())
					file.mkdir();
				
				
	            FileOutputStream is = new FileOutputStream(statText);
	            OutputStreamWriter osw = new OutputStreamWriter(is);    
	            w = new BufferedWriter(osw);
	            
	            for(Category catType : Category.values()) {
	            	if(catType.isCwe())
	            		continue;
	            	StringBuilder sb = new StringBuilder();
					sb.append("***__");
					sb.append(catType.toString());
					sb.append("__*** \n\n");
					
	            	for (Method method : methods) {
						if (method.getCategoriesClassified().isEmpty())
							continue;
						for (Category category : method.getCategoriesClassified()) {
							if(category.toString().equals(catType.toString())) {
								sb.append(method.getSignature());
								sb.append(" \n");
							}
						}
					}
	            	sb.append("\n");
					w.write(sb.toString() + "\n");
	            }
				

				w.flush();
				w.close();
				}
		} finally {
			if (w != null)
				w.close();
		}
	}

}
