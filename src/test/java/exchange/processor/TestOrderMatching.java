package exchange.processor;

import exchange.core.IDGenerator;
import exchange.core.MatchingProcessorImpl;
import exchange.core.Router;
import exchange.events.*;
import exchange.test.util.TestClientSession;
import exchange.test.util.TestMatchingEngine;
import exchange.util.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestOrderMatching {

    private MatchingProcessorImpl matchingProcessor;
    private String testInstrument = "BTC/USD";

    private TestMatchingEngine testMatchingEngine;
    private TestClientSession clientSession;

    public void setUp() {
        testMatchingEngine = new TestMatchingEngine();
        matchingProcessor = testMatchingEngine.getMatchingProcessor();
        IDGenerator idGenerator = new IDGenerator("AZ");
        matchingProcessor.setIdGenerator(idGenerator);
        List<String> instr = Arrays.asList(new String[]{"BTC/USD", "XYZ/USD"});
        matchingProcessor.setUpOrderBookForInstruments(instr);
        clientSession = new TestClientSession();
        Router router = new Router();
        HashMap<String, IMessageHandler> routes = new HashMap<>();
        routes.put("BTC/USD", matchingProcessor);
        routes.put("XYZ/USD", matchingProcessor);
        router.setUpMessageHandler(routes);
        clientSession.setRouter(router);
    }

    @Test
    public void testLimitOrderMatching() throws InterruptedException {
        setUp();
        testMatchingEngine.start();

        NewOrder nosB1 = createLimitOrder("BUY1", Side.BUY, 100, 100.1, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB1);
        NewOrder nosB2 = createLimitOrder("BUY2", Side.BUY, 200, 99.99, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB2);
        NewOrder nosB3 = createLimitOrder("BUY3", Side.BUY, 300, 99.79, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB3);

        // Sell Order with Qty=150 and Price = 99.85
        NewOrder nosS1 = createLimitOrder("SELL1", Side.SELL, 150, 99.85, System.currentTimeMillis());
        clientSession.sendNewOrder(nosS1);

        List<Event> msg = clientSession.getMessagesInQueue();
        assertEquals(4, msg.size());

        // Top of the book buy side ( qty == 100 and price 100.1 )
        Trade sellSide = (Trade) msg.get(0);
        assertEquals(sellSide.getCustOrderID(), nosS1.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 100);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(100.1).value());
        sellSide = (Trade) msg.get(2);

        // Top of the book buy side ( qty == 50 and price 99.99 ).
        assertEquals(sellSide.getCustOrderID(), nosS1.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 50);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(99.99).value());


        //Now top of the book buy side is( qty = 150 and Price 99.99 )
        //Test Send a sell Order

        // Sell Order with Qty=500 and Price = 99.69
        NewOrder nosS2 = createLimitOrder("SELL2", Side.SELL, 500, 99.69, System.currentTimeMillis());
        clientSession.sendNewOrder(nosS2);

        msg = clientSession.getMessagesInQueue();
        assertEquals(4, msg.size());

        sellSide = (Trade) msg.get(0);
        assertEquals(sellSide.getCustOrderID(), nosS2.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 150);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(99.99).value());

        sellSide = (Trade) msg.get(2);


        assertEquals(sellSide.getCustOrderID(), nosS2.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 300);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(99.79).value());

        // Top of the book sell side ( qty == 50 and price 99.69 )
        NewOrder nosB4 = createLimitOrder("BUY4", Side.BUY, 300, 99.79, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB4);

        msg = clientSession.getMessagesInQueue();
        assertEquals(2, msg.size());

        Trade buySide = (Trade) msg.get(0);
        assertEquals(buySide.getCustOrderID(), nosB4.getCustOrdID());
        assertEquals(buySide.getLastTradedQty(), 50);
        assertEquals(buySide.getLastTradedPrice().value(), new Price(99.69).value());

        // Top of the book buy side ( qty == 250 and price 99.79 )
        // Test two Sell Orders ( qty = 50  and qty = 500 )
        NewOrder nosS3 = createLimitOrder("SELL3", Side.SELL, 50, 99.69, System.currentTimeMillis());
        clientSession.sendNewOrder(nosS3);

        NewOrder nosS4 = createLimitOrder("SELL4", Side.SELL, 500, 99.75, System.currentTimeMillis());
        clientSession.sendNewOrder(nosS4);

        msg = clientSession.getMessagesInQueue();
        assertEquals(4, msg.size());

        sellSide = (Trade) msg.get(0);
        assertEquals(sellSide.getCustOrderID(), nosS3.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 50);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(99.79).value());

        sellSide = (Trade) msg.get(2);
        assertEquals(sellSide.getCustOrderID(), nosS4.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 200);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(99.79).value());

        // Top of the book sell side ( qty == 250 and price 99.75 )
        //Test Buy order  qty = 250 ( OrdStatus should be Filled for buy and sell side trades )
        NewOrder nosB5 = createLimitOrder("BUY5", Side.BUY, 300, 99.79, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB5);

        msg = clientSession.getMessagesInQueue();
        assertEquals(2, msg.size());

        buySide = (Trade) msg.get(0);
        assertEquals(buySide.getCustOrderID(), nosB5.getCustOrdID());
        assertEquals(buySide.getLastTradedQty(), 300);
        assertEquals(buySide.getLastTradedPrice().value(), new Price(99.75).value());
        assertEquals(buySide.getOrdStatus(), OrdStatus.Fill);

        sellSide = (Trade) msg.get(1);
        assertEquals(sellSide.getCustOrderID(), nosS4.getCustOrdID());
        assertEquals(sellSide.getLastTradedQty(), 300);
        assertEquals(sellSide.getLastTradedPrice().value(), new Price(99.75).value());
        assertEquals(sellSide.getOrdStatus(), OrdStatus.Fill);

        testMatchingEngine.stop();
    }

    @Test
    public void testLimitOrderCancel() throws InterruptedException {
        setUp();
        testMatchingEngine.start();

        NewOrder nosBuy1 = createLimitOrder("BUY1", Side.BUY, 100, 100.1, System.currentTimeMillis());
        clientSession.sendNewOrder(nosBuy1);
        NewOrder nos2 = createLimitOrder("SELL1", Side.SELL, 50, 100.05, System.currentTimeMillis());
        clientSession.sendNewOrder(nos2);

        List<Event> msg = clientSession.getMessagesInQueue();
        Trade t1 = (Trade) msg.get(0);
        assertEquals(t1.getCustOrderID(), nos2.getCustOrdID());
        assertEquals(t1.getLastTradedPrice().value(), new Price(100.1).value());
        assertEquals(t1.getLastTradedQty(), 50);
        assertEquals(t1.getOrdStatus(), OrdStatus.Fill);

        Trade t2 = (Trade) msg.get(1);
        assertEquals(t2.getCustOrderID(), nosBuy1.getCustOrdID());
        assertEquals(t2.getLastTradedPrice().value(), new Price(100.1).value());
        assertEquals(t2.getLastTradedQty(), 50);
        assertEquals(t2.getOrdStatus(), OrdStatus.Partial);

        String ordID = t2.getOrdID();

        CancelOrder cancelMsg = createCancelMsg(ordID, nosBuy1);
        clientSession.sendCancelOrder(cancelMsg);
        List<Event> msgs = clientSession.getMessagesInQueue();
        assertEquals(1, msgs.size());
        Canceled canceled = (Canceled) msgs.get(0);
        assertEquals(canceled.getCanceledQty(), 50);
        assertEquals(canceled.getOrdID(), ordID);

        NewOrder nosB1 = createLimitOrder("BUY1", Side.BUY, 100, 100.1, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB1);
        NewOrder nosB2 = createLimitOrder("BUY2", Side.BUY, 200, 99.99, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB2);
        NewOrder nosB3 = createLimitOrder("BUY3", Side.BUY, 300, 99.79, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB3);

        cancelMsg = createCancelMsg("AZ3", nosB2); //Cancel NOSB2 ( 2nd in the PQ )
        clientSession.sendCancelOrder(cancelMsg);
        msgs = clientSession.getMessagesInQueue();
        assertEquals(1, msgs.size());
        canceled = (Canceled) msgs.get(0);
        assertEquals(canceled.getOrdID(), "AZ3");
        assertEquals(canceled.getCanceledQty(), 200);

        cancelMsg = createCancelMsg("AZ2", nosB1); //Cancel NOSB2 ( 2nd in the PQ )
        clientSession.sendCancelOrder(cancelMsg);
        msgs = clientSession.getMessagesInQueue();
        assertEquals(1, msgs.size());
        canceled = (Canceled) msgs.get(0);
        assertEquals(canceled.getOrdID(), "AZ2");
        assertEquals(canceled.getCanceledQty(), 100);

        nos2 = createLimitOrder("SELL2", Side.SELL, 400, 99.50, System.currentTimeMillis());//Trying to croos Buy@300,99.79
        clientSession.sendNewOrder(nos2);

        msg = clientSession.getMessagesInQueue();
        t1 = (Trade) msg.get(0);
        assertEquals(t1.getCustOrderID(), nos2.getCustOrdID());
        assertEquals(t1.getLastTradedPrice().value(), new Price(99.79).value());
        assertEquals(t1.getLastTradedQty(), 300);
        assertEquals(t1.getOrdStatus(), OrdStatus.Partial);

        t2 = (Trade) msg.get(1);
        assertEquals(t2.getCustOrderID(), nosB3.getCustOrdID());
        assertEquals(t2.getLastTradedPrice().value(), new Price(99.79).value());
        assertEquals(t2.getLastTradedQty(), 300);
        assertEquals(t2.getOrdStatus(), OrdStatus.Fill);

        cancelMsg = createCancelMsg(t1.getOrdID(), nos2);
        clientSession.sendCancelOrder(cancelMsg);
        msgs = clientSession.getMessagesInQueue();
        assertEquals(1, msgs.size());
        canceled = (Canceled) msgs.get(0);
        assertEquals(canceled.getOrdID(), t1.getOrdID());
        assertEquals(canceled.getCanceledQty(), 100);
        testMatchingEngine.stop();

    }

    @Test
    public void testAmendOrderInBook() throws InterruptedException {
        setUp();
        testMatchingEngine.start();

        NewOrder nosB1 = createLimitOrder("BUY1", Side.BUY, 100, 100.1, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB1);
        NewOrder nosB2 = createLimitOrder("BUY2", Side.BUY, 200, 99.99, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB2);
        NewOrder nosB3 = createLimitOrder("BUY3", Side.BUY, 300, 99.79, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB3);

        // Sell Order with Qty=150 and Price = 100.2
        NewOrder nosS1 = createLimitOrder("SELL1", Side.SELL, 150, 100.2, System.currentTimeMillis());
        clientSession.sendNewOrder(nosS1);

        List<Event> msg = clientSession.getMessagesInQueue();
        assertEquals(0, msg.size()); //There is no match.

        //Test Amend the last buy order in the buy side book ( BUY3 )

        AmendOrder amend = createAmendMsg("AZ2", nosB3, 100.3);
        clientSession.sendAmendOrder(amend);
        msg = clientSession.getMessagesInQueue();
        assertEquals(3, msg.size());
        Amended amended = (Amended) msg.get(0);
        assertEquals(amend.getPrice().value(), new Price(100.3).value());

        Trade t1 = (Trade) msg.get(1);
        assertEquals(t1.getCustOrderID(), nosB3.getCustOrdID());
        assertEquals(t1.getLastTradedPrice().value(), new Price(100.2).value());

        Trade t2 = (Trade) msg.get(2);
        assertEquals(t2.getCustOrderID(), nosS1.getCustOrdID());
        assertEquals(t2.getLastTradedPrice().value(), new Price(100.2).value());
        assertEquals(t2.getOrdStatus(), OrdStatus.Fill);
    }


    /*
       The Market order scrap  all the  orders on opposite side .And rest of the quatity is canceled
     */
    @Test
    public void testMarketOrder() throws InterruptedException {
        setUp();
        testMatchingEngine.start();

        NewOrder nosB1 = createLimitOrder("BUY1", Side.BUY, 100, 100.1, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB1);
        NewOrder nosB2 = createLimitOrder("BUY2", Side.BUY, 200, 99.99, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB2);
        NewOrder nosB3 = createLimitOrder("BUY3", Side.BUY, 300, 99.79, System.currentTimeMillis());
        clientSession.sendNewOrder(nosB3);

        // Sell Order with Qty=150 and Market Order
        NewOrder nosS1 = createMarketOrder("SELL1", Side.SELL, 700, System.currentTimeMillis());
        clientSession.sendNewOrder(nosS1);

        List<Event> msg = clientSession.getMessagesInQueue();
        assertEquals(7, msg.size());

        Trade t = ( Trade) msg.get(0);
        assertEquals( t.getLastTradedPrice().value() , new Price(100.1).value() );
        assertEquals( t.getLastTradedQty() , 100 );
        t = ( Trade) msg.get(1);
        assertEquals( t.getLastTradedPrice().value() , new Price(100.1).value() );
        assertEquals( t.getLastTradedQty() , 100 );
        t = ( Trade) msg.get(2);
        assertEquals( t.getLastTradedPrice().value() , new Price(99.99).value() );
        assertEquals( t.getLastTradedQty() , 200 );
        t = ( Trade) msg.get(3);
        assertEquals( t.getLastTradedPrice().value() , new Price(99.99).value() );
        assertEquals( t.getLastTradedQty() , 200 );
        t = ( Trade) msg.get(4);
        assertEquals( t.getLastTradedPrice().value() , new Price(99.79).value() );
        assertEquals( t.getLastTradedQty() , 300 );
        t = ( Trade) msg.get(5);
        assertEquals( t.getLastTradedPrice().value() , new Price(99.79).value() );
        assertEquals( t.getLastTradedQty() , 300 );
        Canceled t1 = ( Canceled) msg.get(6);
        assertEquals( t1.getCanceledQty() , 100 );
    }

    private NewOrder createLimitOrder(String id, Side s, long quantity, double price, long timeStamp) {
        NewOrder nos = new NewOrder();
        nos.setCustOrdID(id);
        nos.setInstrument(testInstrument);
        nos.setOrdType(OrdType.LIMIT);
        nos.setMsgHandler(clientSession);
        nos.setPrice(price);
        nos.setTimeStamp(timeStamp);
        nos.setQty(quantity);
        nos.setSide(s);
        return nos;
    }

    private NewOrder createMarketOrder(String id, Side s, long quantity, long timeStamp) {
        NewOrder nos = new NewOrder();
        nos.setCustOrdID(id);
        nos.setInstrument(testInstrument);
        nos.setOrdType(OrdType.MKT);
        nos.setMsgHandler(clientSession);
        nos.setTimeStamp(timeStamp);
        nos.setQty(quantity);
        nos.setSide(s);
        return nos;
    }



    private AmendOrder createAmendMsg(String ordID, NewOrder nos, double price) {
        AmendOrder amendOrder = new AmendOrder();
        amendOrder.setOrderID(ordID);
        amendOrder.setPrice(price);
        amendOrder.setInstrument(nos.getInstrument());
        return amendOrder;
    }

    private CancelOrder createCancelMsg(String ordID, NewOrder nos) {
        CancelOrder cancel = new CancelOrder();
        cancel.setOrderID(ordID);
        cancel.setMsgHandler(nos.getMsgHandler());
        cancel.setInstrument(nos.getInstrument());
        return cancel;
    }


}
