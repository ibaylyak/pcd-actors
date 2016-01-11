package it.unipd.math.pcd.actors.impl;
import com.sun.istack.internal.NotNull;
import it.unipd.math.pcd.actors.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by igor on 10/01/16.
 */
public final class ActorSystemImpl extends AbsActorSystem {
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode){
        ActorRef<?> reference =super.actorOf(actor,mode);

        /**
         * @param actorIstance used to get the underlying actor associated to the reference
         */
        final Actor actorIstance = this.getActor(reference);
        executor.submit(((AbsActor<?>)actorIstance));

        return reference;
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }
    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if (mode == ActorMode.LOCAL) return new ActorRefImplLocal<>();
        else {
            //La parte del framework dedicata alla distribuzione non implementata
            throw new IllegalArgumentException();
        }
    }
    @Override
    public void stop() {
        executor.shutdown();
    }
    @Override
    public void stop(ActorRef<?> actor) {
        ((AbsActor<?>)getActor(actor)).stopActor();
    }
    private  class ActorRefImplLocal<T extends Message> implements ActorRef<T> {
        /**
         *
         * @class ActorRefImplLocal internal implementation of ActorRef
         *
         */
        @Override
        public void send(T message, ActorRef to) {
            ((AbsActor<T>)getUnderlyingActor(ActorSystemImpl.this)).insertMailBox(message, this);
        }

        @Override
        public int compareTo(@NotNull ActorRef o) {
            /**
             * @param equals indicates that a reference must be equal to itself
             * @param disequals indicates that a reference must be a different to itself
             */
            final int equals = 0;
            final int disequals = 1;
            return hashCode() == o.hashCode() ? equals : disequals;
        }
        /**
         * Returns the {@link Actor} associated to the internal reference.
         * @param system Actor system from which retrieving the actor
         *
         * @return An actor
         */
        public Actor getUnderlyingActor(ActorSystem system) {
            if(system instanceof ActorSystemImpl) {
                ActorSystemImpl instanceOfSystem = (ActorSystemImpl) system;
                return instanceOfSystem.getActor(this);
            }else{
                return null;
            }
        }
    }
}
