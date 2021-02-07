package exchange.events;

import exchange.util.Price;

public class CancelOrder extends Event {

    private String instrument;
    private String orderID;
    private String custOrderID;


    public CancelOrder() {
        super(MsgType.CancelOrder);
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
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

    public void setCustOrdID(String custOrdID) {
        custOrderID = custOrdID;
    }

    public String getCustOrdID() {
        return custOrderID;
    }
}
