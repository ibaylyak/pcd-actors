package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.impl.ActorSystemImpl;
import it.unipd.math.pcd.actors.utils.actors.StoreActor;
import it.unipd.math.pcd.actors.utils.actors.TrivialActor;
import it.unipd.math.pcd.actors.utils.actors.counter.CounterActor;
import it.unipd.math.pcd.actors.utils.actors.ping.pong.PingPongActor;
import it.unipd.math.pcd.actors.utils.messages.StoreMessage;
import it.unipd.math.pcd.actors.utils.messages.counter.Increment;
import it.unipd.math.pcd.actors.utils.messages.ping.pong.PingMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by igor on 12/01/16.
 * version 1.0
 */
public class ActorSystemSingletonTest {
    private ActorSystem system;

    /**
     * Initializes the {@code system} with a concrete implementation before each test.
     */
    @Before
    public void init() throws IllegalAccessException {
        system =ActorSystemImpl.getIstance();
    }

    @Test(expected = IllegalAccessException.class)
    public void shouldIllegalAccessException() throws IllegalAccessException {
            ActorSystemImpl SecondSystem = new ActorSystemImpl();
    }

}
