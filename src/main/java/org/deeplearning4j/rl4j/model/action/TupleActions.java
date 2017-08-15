package org.deeplearning4j.rl4j.model.action;

import java.util.Arrays;

/** TODO support mixed and non-Discrete tuple contents.
 */
public class TupleActions implements ActionSpace<int[]> {

    private final DiscreteActions[] sub;

    @Override
    public String toString() {
        return "TupleActions(" +
                Arrays.toString(sub) +
                ')';
    }

    public TupleActions(DiscreteActions[] subspaces) {
        this.sub = subspaces;
    }

    @Override
    public int[] random() {
        int[] r = new int[sub.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = sub[i].random();
        }
        return r;
    }

    @Override public Object encode(int[] a) {
        return a;
    }


    @Override
    public int[] nop() {
        return null;
    }
}
