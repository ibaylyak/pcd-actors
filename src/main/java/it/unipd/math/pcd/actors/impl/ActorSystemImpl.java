package it.unipd.math.pcd.actors.impl;
import it.unipd.math.pcd.actors.*;
import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by igor on 10/01/16.
 * version 1.0
 */
public final class ActorSystemImpl extends AbsActorSystem {
    private static final ActorSystemImpl instance = new ActorSystemImpl();
    public ActorSystemImpl getInstance(){return instance;}
    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode){
        if(this==instance) {
            return super.actorOf(actor,mode);
        }else{
            return instance.actorOf(actor,mode);
        }

    }
    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor){
            return instance.actorOf(actor,ActorMode.LOCAL);
    }

    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if(this==instance) {
            if (mode == ActorMode.LOCAL) return new ActorRefImplLocal<>();
            else {
                //La parte del framework dedicata alla distribuzione non implementata
                throw new IllegalArgumentException();
            }
        }else{
            return instance.createActorReference(mode);
        }
    }

    public Actor getActorInstance(ActorRef ref) {

        if(this==instance) {
            return super.getActor(ref);
        }else{
            return instance.getActor(ref);
        }
    }
    @Override
    public void stop(ActorRef<?> actor){
                if(this==instance) {
                    super.stop(actor);
                }else{
                    instance.stop(actor);
                }
    }
    @Override
    public void stop(){
        if(this==instance) {
            super.stop();
        }else{
            instance.stop();
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
                final Actor actorIstance = ActorSystemImpl.instance.getActor(to);
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
