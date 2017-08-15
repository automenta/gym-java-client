package org.deeplearning4j.rl4j.model.in;

import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;

public class DiscreteInputs implements InputSpace {

    public DiscreteInputs(JSONObject j) {

        System.out.println(j);
    }

    @Override
    public Object get(JSONObject x) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int[] getShape() {
        return new int[0];
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
