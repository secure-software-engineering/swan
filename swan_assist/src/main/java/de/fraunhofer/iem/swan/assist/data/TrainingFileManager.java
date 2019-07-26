package de.fraunhofer.iem.swan.assist.data;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.swan.assist.util.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Oshando Johnson on 2019-07-04
 */
public class TrainingFileManager {

    private Properties config;
    private String trainingFile;
    private Project currentProject;

    public TrainingFileManager(Project project) {

        currentProject = project;

        config = new Properties();
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public HashMap<String, MethodWrapper> getTrainingMethods() {

        HashMap<String, MethodWrapper> methods = new HashMap<>();
        //Check if training file was set and if it exists
        if (PropertiesComponent.getInstance(currentProject).isValueSet(Constants.TRAIN_FILE_SUGGESTED)) {

            File trainFile = new File(PropertiesManager.setProjectOutputPath(currentProject));

            if (!trainFile.exists())
                PropertiesComponent.getInstance(currentProject).unsetValue(Constants.OUTPUT_DIRECTORY);
            else {
                JSONFileParser fileParser = new JSONFileParser(PropertiesComponent.getInstance(currentProject).getValue(Constants.TRAIN_FILE_SUGGESTED));
                methods = fileParser.parseJSONFileMap();
            }
        }

        //If training file was not found, the default training file will be used
        if (methods.isEmpty()) {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(config.getProperty("train_config_file"));

            if (stream != null) {

                JSONFileParser fileParser = new JSONFileParser();
                methods = fileParser.parseJSONFileStream(new InputStreamReader(stream));
            }
        }
        return methods;
    }

    public boolean mergeExport(HashMap<String, MethodWrapper> methods, String configFileName) {

        HashMap<String, MethodWrapper> trainingMethods = new HashMap<>();

        trainingMethods = getTrainingMethods();
        HashMap<String, MethodWrapper> mergedMethods = new HashMap<>(methods);
        mergedMethods.putAll(trainingMethods);

        //Export changes to configuration files
        JSONWriter exportFile = new JSONWriter();
        try {
            exportFile.writeToJsonFile(new ArrayList<>(mergedMethods.values()), configFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(configFileName);

        trainingFile = file.getAbsolutePath();

        return file.exists();

    }

    public boolean exportNew(HashMap<String, MethodWrapper> methods, String projectPath) {

        String filename = projectPath +
                File.separator +
                config.getProperty("swan_new_training_directory") +
                File.separator +
                config.getProperty("swan_new_training_filename");

        File trainingFile = new File(filename);

        if(!trainingFile.exists()){
            try {
                trainingFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mergeExport(methods, filename);
    }

    public String getTrainingFile() {
        return trainingFile;
    }
}
