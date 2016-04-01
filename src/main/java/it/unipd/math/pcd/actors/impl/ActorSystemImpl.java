package it.unipd.math.pcd.actors.impl;
import it.unipd.math.pcd.actors.*;
import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by igor on 10/01/16.
 * version 1.0
 */
public final class ActorSystemImpl extends AbsActorSystem {


    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if (mode == ActorMode.LOCAL) {
            return new ActorRefImplLocal();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private  class ActorRefImplLocal<T extends Message> implements ActorRef<T> {
        /**
         *ActorRefImplLocal internal implementation of ActorRef
         *
         */
        @Override
        public void send(T message, ActorRef to) {
            try {
                final Actor actorIstance = ActorSystemImpl.this.getActor(to);
                ((AbsActor<T>) actorIstance).actorMessageListener(message, this);
            }catch (NoSuchActorException e){ throw e;}
        }

        @Override
        public int compareTo( ActorRef o) {
            /**
             * @param equals indicates that a reference must be equal to itself
             * @param disequals indicates that a reference must be a different to itself
             */
            final int equals = 0;
            final int disequals = 1;
            return hashCode() == o.hashCode() ? equals : disequals;
        }

    }
}
