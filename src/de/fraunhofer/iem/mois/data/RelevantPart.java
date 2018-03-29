package de.fraunhofer.iem.mois.data;

/**
 * POJO for the Relevant parts of a method
 *
 * @author Goran Piskachev
 *
 */

import java.util.ArrayList;
import java.util.List;

public class RelevantPart {
  private boolean returnValue = false;
  private List<Integer> parameterIndeces = new ArrayList<Integer>();

  public RelevantPart(boolean rT, List<Integer> parInd) {
    setReturnValue(rT);
    setParameterIndeces(parInd);
  }

  public RelevantPart() {

  }

  public List<Integer> getParameterIndeces() {
    return parameterIndeces;
  }

  public void setParameterIndeces(List<Integer> parameterIndeces) {
    this.parameterIndeces = parameterIndeces;
  }

  public boolean getReturnValue() {
    return returnValue;
  }

  public void setReturnValue(boolean returnValue) {
    this.returnValue = returnValue;
  }
}
