package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.swan.data.Method;

import java.util.Set;

public class Dataset {

    private SrmList train;
    private SrmList test;

    public Dataset() {
        train = new SrmList();
        test = new SrmList();
    }

    public SrmList getTrain() {
        return train;
    }

    public void setTrain(SrmList train) {
        this.train = train;
    }

    public SrmList getTest() {
        return test;
    }

    public void setTest(SrmList test) {
        this.test = test;
    }

    public Set<Method> getTrainMethods() {
        return train.getMethods();
    }

    public Set<Method> getTestMethods() {
        return test.getMethods();
    }
}
