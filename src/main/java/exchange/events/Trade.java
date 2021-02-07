package exchange.events;

import com.sun.org.apache.xpath.internal.operations.Or;
import exchange.core.Order;
import exchange.util.OrdStatus;
import exchange.util.Price;

public class Trade extends Event{
    private String          custOrderID;
    private long            lastTradedQty;
    private Price           lastTradedPrice;
    private String          ordID;
    private OrdStatus       ordStatus;
    public Trade() {
        super( MsgType.Trade );
    }

    public void setCustOrderID( String custOrderID ){
        this.custOrderID = custOrderID ;
    }

    public String getCustOrderID() {
        return custOrderID;
    }

    public void setOrdStatus( OrdStatus status ){
        this.ordStatus = status;
    }
    public OrdStatus getOrdStatus(){
        return ordStatus;
    }

    public void setLastTradedQty( long qty ){
        this.lastTradedQty = qty;
    }

    public long getLastTradedQty(){
        return this.lastTradedQty;
    }

    public void setLastTradedPrice( Price price ){
        this.lastTradedPrice = price;
    }

    public Price getLastTradedPrice() {
        return lastTradedPrice;
    }

    public String getOrdID(){
        return ordID;
    }

    public void setOrdID( String ordID  ){
        this.ordID = ordID;
    }

    public static Trade createTrade(Order ord, Price price, long executedQty) {
        Trade trade =  new Trade();
        trade.setMsgHandler( ord.getMsgHandler());
        trade.setLastTradedPrice( price );
        trade.setLastTradedQty( executedQty );
        trade.setCustOrderID( ord.getCustOrdID());
        trade.setOrdID( ord.getOrdID());
        OrdStatus status = null;
        if( ord.getLeavesQty() == ord.getTotalQty() ){
            status = OrdStatus.New;
        } else if ( ord.getLeavesQty() > 0 ) {
            status = OrdStatus.Partial;
        } else if ( ord.getLeavesQty() == 0 ) {
            status = OrdStatus.Fill;
        }
        trade.setOrdStatus( status );
        return trade;
    }
}
