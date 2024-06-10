package de.fraunhofer.iem.swan.data;

/**
 * POJO for the Relevant parts of a method
 *
 * @author Goran Piskachev
 */

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RelevantPart {

    @JsonProperty("return")
    private boolean returnValue = false;
    private List<Integer> parameters = new ArrayList<Integer>();

    public RelevantPart(boolean rT, List<Integer> parInd) {
        setReturnValue(rT);
        setParameters(parInd);
    }

    public RelevantPart() {

    }

    public List<Integer> getParameters() {
        return parameters;
    }

    public void setParameters(List<Integer> parameters) {
        this.parameters = parameters;
    }

    public boolean getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }
}
