package org.deeplearning4j.gym;


import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.deeplearning4j.rl4j.model.action.ActionSpace;
import org.deeplearning4j.rl4j.model.action.DiscreteActions;
import org.deeplearning4j.rl4j.model.action.HighLowDiscreteActions;
import org.deeplearning4j.rl4j.model.action.TupleActions;
import org.deeplearning4j.rl4j.model.in.InputSpace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.ConstructorProperties;
import java.util.function.Function;

/**
 * @param <I> Observation type
 * @param <A> Action type
 * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/6/16.
 * <p>
 * A client represent an active connection to a specific instance of an environment on a rl4j-http-api server.
 * for API specification
 * @see <a href="https://github.com/openai/gym-http-api#api-specification">https://github.com/openai/gym-http-api#api-specification</a>
 */
public class Client<I, A> {

    private static final Logger log = LoggerFactory.getLogger(Client.class);
    public static String V1_ROOT = "/v1";
    public static String ENVS_ROOT;
    public static String MONITOR_START;
    public static String MONITOR_CLOSE;
    public static String CLOSE;
    public static String RESET;
    public static String SHUTDOWN;
    public static String UPLOAD;
    public static String STEP;
    public static String OBSERVATION_SPACE;
    public static String ACTION_SPACE;

    public final String url;

    /**
     * environment ID
     */
    private final String env;

    /**
     * experiment instance ID
     */
    private final String id;

    public final InputSpace<I> inputs;
    public final ActionSpace<A> actions;
    private final boolean rendered;

    static JSONObject environments(String url) {
        JSONObject reply = ClientUtils.get(url + ENVS_ROOT);
        return reply.getJSONObject("all_envs");
    }

    public static void shutdownServer(String url) {
        ClientUtils.post(url + ENVS_ROOT + SHUTDOWN, new JSONObject());
    }

    public static <I, A> Client<I, A> connect(String url, String envId, boolean render) {

        JSONObject body = new JSONObject().put("env_id", envId);
        JSONObject reply = ClientUtils.post(url + ENVS_ROOT, body).getObject();

        String instanceId;

        try {
            instanceId = reply.getString("instance_id");
        } catch (JSONException e) {
            throw new RuntimeException("Environment id not found", e);
        }

        InputSpace observationSpace = fetchObservationSpace(url, instanceId);
        ActionSpace<A> actionSpace = fetchActionSpace(url, instanceId);

        return new Client<>(url, envId, instanceId, observationSpace, actionSpace, render);

    }

    static <A extends ActionSpace> A fetchActionSpace(String url, String instanceId) {

        final JSONObject reply = ClientUtils.get(url + ENVS_ROOT + instanceId + ACTION_SPACE);
        JSONObject info = reply.getJSONObject("info");
        return actionSpace(info);
    }

    private static <A extends ActionSpace> A actionSpace(JSONObject info) {
        String infoName = info.getString("name");

        switch (infoName) {
            case "Discrete":
                return (A) new DiscreteActions(info.getInt("n"));
            case "HighLow":
                int numRows = info.getInt("num_rows");
                int size = 3 * numRows;
                JSONArray matrixJson = info.getJSONArray("matrix");
                INDArray matrix = Nd4j.create(numRows, 3);
                for (int i = 0; i < size; i++) {
                    matrix.putScalar(i, matrixJson.getDouble(i));
                }
                matrix.reshape(numRows, 3);
                return (A) new HighLowDiscreteActions(matrix);
            case "Tuple":
                return (A) new TupleActions(Lists.newArrayList(
                        info.getJSONArray("spaces").iterator()).stream()
                            .map(x -> (DiscreteActions)actionSpace(((JSONObject)x)))
                            .toArray(DiscreteActions[]::new));
            default:
                throw new RuntimeException("Unknown action model " + infoName + " in " + info);
        }
    }

    public static InputSpace fetchObservationSpace(String url, String instanceId) {
        JSONObject reply = ClientUtils.get(url + ENVS_ROOT + instanceId + OBSERVATION_SPACE);
        JSONObject info = reply.getJSONObject("info");
        return InputSpace.build(info);
    }

    public JSONObject environments() {
        return environments(this.url);
    }

    public Step<I> run(A action) {
        JSONObject body = new JSONObject().put("action",
                this.actions.encode(action)).put("render", this.rendered);
        JSONObject reply = ClientUtils.post(this.url + ENVS_ROOT + this.id + STEP, body).getObject();
        I observation = this.inputs.get(reply.get("observation"));
        double reward = reply.getDouble("reward");
        boolean done = reply.getBoolean("done");
        JSONObject info = reply.getJSONObject("info");
        return new Step<>(observation, reward, done, info);
    }

    public I reset() {
        JsonNode resetRep = ClientUtils.post(this.url + ENVS_ROOT + this.id + RESET, new JSONObject());
        return this.inputs.get(resetRep.getObject().get("observation")); //getValue(resetRep.getObject(), "observation");

    }

    public void monitorStart(String directory, boolean force, boolean resume) {
        JSONObject json = (new JSONObject()).put("directory", directory).put("force", force).put("resume", resume);
        this.monitorStartPost(json);
    }

    private void monitorStartPost(JSONObject json) {
        ClientUtils.post(this.url + ENVS_ROOT + this.id + MONITOR_START, json);
    }

    public void monitorClose() {
        ClientUtils.post(this.url + ENVS_ROOT + this.id + MONITOR_CLOSE, new JSONObject());
    }

    public void close() {
        ClientUtils.post(this.url + ENVS_ROOT + this.id + CLOSE, new JSONObject());
    }

    public void upload(String trainingDir, String apiKey, String algorithmId) {
        JSONObject json = (new JSONObject()).put("training_dir", trainingDir).put("api_key", apiKey).put("algorithm_id", algorithmId);
        this.uploadPost(json);
    }

    public void upload(String trainingDir, String apiKey) {
        JSONObject json = (new JSONObject()).put("training_dir", trainingDir).put("api_key", apiKey);
        this.uploadPost(json);
    }

    private void uploadPost(JSONObject json) {
        try {
            ClientUtils.post(this.url + V1_ROOT + UPLOAD, json);
        } catch (RuntimeException var3) {
            log.error("Impossible to upload: Wrong API key?");
        }

    }

    public void serverShutdown() {
        shutdownServer(this.url);
    }

    @ConstructorProperties({"url", "envId", "instanceId", "observationSpace", "actionSpace", "render"})
    public Client(String url, String env, String id, InputSpace inputs, ActionSpace<A> actions, boolean rendered) {
        this.url = url;
        this.env = env;
        this.id = id;
        this.inputs = inputs;
        this.actions = actions;
        this.rendered = rendered;
    }

    public String url() {
        return this.url;
    }

    public String env() {
        return this.env;
    }

    public String id() {
        return this.id;
    }

    public boolean rendered() {
        return this.rendered;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Client)) {
            return false;
        } else {
            Client other;
            label72:
            {
                other = (Client) o;
                Object this$url = this.url();
                Object other$url = other.url();
                if (this$url == null) {
                    if (other$url == null) {
                        break label72;
                    }
                } else if (this$url.equals(other$url)) {
                    break label72;
                }

                return false;
            }

            label65:
            {
                Object this$envId = this.env();
                Object other$envId = other.env();
                if (this$envId == null) {
                    if (other$envId == null) {
                        break label65;
                    }
                } else if (this$envId.equals(other$envId)) {
                    break label65;
                }

                return false;
            }

            Object this$instanceId = this.id();
            Object other$instanceId = other.id();
            if (this$instanceId == null) {
                if (other$instanceId != null) {
                    return false;
                }
            } else if (!this$instanceId.equals(other$instanceId)) {
                return false;
            }

            Object this$observationSpace = this.inputs;
            Object other$observationSpace = other.inputs;
            if (this$observationSpace == null) {
                if (other$observationSpace != null) {
                    return false;
                }
            } else if (!this$observationSpace.equals(other$observationSpace)) {
                return false;
            }

            label44:
            {
                Object this$actionSpace = this.actions;
                Object other$actionSpace = other.actions;
                if (this$actionSpace == null) {
                    if (other$actionSpace == null) {
                        break label44;
                    }
                } else if (this$actionSpace.equals(other$actionSpace)) {
                    break label44;
                }

                return false;
            }

            if (this.rendered() != other.rendered()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public int hashCode() {
        int _result = 1;
        Object $url = this.url();
        int result = _result * 59 + ($url == null ? 43 : $url.hashCode());
        Object $envId = this.env();
        result = result * 59 + ($envId == null ? 43 : $envId.hashCode());
        Object $instanceId = this.id();
        result = result * 59 + ($instanceId == null ? 43 : $instanceId.hashCode());
        Object $observationSpace = this.inputs;
        result = result * 59 + ($observationSpace == null ? 43 : $observationSpace.hashCode());
        Object $actionSpace = this.actions;
        result = result * 59 + ($actionSpace == null ? 43 : $actionSpace.hashCode());
        result = result * 59 + (this.rendered() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "Client(url=" + this.url() + ", envId=" + this.env() + ", instanceId=" + this.id() + ", observationSpace=" + this.inputs + ", actionSpace=" + this.actions + ", render=" + this.rendered() + ")";
    }

    static {
        ENVS_ROOT = V1_ROOT + "/envs/";
        MONITOR_START = "/monitor/start/";
        MONITOR_CLOSE = "/monitor/close/";
        CLOSE = "/close/";
        RESET = "/reset/";
        SHUTDOWN = "/shutdown/";
        UPLOAD = "/upload/";
        STEP = "/step/";
        OBSERVATION_SPACE = "/observation_space/";
        ACTION_SPACE = "/action_space/";
    }

    public double episode(int maxSteps, Function<I, A> agent) {
        double rewardSum = 0;

        I next = reset();

        for (int j = 0; j < maxSteps; j++) {

            Step<I> step = run(agent.apply(next));
            rewardSum += step.reward;
            next = step.input;

            if (step.done) {
                break;
            }
        }

        return rewardSum;
    }

    /**
     * @author rubenfiszel (ruben.fiszel@epfl.ch) on 7/8/16.
     * <p>
     * ClientUtils contain the utility methods to post and get data from the server REST API through the library unirest.
     */
    public static class ClientUtils {

        static public JsonNode post(String url, JSONObject json) {
            HttpResponse<JsonNode> jsonResponse = null;

            RequestBodyEntity resp = Unirest.post(url).header("content-type", "application/json").body(json);
            try {
                jsonResponse = resp.asJson();
            } catch (UnirestException e) {
                crash(e, resp.getBody());
            }

            return jsonResponse.getBody();
        }



        static public JSONObject get(String url) {
            HttpResponse<JsonNode> jsonResponse = null;

            try {
                jsonResponse = Unirest.get(url).header("content-type", "application/json").asJson();
            } catch (UnirestException e) {
                crash(e);
            }

            checkReply(jsonResponse, url);

            return jsonResponse.getBody().getObject();
        }


        static public void checkReply(HttpResponse<JsonNode> res, String url) {
            if (res.getBody() == null)
                throw new RuntimeException("Invalid reply at: " + url);
        }

        static public void crash(UnirestException e) {
            //if couldn't parse json
            if (e.getCause().getCause().getCause() instanceof JSONException)
                throw new RuntimeException("Couldn't parse json reply.", e);
            else
                throw new RuntimeException("Connection error", e);
        }
        private static void crash(UnirestException e, Object body) {
            System.err.println(body);
            crash(e);
        }


    }
}
