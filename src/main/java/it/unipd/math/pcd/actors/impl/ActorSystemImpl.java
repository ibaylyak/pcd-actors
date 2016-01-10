package it.unipd.math.pcd.actors.impl;
import it.unipd.math.pcd.actors.*;
/**
 * Created by igor on 10/01/16.
 */
public class ActorSystemImpl extends AbsActorSystem {

    private class ActorRefImplLocal<T extends Message> implements ActorRef<T> {

        @Override
        public void send(T message, ActorRef to) {

            ((AbsActor)getActor(to)).insertMailBox(message, this);
        }

        @Override
        public int compareTo(ActorRef o) {
            final int LESS = -1;
            final int EQUAL = 0;
            final int GREATER = 1;
            return (hashCode() < o.hashCode() ? LESS : (hashCode() > o.hashCode() ? GREATER : EQUAL));
        }
    }




    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if (mode == ActorMode.LOCAL) {
            return new ActorRefImplLocal();
        } else {
            //La parte del framework dedicata alla distribuzione non implementata
            return null;
        }
    }
}
