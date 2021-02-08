package exchange.core;

import exchange.events.NewOrder;
import exchange.util.*;

public class Order implements Comparable<Order> {

    private String orderID;
    private String custOrderID;
    private String instrument;
    private Price price;
    private long totalQty;
    private long leavesQty;
    private OrdType ordType;
    private Side side;

    private IMessageHandler msgHandler;
    private long timeStamp;

    public Order() {
    }

    public String getOrdID() {
        return orderID;
    }

    public void setCustOrdID(String custOrdID) {
        custOrderID = custOrdID;
    }

    public String getCustOrdID() {
        return custOrderID;
    }

    public void setPrice(Price p) {
        price = p;
    }

    public Price getPrice() {
        return price;
    }

    public void setTotalQty(long qty) {
        totalQty = qty;
    }

    public long getTotalQty() {
        return totalQty;
    }

    public void setLeavesQty(long leavesQty) {
        this.leavesQty = leavesQty;
    }

    public long getLeavesQty() {
        return leavesQty;
    }

    public void setOrdType(OrdType ordType) {
        this.ordType = ordType;
    }

    public OrdType getOrdType() {
        return ordType;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public Side getSide() {
        return side;
    }

    public void setMsgHandler(IMessageHandler msgHandler) {
        this.msgHandler = msgHandler;
    }

    public IMessageHandler getMsgHandler() {
        return msgHandler;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getInstrument() {
        return this.instrument;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setOrdID(String id) {
        orderID = id;
    }

    @Override
    public int compareTo(Order o) {
        if (getPrice().value() < o.getPrice().value()) {
            return -1;
        } else if (getPrice().value() > o.getPrice().value()) {
            return 1;
        } else if (getPrice().value() == o.getPrice().value()) {
            //compare by Time
            if (getTimeStamp() < o.getTimeStamp()) {
                return -1;
            } else if (getTimeStamp() > o.getTimeStamp()) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;

    }

    public static Order createOrder(NewOrder noMsg) {
        Order ord = new Order();
        ord.setTimeStamp(noMsg.getTimeStamp());
        ord.setCustOrdID(new String(noMsg.getCustOrdID()));
        ord.setInstrument(new String(noMsg.getInstrument()));
        ord.setLeavesQty(noMsg.getQty());
        ord.setTotalQty(noMsg.getQty());
        ord.setOrdType(noMsg.getOrdType());
        if (noMsg.getPrice() != Price.UNSET) {
            ord.setPrice(noMsg.getPrice());
        }
        ord.setSide(noMsg.getSide());
        ord.setMsgHandler(noMsg.getMsgHandler());
        return ord;
    }
}
