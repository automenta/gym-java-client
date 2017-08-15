package org.deeplearning4j.rl4j.model.action;

public class BoxActions implements ActionSpace<float[]> {

    public final int[] shape;
    public final float[] min;
    public final float[] max;

    public BoxActions(int[] shape, float[] min, float[] max) {
        this.shape = shape; this.min = min; this.max = max;
    }

    @Override
    public float[] random() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public float[] nop() {
        throw new UnsupportedOperationException("TODO");
        //return new float[0];
    }

    @Override
    public Object encode(float[] action) {
        return action;
    }
}
