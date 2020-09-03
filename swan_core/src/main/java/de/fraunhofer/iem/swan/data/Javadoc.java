
package de.fraunhofer.iem.swan.data;

/**
 * Stores the Javadoc comment for a method and the class it belongs to.
 *
 * @author Oshando Johnson on 23.07.20
 */
public class Javadoc {

    private String methodComment;
    private String classComment;

    public Javadoc() {

        methodComment = "";
        classComment = "";
    }

    public Javadoc(String methodComment, String classComment) {
        this.methodComment = methodComment;
        this.classComment = classComment;
    }

    public String getMethodComment() {
        return methodComment;
    }

    public void setMethodComment(String methodComment) {
        this.methodComment = methodComment;
    }

    public String getClassComment() {
        return classComment;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }
}