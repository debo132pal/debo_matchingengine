package exchange.test.util;

import exchange.core.Router;
import exchange.events.*;
import exchange.sessions.ISession;
import exchange.util.IMessageHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TestClientSession implements IMessageHandler, ISession {
    private LinkedBlockingQueue<Event> msgQueue = new LinkedBlockingQueue();
    private Router router;
    private static int MAX_TRY = 100000000;
    @Override
    public void sumbit(Event msg) {
        msgQueue.offer(msg);
    }

    public Event getNextMsg() {
        return msgQueue.poll();
    }

    public List<Event> getMessagesInQueue( ) throws InterruptedException {
        List<Event>  msgReceived = new LinkedList<>();
        int tries = 0;
        while( tries < MAX_TRY ) {
            Event e = msgQueue.poll();
            if( e != null ) {
                msgReceived.add( e );
            }
            tries++;
        }
        return msgReceived;
    }

    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    public void sendNewOrder(NewOrder ord) {
        router.getMessageHandler( ord.getInstrument() ).sumbit(ord);
    }

    public void sendAmendOrder(AmendOrder ord) {
        router.getMessageHandler( ord.getInstrument() ).sumbit(ord);
    }

    public void sendCancelOrder(CancelOrder ord) {
        router.getMessageHandler( ord.getInstrument() ).sumbit(ord);
    }

}
