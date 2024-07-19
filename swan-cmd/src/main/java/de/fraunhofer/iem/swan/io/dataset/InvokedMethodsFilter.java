package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.swan.data.Method;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Filters invoked methods in the application that are already part of the dataset
 *
 */

public class InvokedMethodsFilter {

    Set<Method> testMethods;
    static Set<Method> invokedMethodsInDataset;
    Set<Method> filteredTestMethods;
    Set<Method> datasetMethods;
    private static final Logger logger = LoggerFactory.getLogger(InvokedMethodsFilter.class);

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
                    datasetMethod.setKnown(true);
                    logger.info("Invoked method: {} is part of the dataset.", datasetMethod.getTrimmedSignature());
                    invokedMethodsInDataset.add(datasetMethod);
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
}
