package org.deeplearning4j.gym.test;

import org.deeplearning4j.gym.Client;
import org.deeplearning4j.rl4j.model.action.ActionSpace;
import org.deeplearning4j.rl4j.model.in.InputSpace;
import org.junit.Test;

public class ClientMain {

    @Test
    public void testClient() {

        //test

        String url = "http://localhost:5000";
        String env =
                "CartPole-v0";
        //"Pong-v0";
        //"SpaceInvaders-v0";
        //"Copy-v0";

        boolean render = true;

        Client gym = Client.connect(url, env, render);
        System.out.println("Environments: " + gym.environments());
        //client.monitorStart(testDir, true, false);

        int episodeCount = 4;
        int maxSteps = 200;
        int reward = 0;


        for (int e = 0; e < episodeCount; e++) {
            reward += gym.episode(maxSteps, (i) -> gym.actions.random());
        }

        //client.reset();
        gym.close();
        //client.monitorClose();
        //client.serverShutdown();
        //client.upload(testDir, "YOUR_OPENAI_GYM_API_KEY");


    }

}
