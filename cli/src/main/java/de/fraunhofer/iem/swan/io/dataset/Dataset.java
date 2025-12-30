package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.srm.dataset.Method;
import de.fraunhofer.iem.srm.dataset.SrmDataset;

import java.util.Set;

public class Dataset {

    private SrmDataset train;
    private SrmDataset test;

    public Dataset() {
        train = new SrmDataset();
        test = new SrmDataset();
    }

    public SrmDataset getTrain() {
        return train;
    }

    public void setTrain(SrmDataset train) {
        this.train = train;
    }

    public SrmDataset getTest() {
        return test;
    }

    public void setTest(SrmDataset test) {
        this.test = test;
    }

    public Set<Method> getTrainMethods() {
        return train.getMethods();
    }

    public Set<Method> getTestMethods() {
        return test.getMethods();
    }
}
