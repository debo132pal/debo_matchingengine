package exchange.events;

import exchange.core.Order;
import exchange.util.OrdStatus;
import exchange.util.Price;

public class Canceled extends Event{
    private String          custOrderID;
    private long            canceledQty;
    private String          ordID;
    private OrdStatus       ordStatus;

    public Canceled() {
        super( MsgType.Trade );
    }

    public static Canceled createCanceled(Order order) {
        Canceled trade =  new Canceled();
        trade.setMsgHandler( order.getMsgHandler());
        trade.setCanceledQty( order.getLeavesQty() );
        trade.setCustOrderID( order.getCustOrdID());
        trade.setOrdID( order.getOrdID());
        OrdStatus status = OrdStatus.Canceled;
        trade.setOrdStatus( status );
        return trade;
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

    public void setCanceledQty( long qty ){
        this.canceledQty = qty;
    }

    public long getCanceledQty(){
        return this.canceledQty;
    }

    public String getOrdID(){
        return ordID;
    }

    public void setOrdID( String ordID  ){
        this.ordID = ordID;
    }

}
