package exchange.events;

import exchange.util.Price;

public class AmendOrder extends Event {

    private String instrument;
    private String orderID;
    private String custOrderID;
    private Price limitPrice = Price.UNSET;
    private long newQty = -1;

    public AmendOrder() {
        super(MsgType.AmendOrder);
    }

    public String getInstrument() {
        return instrument;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderID() {
        return orderID;
    }

    public long getQty() {
        return newQty;
    }

    public void setQty(long qty) { newQty = qty; }

    public void setPrice(double p) {
        limitPrice = new Price(p);
    }

    public Price getPrice() {
        return limitPrice;
    }

    public void setCustOrdID(String custOrdID) {
        custOrderID = custOrdID;
    }

    public String getCustOrdID() {
        return custOrderID;
    }


    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
}
