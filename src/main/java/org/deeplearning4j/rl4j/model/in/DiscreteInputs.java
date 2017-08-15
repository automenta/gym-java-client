package org.deeplearning4j.rl4j.model.in;

import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;

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

    @Override
    public INDArray getLow() {
        return null;
    }

    @Override
    public INDArray getHigh() {
        return null;
    }

}
