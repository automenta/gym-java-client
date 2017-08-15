package org.deeplearning4j.rl4j.model.in;

import org.json.JSONObject;

public class DiscreteInputs implements InputSpace<Integer> {

    public final int n;

    public DiscreteInputs(JSONObject j) {
        n = j.getInt("n");
    }

    @Override
    public Integer get(Object x) {
        return (Integer)x;
    }

    @Override
    public String getName() {
        return "Discrete";
    }


}
