package de.fraunhofer.iem.swan.io.doc;

import de.fraunhofer.iem.swan.io.doc.ssldoclet.MethodBlockType;
import de.fraunhofer.iem.swan.io.doc.ssldoclet.PackageBlockType;
import de.fraunhofer.iem.swan.io.doc.ssldoclet.SsldocletType;
import edu.stanford.nlp.util.CoreMap;

import java.util.HashMap;
import java.util.List;

/**
 * @author Oshando Johnson on 05.06.20
 */
public class Javadoc {

    private SsldocletType doc;
    private HashMap<String, List<CoreMap>> coreMap;
    private String filename;
    private PackageBlockType packageBlock;
    private HashMap<String, MethodBlockType> methodBlocks;

    public Javadoc(SsldocletType doc, String filename) {
        this.doc = doc;
        this.coreMap = new HashMap<>();
        this.filename = filename;
    }

    public Javadoc(PackageBlockType packageBlock, HashMap<String, MethodBlockType> methods, String filename) {
        this.packageBlock = packageBlock;
        this.methodBlocks = methods;
        this.filename = filename;
        this.coreMap = new HashMap<>();
    }


    public HashMap<String, List<CoreMap>> getCoreMap() {
        return coreMap;
    }

    public void setCoreMap(HashMap<String, List<CoreMap>> coreMap) {
        this.coreMap = coreMap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public PackageBlockType getPackageBlock() {
        return packageBlock;
    }

    public void setPackageBlock(PackageBlockType packageBlock) {
        this.packageBlock = packageBlock;
    }

    public HashMap<String, MethodBlockType> getMethodBlocks() {
        return methodBlocks;
    }

    public void setMethodBlocks(HashMap<String, MethodBlockType> methodBlocks) {
        this.methodBlocks = methodBlocks;
    }
}