package org.deeplearning4j.rl4j.model.in;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/8/16.
 * <p>
 * Contain contextual information about the environment from which Observations are observed and must know how to build an Observation from json.
 */
public abstract class BoxInputs<D> implements InputSpace<D> {
    private final int[] shape;
    private final INDArray low;
    private final INDArray high;
    private final String name;
    public final int volume;


    public static BoxInputs get(JSONObject j) {

        JSONArray arr = j.getJSONArray("shape");
        int dim = arr.length();
        int[] shape = new int[dim];
        for (int i = 0; i < dim; ++i)
            shape[i] = arr.getInt(i);

        switch (dim) {
            case 1:
                return new BoxInputs<double[]>(j, shape) {
                    @Override public double[] get(Object o) {
                        JSONArray a = (JSONArray)o;
                        return IntStream.range(0, shape[0]).mapToDouble(a::getDouble).toArray();
                    }
                };
            case 2:
                return new BoxInputs<double[][]>(j, shape) {
                    @Override public double[][] get(Object o) {
                        JSONArray a = (JSONArray)o;

                        return IntStream.range(0, shape[0]).mapToObj(x ->
                            IntStream.range(0, shape[1]).mapToDouble(y -> a.getJSONArray(x).getDouble(y)).toArray()
                        ).toArray(double[][]::new);
                    }
                };
            case 3:
                return new BoxInputs<double[][][]>(j, shape) {
                    @Override public double[][][] get(Object o) {
                        JSONArray a = (JSONArray)o;

                        //TERRIBLE will fix later
                        return IntStream.range(0, shape[0]).mapToObj(
                            x -> IntStream.range(0, shape[1]).mapToObj(
                                y -> IntStream.range(0, shape[2]).mapToDouble(
                                    z -> a.getJSONArray(x).getJSONArray(y).getDouble(z)).toArray()
                        ).toArray(double[][]::new)).toArray(double[][][]::new);
                    }
                };

            default:
                throw new UnsupportedOperationException("TODO: dim=" + dim);
        }
    }

    BoxInputs(JSONObject j, int[] shape) {
        this.name = j.getString("name");
        this.shape = shape;
        this.low = Nd4j.create(shape);
        this.high = Nd4j.create(shape);
        JSONArray lowJson = j.getJSONArray("low");
        JSONArray highJson = j.getJSONArray("high");


        int i;
        int size = this.shape[0];
        for (i = 1; i < this.shape.length; ++i) {
            size *= this.shape[i];
        }
        this.volume = size;

        for (i = 0; i < size; ++i) {
            this.low.putScalar(i, lowJson.getDouble(i));
            this.high.putScalar(i, highJson.getDouble(i));
        }
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
//        } else if (!(o instanceof BoxInputs)) {
//            return false;
//        } else {
//            BoxInputs other = (BoxInputs) o;
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
        return "GymObservationSpace(name=" + this.getName() + ", shape=" + Arrays.toString(this.getShape()) + ", low=" + this.getLow() + ", high=" + this.getHigh() + ")";
    }

}
