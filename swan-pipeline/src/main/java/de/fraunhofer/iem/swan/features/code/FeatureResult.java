package de.fraunhofer.iem.swan.features.code;

public class FeatureResult {

    private String stringValue;

    private int integerValue;

    private Boolean booleanValue;

    public void setIntegerValue(int value){
        this.integerValue = value;
    }

    public void setStringValue(String value){
        this.stringValue = value;
    }

    public void setBooleanValue(Boolean value){
        this.booleanValue = value;
    }

    public int getIntegerValue(){return this.integerValue;}

    public String getStringValue(){return this.stringValue;}

    public Boolean getBooleanValue(){return this.booleanValue;}

}
