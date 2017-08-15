package org.deeplearning4j.rl4j.model.action;

/**
 * @param <A> the type of Action
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/8/16.
 *         <p>
 *         Should contain contextual information about the Action model, which is the model of all the actions that could be available.
 *         Also must know how to return a randomly uniformly sampled action.
 */
public interface ActionSpace<A> {

    /**
     * @return A randomly uniformly sampled action,
     */
    A random();

    /** "No" Operation, if supported */
    A nop();

    Object encode(A action);


}
