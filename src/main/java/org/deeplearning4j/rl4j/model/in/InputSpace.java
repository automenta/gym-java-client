package org.deeplearning4j.rl4j.model.in;

import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 8/11/16.
 * An observation model contains the basic informations about the state model
 */
public interface InputSpace<I> {

    static InputSpace build(JSONObject j) {

        String name = j.getString("name");
        switch (name) {
            case "Discrete":
                return new DiscreteInputs(j);
            case "Box":
                return new BoxInputs(j);
            default:
                throw new UnsupportedOperationException(j.toString());
        }

    }

    String getName();

    int[] getShape();

    INDArray getLow();

    INDArray getHigh();

    I get(JSONObject x);
}
