package exchange.core;

import exchange.events.*;
import exchange.util.IMessageHandler;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class Processor implements Runnable, IMessageHandler, IMatchingProcessor {

    private LinkedBlockingQueue<Event> inBoundQueue = new LinkedBlockingQueue<>();
    private volatile boolean stopThread = false;
    String  id;
    public Processor( int idx ){
        id =  "Processor" + idx;
    }

    @Override
    public void handle(Event msg) {
        switch (msg.getMsgType()) {
            case NewOrder:
                handle((NewOrder) msg);
                break;
            case AmendOrder:
                handle((AmendOrder) msg);
            case CancelOrder:
                handle((CancelOrder) msg);
            default:
        }
    }

    @Override
    public void run() {
        while (!stopThread) {
            try {
                handle(inBoundQueue.poll());
            } catch (Exception e) {
                //log
            }
        }
    }

    @Override
    public void sumbit(Event msg) {
        inBoundQueue.offer(msg);
    }

    @Override
    public void stop() {
        stopThread = true;
    }
}
