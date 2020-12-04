
package com.manoelcampos.sample2;

import java.io.IOException;

public class Test implements Interface1, Interface2{

    @Override
    public int equation(int input) throws IOException {
        return 0;
    }

    @Override
    public int divide(int a, int b) {
        return 0;
    }

    @Override
    public int equation(int input1, int input2) throws IOException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int someMethod1(int input1, int input2) throws IOException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public double someMethod2(double input) throws IOException, IndexOutOfBoundsException, IllegalArgumentException {
        return 0;
    }

    @Override
    public long someMethod3(long input) throws IOException, IndexOutOfBoundsException, IllegalArgumentException {
        return 0;
    }

    @Override
    public long someMethod4(long input) throws IOException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int method1(int input1, int input2) throws IOException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public double method2(double input) throws IOException, IndexOutOfBoundsException, IllegalArgumentException {
        return 0;
    }

    @Override
    public long method3(long input) throws IOException, IndexOutOfBoundsException, IllegalArgumentException {
        return 0;
    }
}