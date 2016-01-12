package it.unipd.math.pcd.actors.impl;
import it.unipd.math.pcd.actors.*;

/**
 * Created by igor on 10/01/16.
 * version 1.0
 */
public final class ActorSystemImpl extends AbsActorSystem {
    public static ActorSystemImpl istance=null;
    public ActorSystemImpl() throws IllegalAccessException {
        if(istance!=null) throw new IllegalAccessException("Use method getIstance()");
        istance=this;
    }
    public ActorSystemImpl getIstance(){return istance;}
    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if (mode == ActorMode.LOCAL) return new ActorRefImplLocal<>();
        else {
            //La parte del framework dedicata alla distribuzione non implementata
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
            final Actor actorIstance = ActorSystemImpl.this.getActor(to);
            ((AbsActor<T>)actorIstance).insertMailBox(message, this);
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
