package exchange.events;

import exchange.util.IMessageHandler;
import exchange.util.OrdType;
import exchange.util.Price;
import exchange.util.Side;

public class NewOrder extends Event {

    private String          custOrderID;
    private Price           limitPrice = Price.UNSET; // for market orders
    private long            totalQty;
    private OrdType         ordType;
    private Side            side;
    private String          instrument;
    private IMessageHandler msgHandler;
    private long            timeStamp;

    public NewOrder() {
        super(MsgType.NewOrder);
    }

    public long getQty() {
        return totalQty;
    }

    public void setQty( long qty ){
        totalQty = qty;
    }

    public void setCustOrdID(String custOrdID) {
        custOrderID = custOrdID;
    }

    public String getCustOrdID() {
        return custOrderID;
    }

    public void setPrice(double p) {
        limitPrice = new Price(p);
    }

    public Price getPrice() {
        return limitPrice;
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

}
