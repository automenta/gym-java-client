package org.deeplearning4j.rl4j.model.in;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;

/**
 * @param <I> the type of Observation
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/8/16.
 *         <p>
 *         An array observation model enables you to create an Observation Space of custom dimension
 */

public class ArrayInputSpace<I> implements InputSpace<I[]> {
    private final String name = "Custom";
    private final int[] shape;
    private final INDArray low;
    private final INDArray high;

    public ArrayInputSpace(int[] shape) {
        this.shape = shape;
        this.low = Nd4j.create(1);
        this.high = Nd4j.create(1);
    }

    public String getName() {
        return this.name;
    }

    public int[] getShape() {
        return this.shape;
    }

    public INDArray getLow() {
        return this.low;
    }

    public INDArray getHigh() {
        return this.high;
    }

//    public boolean equals(Object o) {
//        if (o == this) {
//            return true;
//        } else if (!(o instanceof ArrayInputSpace)) {
//            return false;
//        } else {
//            ArrayInputSpace<?> other = (ArrayInputSpace)o;
//            Object this$name = this.getName();
//            Object other$name = other.getName();
//            if (this$name == null) {
//                if (other$name != null) {
//                    return false;
//                }
//            } else if (!this$name.equals(other$name)) {
//                return false;
//            }
//
//            if (!Arrays.equals(this.getShape(), other.getShape())) {
//                return false;
//            } else {
//                Object this$low = this.getLow();
//                Object other$low = other.getLow();
//                if (this$low == null) {
//                    if (other$low != null) {
//                        return false;
//                    }
//                } else if (!this$low.equals(other$low)) {
//                    return false;
//                }
//
//                Object this$high = this.getHigh();
//                Object other$high = other.getHigh();
//                if (this$high == null) {
//                    if (other$high != null) {
//                        return false;
//                    }
//                } else if (!this$high.equals(other$high)) {
//                    return false;
//                }
//
//                return true;
//            }
//        }
//    }

    @Override
    public I[] get(Object x) {
        throw new UnsupportedOperationException("TODO: " + x.getClass() + " " + x);
    }

//    public int hashCode() {
//        //int PRIME = true;
//        int _result = 1;
//        Object $name = this.getName();
//        int result = _result * 59 + ($name == null ? 43 : $name.hashCode());
//        result = result * 59 + Arrays.hashCode(this.getShape());
//        Object $low = this.getLow();
//        result = result * 59 + ($low == null ? 43 : $low.hashCode());
//        Object $high = this.getHigh();
//        result = result * 59 + ($high == null ? 43 : $high.hashCode());
//        return result;
//    }

    public String toString() {
        return "ArrayObservationSpace(name=" + this.getName() + ", shape=" + Arrays.toString(this.getShape()) + ", low=" + this.getLow() + ", high=" + this.getHigh() + ")";
    }

}
