/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;
import it.unipd.math.pcd.actors.exceptions.UnsupportedMessageException;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message>  implements  Actor<T>, Comparable<AbsActor> {
    /**
     *
     * @param toStop flag variable that indicates to Actor when to stop receiving massages from @code mailingBox
     * @param mailingBox contains a message queue to process
     *
     */

    private volatile Boolean interupted = false;
    private MessageDispatcher myDispatcher;
    protected AbsActor(){
        myDispatcher= new MessageDispatcher();

    }






    /**
     * Self-reference of the actor
     */
    protected ActorRef<T> self;

    /**
     * Sender of the current message
     */
    protected ActorRef sender;

    /**
     * Sets the self-referece.
     *
     * @param self The reference to itself
     * @return The actor.
     */
    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

    protected void finalize()throws Throwable{
            interupted=true;
            try {
                    myDispatcher.finalize();
            } finally {
                super.finalize();
            }

    }


    public boolean isStoped(){
            return interupted;
        }
    public void actorMessageListener(T message, ActorRef from){
        myDispatcher.processMessage(message,from);
    }
    @Override
    public int compareTo( AbsActor o) {
        /**
         * @param equals indicates that a reference must be equal to itself
         * @param disequals indicates that a reference must be a different to itself
         */
        final int equals = 0;
        final int disequals = 1;
        return hashCode() == o.hashCode() ? equals : disequals;
    }
private  class MessageDispatcher {
    private final Queue<Pair<T, ActorRef>> mailingBox = new LinkedList<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    public void processMessage(T message, ActorRef from) {
        if (!interupted) {
            executor.submit(new PushMessage(message, from));
            executor.submit(new PopMessage());
        } else throw new NoSuchActorException("Actor has been stoped");


    }
    private class PushMessage implements Runnable{
        private T message;
        private ActorRef from;
        public PushMessage(T message, ActorRef from){
            this.message=message;
            this.from=from;
        }
        @Override
        public void run() {
            synchronized(mailingBox) {
                mailingBox.add(new Pair<>(message, from));
                mailingBox.notifyAll();
            }
        }
    }
    private class PopMessage implements Runnable {
        @Override
        public void run() {

            Pair<T, ActorRef> box;
            synchronized (mailingBox) {
                while (mailingBox.isEmpty()) {
                    try {
                        mailingBox.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                box = mailingBox.remove();
            }

            try {
                sender = box.getValue();
                receive(box.getKey());
            } catch (ClassCastException e) {
                throw new UnsupportedMessageException(box.getKey());
            }
            interupted.notifyAll();

        }

    }
    protected void finalize()throws Throwable{
        try {
            while(!executor.isTerminated()){
                mailingBox.notifyAll();
                interupted.wait();
            }
        } finally {
            super.finalize();
        }

    }
}
}
