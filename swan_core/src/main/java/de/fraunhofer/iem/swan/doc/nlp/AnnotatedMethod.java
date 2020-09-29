package de.fraunhofer.iem.swan.doc.nlp;

import de.fraunhofer.iem.swan.data.Method;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

/**
 * @author Oshando Johnson on 19.07.20
 */
public class AnnotatedMethod {

    private Method method;
    private List<CoreMap> methodMap;
    private List<CoreMap> classMap;

    public AnnotatedMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<CoreMap> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(List<CoreMap> methodMap) {
        this.methodMap = methodMap;
    }

    public List<CoreMap> getClassMap() {
        return classMap;
    }

    public void setClassMap(List<CoreMap> classMap) {
        this.classMap = classMap;
    }
}
