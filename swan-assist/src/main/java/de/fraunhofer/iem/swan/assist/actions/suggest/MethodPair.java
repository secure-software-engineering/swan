/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Goran Piskachev (goran.piskachev@iem.fraunhofer.de) - initial implementation
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - plugin integration
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.suggest;

import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.data.Method;

public class MethodPair {
    private MethodWrapper m1;
    private MethodWrapper m2;

    /**
     * Initializes MethodPair object.
     * @param m1 first method
     * @param m2 second method
     */
    MethodPair(MethodWrapper m1, MethodWrapper m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    /**
     *
     * @return first method of pair.
     */
    MethodWrapper getMethod1() {
        return m1;
    }

    /**
     *
     * @return second method of pair.
     */
    MethodWrapper getMethod2() {
        return m2;
    }
}
