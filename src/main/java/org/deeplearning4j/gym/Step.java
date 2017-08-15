package org.deeplearning4j.gym;

import org.json.JSONObject;

import java.beans.ConstructorProperties;

/**
 * @param <I> type of observation
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/6/16.
 *
 *  StepReply is the container for the data returned after each step(action).
 */
public class Step<I> {
    public final I input;
    public final double reward;
    public final boolean done;
    public final JSONObject data;

    @ConstructorProperties({"observation", "reward", "done", "info"})
    public Step(I input, double reward, boolean done, JSONObject data) {
        this.input = input;
        this.reward = reward;
        this.done = done;
        this.data = data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Step)) {
            return false;
        } else {
            Step other;
            label40: {
                other = (Step)o;
                Object this$observation = this.input;
                Object other$observation = other.input;
                if (this$observation == null) {
                    if (other$observation == null) {
                        break label40;
                    }
                } else if (this$observation.equals(other$observation)) {
                    break label40;
                }

                return false;
            }

            if (Double.compare(this.reward, other.reward) != 0) {
                return false;
            } else if (this.done != other.done) {
                return false;
            } else {
                Object this$info = this.data;
                Object other$info = other.data;
                if (this$info == null) {
                    if (other$info != null) {
                        return false;
                    }
                } else if (!this$info.equals(other$info)) {
                    return false;
                }

                return true;
            }
        }
    }

    public int hashCode() {
        int _result = 1;
        Object $observation = this.input;
        int result = _result * 59 + ($observation == null ? 43 : $observation.hashCode());
        long $reward = Double.doubleToLongBits(this.reward);
        result = result * 59 + (int)($reward >>> 32 ^ $reward);
        result = result * 59 + (this.done ? 79 : 97);
        Object $info = this.data;
        result = result * 59 + ($info == null ? 43 : $info.hashCode());
        return result;
    }

    public String toString() {
        return "Step(in=" + this.input + ", reward=" + this.reward + ", done=" + this.done + ", data=" + this.data + ")";
    }
}
