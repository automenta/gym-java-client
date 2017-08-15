package org.deeplearning4j.rl4j.model.action;

import java.util.Random;

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/8/16.
 *         <p>
 *         A discrete model of action. A discrete model is always isomorphic
 *         to a model of integer so we can parametrize directly by Integer.
 *         Benefit of using Integers directly is that you can use it as the
 *         id of the node assigned to that action in the outpout of a DQN.
 */
public class DiscreteActions implements ActionSpace<Integer> {

    //size of the model also defined as the number of different actions
    public final int size;
    protected Random rd;

    public DiscreteActions(int size) {
        this.size = size;
        rd = new Random();
    }

    public Integer random() {
        return rd.nextInt(size);
    }

    public Object encode(Integer a) {
        return a;
    }

    public Integer nop() {
        return 0;
    }

}
