package com.manoelcampos.sample1;

/**
 * The SubClass1.
 */
public class SubClass1 extends Class1 {

    /**
     * I added this
     */
    public SubClass1(){

    }

    @Override
    public int getField1(){
        return -1;
    }

    /**
     * Gets the value of field 2 as a negative number.
     * @return
     */
    @Override
    public int getField2(){
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * this is me
     * @return {@inheritDoc} value of
     */
    @Override
    public int getField3(){
        return -1;
    }

}