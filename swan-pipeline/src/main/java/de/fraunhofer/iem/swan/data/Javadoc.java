
package de.fraunhofer.iem.swan.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the Javadoc comment for a method and the class it belongs to.
 *
 * @author Oshando Johnson on 23.07.20
 */
public class Javadoc {

    @JsonProperty("method")
    private String methodComment;
    @JsonProperty("class")
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

    public String getMergedComments() {
        return methodComment + " " + classComment;
    }
    @Override
    public String toString() {
        return "Javadoc{" +
                "methodComment='" + methodComment + '\'' +
                ", classComment='" + classComment + '\'' +
                '}';
    }
}