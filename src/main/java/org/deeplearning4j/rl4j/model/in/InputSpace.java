package org.deeplearning4j.rl4j.model.in;

import org.json.JSONObject;

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
                return BoxInputs.get(j);
            default:
                throw new UnsupportedOperationException(j.toString());
        }

    }

    String getName();


    I get(Object observation);
}
