package de.fraunhofer.iem.swan.features.code.type;

import java.util.*;

import de.fraunhofer.iem.swan.data.Method;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;

/**
 * Feature which checks whether there is a data flow of the specified kind in
 * the current method
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class ParameterInCallFeature extends AbstractSootFeature {

    public enum CheckType {
        CheckSink, CheckSinkInAbstract, CheckSource, CheckFromMethodToSink,
        CheckFromParamToNative, CheckFromParamToInterface,
        CheckFromInterfaceToResult
    }

    private final static String[] sinkMethods = {"start", "save", "send", "dump",
            "dial", "broadcast", "bind", "transact", "write", "update", "perform",
            "notify", "insert", "enqueue", "replace", "show", "dispatch", "print",
            "println", "create", "adjust", "set", "query", "execute"};

    private final static String[] transformerMethods =
            {"get", "append", "find", "arraycopy", "valueOf", "as", "generate"};
    private final static String[] writerMethods = {"writeArray",
            "writeBooleanArray", "writeBundle", "writeByte", "writeByteArray",
            "writeCharArray", "writeCharSequence", "writeDouble", "writeDoubleArray",
            "writeFloat", "writeFloatArray", "writeInt", "writeIntArray", "writeList",
            "writeLong", "writeLongArray", "writeMap", "writeParcelable",
            "writeParcelableArray", "writeString", "writeStringArray",
            "writeStringList", "writeStrongBinder", "writeStrongInterface",
            "writeTypedArray", "writeTypedList", "writeObject", "<init>", "add",
            "putExtra", "putExtras"};

    private final String methodName;
    private final CheckType checkType;
    private final List<String> sinks = new ArrayList<>();
    private final int argumentPosition;
    private final boolean restrictPackage;

    public ParameterInCallFeature(String methodName,
                                  CheckType checkType) {
        this(methodName, -1, false, checkType);
    }

    public ParameterInCallFeature(String methodName,
                                  boolean restrictPackage, CheckType checkType) {
        this(methodName, -1, restrictPackage, checkType);
    }

    public ParameterInCallFeature(String methodName, int parameterPostion, boolean restrictPackage, CheckType checkType) {
        super();
        this.methodName = methodName;
        this.checkType = checkType;

        if (methodName.isEmpty()) {
            sinks.addAll(Arrays.asList(sinkMethods));
        } else
            sinks.add(methodName);

        this.argumentPosition = parameterPostion;
        this.restrictPackage = restrictPackage;
    }

    @Override
    public Type appliesInternal(Method method) {
        try {

            if (method.getSootMethod() == null) {
                return Type.FALSE;
            }

            if (!method.getSootMethod().isConcrete()) return Type.FALSE;

            // Collect the sources and then find calls where the parameter is
            // actually sent out
            Set<Value> params = new HashSet<>();
            Set<Value> oldParams = new HashSet<>();
            oldParams.add(null);
            while (!params.equals(oldParams)) {
                oldParams = new HashSet<>(params);

                Body body;
                try {
                    body = method.getSootMethod().retrieveActiveBody();
                } catch (Exception ex) {
                    return Type.FALSE;
                }
                for (Unit u : body.getUnits()) {
                    if (!(u instanceof Stmt)) continue;
                    Stmt stmt = (Stmt) u;

                    // Add private field access. Has to be in the beginning of the for
                    // loop
                    /*
                     * // if (this.checkType == CheckType.CheckFromMethodToSink) if(u
                     * instanceof AssignStmt){ AssignStmt assi = (AssignStmt) u;
                     * if(assi.getRightOp() instanceof FieldRef){ FieldRef fr =
                     * (FieldRef)assi.getRightOp(); if(fr.getField().isPrivate() &&
                     * !fr.getField().isFinal()) params.add(fr); } }
                     */

                    // When we are looking for sinks, we treat the parameters
                    // as sources
                    if (this.checkType == CheckType.CheckSink
                            || this.checkType == CheckType.CheckSinkInAbstract
                            || this.checkType == CheckType.CheckFromParamToNative)
                        if (u instanceof IdentityStmt) {
                            IdentityStmt id = (IdentityStmt) u;
                            if (id.getRightOp() instanceof ParameterRef) params.add(id.getLeftOp());
                        }

                    // When looking for sources, we treat specific method calls
                    // as sources
                    if (this.checkType == CheckType.CheckSource
                            || this.checkType == CheckType.CheckFromMethodToSink)
                        if (u instanceof AssignStmt) {
                            AssignStmt assi = (AssignStmt) u;
                            if (assi.containsInvokeExpr()) {
                                InvokeExpr inv = assi.getInvokeExpr();
                                if (argumentPosition == -1 && inv.getMethod().getName().startsWith(this.methodName))
                                    params.add(assi.getLeftOp());
                            }
                        }
                    if (this.checkType == CheckType.CheckFromInterfaceToResult)
                        if (u instanceof AssignStmt) {
                            AssignStmt assi = (AssignStmt) u;
                            if (assi.containsInvokeExpr())
                                if (assi.getInvokeExpr().getMethod().isAbstract() || assi.getInvokeExpr().getMethod().getDeclaringClass().isInterface())
                                    params.add(assi.getLeftOp());
                        }

                    // Check whether a tainted parameter flows into a return value
                    if (this.checkType == CheckType.CheckSource
                            || this.checkType == CheckType.CheckFromInterfaceToResult)
                        if (u instanceof ReturnStmt) {
                            ReturnStmt ret = (ReturnStmt) u;
                            if (params.contains(ret.getOp())) return Type.TRUE;
                        }

                    if (u instanceof AssignStmt) {
                        AssignStmt assign = (AssignStmt) u;
                        // Do we have an assignment a=b?
                        if (params.contains(assign.getRightOp()))
                            params.add(assign.getLeftOp());
                        if (assign.getRightOp() instanceof CastExpr) {
                            CastExpr ce = (CastExpr) assign.getRightOp();
                            if (params.contains(ce.getOp())) params.add(assign.getLeftOp());
                        }
                        if (assign.getRightOp() instanceof JArrayRef) {
                            JArrayRef ref = (JArrayRef) assign.getRightOp();
                            if (params.contains(ref.getBase()))
                                params.add(assign.getLeftOp());
                        }

                        // Do we have a member access?
                        if (assign.getRightOp() instanceof InstanceFieldRef) {
                            InstanceFieldRef ref = (InstanceFieldRef) assign.getRightOp();
                            if (params.contains(ref.getBase()))
                                params.add(assign.getLeftOp());
                        }

                        // Do we set a field on a tainted object?
                        if (this.checkType == CheckType.CheckFromMethodToSink)
                            if (assign.getLeftOp() instanceof InstanceFieldRef) {
                                InstanceFieldRef ref = (InstanceFieldRef) assign.getLeftOp();
                                if (params.contains(ref.getBase())) return Type.TRUE;
                            }

                        if (stmt.containsInvokeExpr()) {
                            InvokeExpr inv = stmt.getInvokeExpr();

                            // Is this a transformer method?
                            for (String methodSink : transformerMethods)
                                if (inv.getMethod().getName().startsWith(methodSink))
                                    for (Value vInv : inv.getArgs())
                                        if (params.contains(vInv)) {
                                            params.add(assign.getLeftOp());
                                            break;
                                        }

                            if (inv instanceof InstanceInvokeExpr) {
                                // If this a call on the tainted object itself?
                                InstanceInvokeExpr instInv = (InstanceInvokeExpr) inv;
                                if (params.contains(instInv.getBase()))
                                    params.add(assign.getLeftOp());
                            }

                            // Does a tainted value flow into a static method?
                            // Conservatively assume the return value to be tainted
                            if (inv.getMethod().isStatic()) for (Value vInv : inv.getArgs())
                                if (params.contains(vInv)) {
                                    params.add(assign.getLeftOp());
                                    break;
                                }
                        }
                    }

                    if (stmt.containsInvokeExpr()) {
                        InvokeExpr inv = stmt.getInvokeExpr();

                        // Is this a source method with a specified parameter position?
                        if (this.checkType == CheckType.CheckSource && argumentPosition >= 0
                                && inv.getMethod().getName().startsWith(this.methodName)) {
                            if (argumentPosition >= inv.getArgCount())
                                System.out.println("shit");
                            params.add(inv.getArg(argumentPosition));
                        }

                        // Is this a writer method that taints the base object?
                        if (inv instanceof InstanceInvokeExpr)
                            for (String methodSink : writerMethods)
                                if (inv.getMethod().getName().startsWith(methodSink)) for (Value vInv : inv.getArgs())
                                    if (params.contains(vInv)) params.add(((InstanceInvokeExpr) inv).getBase());

                        // Is this a sink method?
                        if (this.checkType == CheckType.CheckSink
                                || this.checkType == CheckType.CheckFromMethodToSink) {
                            for (String methodSink : this.sinks)
                                if (!isTransformer(methodSink))
                                    if (inv.getMethod().getName().startsWith(methodSink) || inv.getMethod().getDeclaringClass().getName().startsWith(methodSink))
                                        if (checkPackageRestriction(method.getSootMethod().getDeclaringClass(), inv.getMethod().getDeclaringClass()))
                                            for (Value vInv : inv.getArgs())
                                                if (params.contains(vInv)) return Type.TRUE;
                        } else {
                            if (this.checkType == CheckType.CheckFromMethodToSink) {
                                inv.getMethod().isNative();
                            }
                        }
                        if (this.checkType == CheckType.CheckSinkInAbstract)
                            if (inv.getMethod().isAbstract()
                                    || inv.getMethod().getDeclaringClass().isInterface())
                                for (Value vInv : inv.getArgs())
                                    if (params.contains(vInv)) return Type.TRUE;
                    }
                }
            }
            return Type.FALSE;
        } catch (Exception ex) {
            return Type.FALSE;
        }
    }

    private boolean checkPackageRestriction(SootClass methodClass,
                                            SootClass sinkClass) {
        if (!restrictPackage) return true;
        String methodPackage = methodClass.getName().substring(0,
                methodClass.getName().lastIndexOf("."));
        String sinkPackage =
                sinkClass.getName().substring(0, sinkClass.getName().lastIndexOf("."));
        return !methodPackage.equals(sinkPackage);
    }

    private boolean isTransformer(String methodSink) {
        for (String s : transformerMethods)
            if (methodSink.startsWith(s)) return true;
        return false;
    }

    @Override
    public String toString() {
        switch (this.checkType) {
            case CheckSink:
                if (this.methodName.isEmpty())
                    return "<Parameter to sink method call>";
                else
                    return "<Parameter to sink method " + this.methodName + ">";
            case CheckSinkInAbstract:
                return "<Parameter to abstract sink>";
            case CheckSource:
                return "<Value from source method " + methodName + " to return>";
            case CheckFromMethodToSink:
                return "<Value from method " + methodName + " to sink method>";
            case CheckFromParamToNative:
                return "<Value from method parameter to native method>";
            case CheckFromInterfaceToResult:
                return "<Value from interface method to return>";
            case CheckFromParamToInterface:
                return "<Value from parameter to interface method>";
            default:
                break;
        }
        return "";
    }
}
