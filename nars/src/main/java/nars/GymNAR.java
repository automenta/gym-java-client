package nars;

import jcog.math.FirstOrderDifferenceFloat;
import jcog.math.FloatNormalized;
import nars.concept.ScalarConcepts;
import org.deeplearning4j.gym.Gym;
import org.deeplearning4j.gym.Step;
import org.deeplearning4j.rl4j.model.action.BoxActions;
import org.deeplearning4j.rl4j.model.action.DiscreteActions;
import org.deeplearning4j.rl4j.model.in.BoxInputs;

public abstract class GymNAR<I, A> extends NAgentX {

    public final Gym<I, A> gym;
    private I next;

    public GymNAR(NAR nar, String url, String environment) {
        super("", nar);

        gym = Gym.connect(url, environment, true);
        next = gym.reset();

    }

    public static void main(String[] args) {

        float fps = 32f;
        int digitization = 4;

        NAgentX.runRT(n -> {
            return new GymNAR(n, "http://localhost:5000",
                //"CartPole-v0"
        "Pendulum-v0"
            ) {

                double[] feel;
                Object move;

                {
                    senseNumber($.func("happy", $.the("i"), $.the("o")),
                                    new FloatNormalized(() -> reward).decay(0.95f),
                                    digitization, ScalarConcepts.FuzzyTriangle
                            );
                    senseNumber($.func("dHappy", $.the("i"), $.the("o")),
                                    new FloatNormalized( new FirstOrderDifferenceFloat(n::time, () -> reward) ).decay(0.95f),
                                    digitization, ScalarConcepts.FuzzyTriangle
                            );

                    if (gym.inputs instanceof BoxInputs) {
                        int in = ((BoxInputs)gym.inputs).volume;
                        feel = new double[in];
                        for (int j = 0; j < in; j++) {
                            int jj = j;

                            senseNumber(in > 1 ?
                                            $.inh($.the(jj), $.the("i") )
                                            //$.p($.the(jj), $.the("i") )
                                            //$.the("i" + jj)
                                            : $.the("i" ),
                                    new FloatNormalized(() -> (float) feel[jj]),
                                    digitization, ScalarConcepts.FuzzyTriangle
                            );
                        }
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    if ((gym.actions instanceof DiscreteActions) &&  (((DiscreteActions)(gym.actions)).size==2)) {
                        move = 0;
                        actionToggle($.the("o"), () -> move = 0, () -> move = 1);
                    } else if (gym.actions instanceof BoxActions) {
                        BoxActions b = (BoxActions) gym.actions;
                        int[] sh = b.shape;
                        assert(sh.length == 1); //1 for now; can be linearized or whatever later
                        int ss = sh[0];
                        move = new float[ss];
                        for (int o = 0; o < ss; o++) {

                            float min = b.min[o];
                            float range = b.max[o] - min;
                            final int oo = o;
                            actionUnipolar(ss > 1 ? $.inh( $.the( o), $.the("o") ) : $.the("o"), (x) -> {

                                float y = x * range + min;
                                ((float[])move)[oo] = y;

                                return x;
                            });
                        }
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    //n.log();
                }

                @Override
                public Object decide(Object next) {
                    if (next instanceof double[]) {
                        System.arraycopy(next, 0, feel, 0, feel.length);
                    }
                    return this.move;
                }

                @Override
                protected double terminalReward(double reward) {
                    return -1;
                }
            };
        }, fps);

    }

    abstract public A decide(I next);

    @Override
    protected float act() {
        Step<I> step = gym.run(decide(next));

        double r;
        if (step.done) {
            next = gym.reset();
            r = terminalReward(step.reward);
        } else {
            next = step.input;
            r = step.reward;
        }

        System.out.println(r + " " + step);
        return (float) r;
    }

    /** the reward input upon termination, which may need to be set negative depending on the env */
    protected double terminalReward(double reward) {
        return reward;
    }


}
