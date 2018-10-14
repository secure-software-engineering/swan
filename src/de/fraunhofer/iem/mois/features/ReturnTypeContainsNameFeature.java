package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.IFeature.Type;
import de.fraunhofer.iem.mois.data.Method;
import soot.SootMethod;

/**
* Feature which checks whether the return type of a method contains a given "key" in the fully qualified name. 
*
* @author Goran Piskachev
*
*/
public class ReturnTypeContainsNameFeature extends AbstractSootFeature {
	private final String key;

	  public ReturnTypeContainsNameFeature(String cp, String key) {
	    super(cp);
	    this.key = key;
	  }

	  @Override
	  public Type appliesInternal(Method method) {
	    if (method.getReturnType().toLowerCase().contains(this.key.toLowerCase()))
	      return Type.TRUE;
	    else return Type.FALSE;
	  }

	  @Override
	  public String toString() {
	    return "<Return type is " + this.key + ">";
	  }
}
