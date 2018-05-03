package de.fraunhofer.iem.mois.assist.data;

import java.util.ArrayList;
import java.util.List;

public class DataInOut {


    /**
     * POJO for the Relevant parts of a method
     *
     * @author Goran Piskachev
     */

    private boolean returnValue = false;
    private List<Integer> parameterIndices = new ArrayList<Integer>();

    public DataInOut(boolean rT, List<Integer> parInd) {
        setReturnValue(rT);
        setParameterIndices(parInd);
    }

    public DataInOut() {

    }

    public List<Integer> getParameterIndices() {
        return parameterIndices;
    }

    public void setParameterIndices(List<Integer> parameterIndices) {
        this.parameterIndices = parameterIndices;
    }

    public boolean getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }


}

