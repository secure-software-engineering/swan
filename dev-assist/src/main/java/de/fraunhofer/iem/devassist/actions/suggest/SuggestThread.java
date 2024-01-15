/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Goran Piskachev (goran.piskachev@iem.fraunhofer.de) - initial implementation
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - plugin integration
 ******************************************************************************/

package de.fraunhofer.iem.devassist.actions.suggest;

import com.intellij.openapi.project.Project;

public class SuggestThread extends Thread {

    private Project project;
    private String configFilePath;
    private String projectPath;

    SuggestThread(Project project, String filePath, String projectDirectory) {

        this.project = project;
        this.configFilePath = filePath;
        this.projectPath = projectDirectory;
    }

    /**
     * Executes code to suggest methods on a separate thread and returns the results.
     */
    public void run() {

        //FIXME Reimplement and evaluate SuggestSWAN
       /* Map<Method, Set<IFeature>> matrix = new HashMap<Method, Set<IFeature>>();
        Set<Method> methods = new HashSet<Method>();
        try {
            SrmList srmList = SrmListUtils.importFile(configFilePath);
            methods = srmList.getMethods();
        } catch (Exception e){
            e.printStackTrace();
        }

        CodeFeatureHandler featureHandler = new CodeFeatureHandler();
        featureHandler.initializeFeatures();
        /*FeatureHandler featureHandler = new FeatureHandler(projectPath);
        featureHandler.initializeFeatures(0);*/

        /**Set<ICodeFeature> features = featureHandler.features().get(Category.NONE);

         for (Method m : methods) {
         Set<IFeature> mFeatures = new HashSet<IFeature>();
         for (ICodeFeature f : features) {
         //If method conforms to the feature, add it to the matrix.
         if (f.applies(m).equals(ICodeFeature.FeatureType.BOOLEAN))
         mFeatures.add(f);
         }
         matrix.put(m, mFeatures);
         }

         Suggester suggester = new Suggester();
         Set<MethodWrapper> suggestedMethods = new HashSet<>();
         boolean methodPairFound = false;

         //Get training methods
         TrainingFileManager trainingFileManager = new TrainingFileManager(project);
         Set<String> trainingMethods = new HashSet<>(trainingFileManager.getTrainingMethods().keySet());
         trainingMethods.addAll(MethodListTree.suggestedMethodsList);

         while (!methodPairFound) {

         MethodPair methodPair = suggester.suggestMethod(matrix, features);

         if (methodPair != null) {

         if (trainingMethods.contains(methodPair.getMethod1().getSignature(true)) ||
         trainingMethods.contains(methodPair.getMethod2().getSignature(true))) {
         continue;
         }

         methodPair.getMethod1().setStatus(MethodWrapper.MethodStatus.SUGGESTED);
         suggestedMethods.add(methodPair.getMethod1());
         methodPair.getMethod2().setStatus(MethodWrapper.MethodStatus.SUGGESTED);
         suggestedMethods.add(methodPair.getMethod2());
         methodPairFound = true;
         }
         }

         MessageBus messageBus = project.getMessageBus();
         SuggestNotifier suggestNotifier = messageBus.syncPublisher(SuggestNotifier.SUGGEST_METHOD_TOPIC);
         suggestNotifier.endSuggestMethod(suggestedMethods);**/
    }
}
