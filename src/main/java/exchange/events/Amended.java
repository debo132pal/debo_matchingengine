package exchange.events;

import exchange.core.Order;
import exchange.util.OrdStatus;

public class Amended extends Event{
    private String          custOrderID;
    private long            leavesQty;
    private String          ordID;
    private OrdStatus       ordStatus;

    public Amended() {
        super( MsgType.Trade );
    }

    public static Amended createAmended(Order order) {
        Amended trade =  new Amended();
        trade.setMsgHandler( order.getMsgHandler());
        trade.setLeavesQty( order.getLeavesQty() );
        trade.setCustOrderID( order.getCustOrdID());
        trade.setOrdID( order.getOrdID());
        OrdStatus status = OrdStatus.Amended;
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

    public void setLeavesQty( long qty ){
        this.leavesQty = qty;
    }

    public long getLeavesQty(){
        return this.leavesQty;
    }

    public String getOrdID(){
        return ordID;
    }

    public void setOrdID( String ordID  ){
        this.ordID = ordID;
    }

}
