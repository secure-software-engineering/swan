package de.fraunhofer.iem.swan.features.code.type;

import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.iem.swan.data.Method;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

/**
 * Feature which checks whether the current method (indirectly) calls another
 * one
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class MethodCallsMethodFeature extends AbstractSootFeature {

    private final String className;
    private final String methodName;
    private final boolean substringMatch;

    public MethodCallsMethodFeature(String methodName) {
        this("", methodName);
    }

    public MethodCallsMethodFeature(String className, String methodName) {
        this(className, methodName, false);
    }

    public MethodCallsMethodFeature(String className, String methodName, boolean substringMatch) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.substringMatch = substringMatch;
    }

    @Override
    public Type appliesInternal(Method method) {
        try {

            if (method.getSootMethod() == null) {
                return Type.FALSE;
            }
            return checkMethod(method.getSootMethod(), new ArrayList<>());
        } catch (Exception ex) {
            System.err.println("Something went wrong:");
            ex.printStackTrace();
            return Type.FALSE;
        }
    }

    public Type checkMethod(SootMethod method, List<SootMethod> doneList) {
        if (doneList.contains(method))
            return Type.FALSE;
        if (!method.isConcrete())
            return Type.FALSE;
        doneList.add(method);

        try {
            Body body;
            try {
                body = method.retrieveActiveBody();
            } catch (Exception ex) {
                return Type.FALSE;
            }

            for (Unit u : body.getUnits()) {
                if (!(u instanceof Stmt))
                    continue;
                Stmt stmt = (Stmt) u;
                if (!stmt.containsInvokeExpr())
                    continue;

                InvokeExpr inv = stmt.getInvokeExpr();
                if ((substringMatch
                        && inv.getMethod().getName().contains(this.methodName))
                        || inv.getMethod().getName().startsWith(this.methodName)) {
                    if (this.className.isEmpty() || this.className
                            .equals(inv.getMethod().getDeclaringClass().getName()))
                        return Type.TRUE;
                } else if (checkMethod(inv.getMethod(), doneList) == Type.TRUE)
                    return Type.TRUE;
            }
            return Type.FALSE;
        } catch (Exception ex) {
            System.err.println("Oops: " + ex);
            return Type.FALSE;
        }
    }

    @Override
    public String toString() {
        return "Method starting with '" + this.methodName + "' invoked";
    }
}
