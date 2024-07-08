package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.Unit;
import soot.jimple.LookupSwitchStmt;

import java.util.ArrayList;

/***
 * Evaluates the number iteration statements in the method.
 *
 * @author Rohith Kumar
 */
public class IterationStatementsCountFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private int numberOfIterationStatements;

    public IterationStatementsCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfIterationStatements = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        if(method.getSootMethod().hasActiveBody()){
            for(Unit u: method.getSootMethod().retrieveActiveBody().getUnits()){
                if(u instanceof LookupSwitchStmt){
                    this.numberOfIterationStatements += 1;
                }
            }
        }
        this.featureResult.setIntegerValue(this.numberOfIterationStatements);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public String toString() {
        return "IterationStatementsCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }
}