package exchange.core;

import exchange.events.*;
import exchange.util.Price;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingProcessorImpl extends Processor implements IMatchingProcessor {

    private Map<String, OrderBook> orderBookMap = new HashMap<>();
    private HashMap<String,Order>  _ordCtx = new HashMap<>();

    private IDGenerator idGenerator ;

    public MatchingProcessorImpl(int idx) {
        super(idx);
    }
    public void setIdGenerator( IDGenerator gen ){
        idGenerator = gen;
    }

    public void setUpOrderBookForInstruments(List<String> instruments) {
        for( String instr : instruments ) {
            if( !orderBookMap.containsKey(instr)) {
                orderBookMap.put(instr, new OrderBook(instr , this) );
            }
        }
    }

    @Override
    public void handle(NewOrder msg) {
        // validate the msg else reject
        //If validation success, create Order in our system
        Order newOrd = Order.createOrder( msg );
        newOrd.setOrdID( idGenerator.getID() );
        OrderBook book = orderBookMap.get( newOrd.getInstrument());
        if( book != null ){
            book.handleNewOrder( newOrd );
        } else {
            Reject rejectMsg = Reject.create( msg );
            handle( rejectMsg );
        }
    }

    @Override
    public void registerOrdIfAddedToBook(Order newOrd) {
        _ordCtx.put( newOrd.getOrdID() , newOrd );
    }

    @Override
    public void handle(AmendOrder msg) {
        Order order = _ordCtx.get(msg.getOrderID());
        boolean doReject = false ;
        if( order != null ){
           if( msg.getQty() > 0 ) {
               long totalQty = order.getTotalQty();
               long leavesQty = order.getLeavesQty();
               long newQty = msg.getQty();
               if( newQty < totalQty && leavesQty <= newQty ){
                   order.setTotalQty( newQty );
               } else {
                   doReject = true;
               }
           } else if( msg.getPrice() != Price.UNSET ){
               order.setPrice( msg.getPrice() );
           }
        } else {
            doReject = true;
        }
        if( !doReject ) {
            OrderBook book = orderBookMap.get(msg.getInstrument());
            book.handleAmend(order);
        } else {
            Reject rejectMsg = Reject.create( msg );
            handle( rejectMsg );
        }
    }

    @Override
    public void handle(CancelOrder msg) {
        Order order = _ordCtx.get(msg.getOrderID());
        boolean doReject = false ;
        if( order == null ) {
            doReject = true;
        }
        if( !doReject ) {
            OrderBook book = orderBookMap.get(msg.getInstrument());
            book.handleCancel(order);
            removeOrder( order );
        } else {
            Reject rejectMsg = Reject.create( msg );
            handle( rejectMsg );
        }
    }

    @Override
    public void removeOrder(Order order) {
        _ordCtx.remove(order.getOrdID());
    }

    @Override
    public void handle(Trade msg) {
        msg.getMsgHandler().sumbit(msg);
    }

    @Override
    public void handle(Reject msg) {
        msg.getMsgHandler().sumbit(msg);
    }

    @Override
    public void handle(Canceled msg) {
        msg.getMsgHandler().sumbit(msg);
    }

    @Override
    public void handle(Amended msg) {
        msg.getMsgHandler().sumbit(msg);
    }


}
