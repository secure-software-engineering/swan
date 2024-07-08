package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.swan.data.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Filters invoked methods in the application that are already part of the dataset
 *
 */

public class InvokedMethodsFilter {

    Set<Method> testMethods;
    static Set<Method> invokedMethodsInDataset;
    Set<Method> filteredTestMethods;
    Set<Method> datasetMethods;

    public InvokedMethodsFilter(Set<Method> testMethods, Set<Method> datasetMethods){
        this.testMethods = testMethods;
        this.datasetMethods = datasetMethods;
        invokedMethodsInDataset = new HashSet<>();
        this.filteredTestMethods = new HashSet<>();
    }

    public Set<Method> filterUnknownInvokedMethods(){
        for(Method method : testMethods){
            if(method.isApplicationMethod()){
                filteredTestMethods.add(method);
                continue;
            }
            boolean inDataset = false;
            for(Method datasetMethod : datasetMethods){
                String testMethodSignature = method.getTrimmedSignature();
                if(datasetMethod.getTrimmedSignature().equals((testMethodSignature))){
                    method.setKnown(true);
                    method.setComment("Invoked method '" + method.getName() + "' is known & part of the Dataset.");
                    invokedMethodsInDataset.add(method);
                    inDataset = true;
                    break;
                }
            }
            if(!inDataset){
                filteredTestMethods.add(method);
            }
        }
        return this.filteredTestMethods;
    }


    public static Set<Method> getKnownInvokedMethods() {
        return invokedMethodsInDataset;
    }

    public String getTestMethodSignature(Method m){
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(m.getFullClassName());
        sb.append(": ").append(m.getReturnType());
        sb.append(" ").append(m.getName());
        sb.append("(");
        sb.append(String.join(", ", m.getParameters()));
        sb.append(")").append(">");
        return sb.toString();
    }
}
