package exchange.events;

import exchange.util.OrdStatus;

public class Reject extends Event {

    private OrdStatus ordStatus;
    private String    custOrderID;

    public Reject() {
        super(MsgType.Reject);
    }

    public void setCustOrderID(String custOrderID) {
        this.custOrderID = custOrderID;
    }

    public String getCustOrderID() {
        return custOrderID;
    }

    public void setOrdStatus(OrdStatus status) {
        this.ordStatus = status;
    }

    public OrdStatus getOrdStatus() {
        return ordStatus;
    }

    public static Reject create(NewOrder msg) {
        Reject reject = new Reject();
        reject.setMsgHandler(msg.getMsgHandler());
        reject.setCustOrderID(msg.getCustOrdID());
        reject.setOrdStatus(OrdStatus.Rejected);
        return reject;
    }

    public static Reject create(AmendOrder msg) {
        Reject reject = new Reject();
        reject.setMsgHandler(msg.getMsgHandler());
        reject.setOrdStatus(OrdStatus.Rejected);
        return reject;
    }

    public static Reject create(CancelOrder msg) {
        Reject reject = new Reject();
        reject.setMsgHandler(msg.getMsgHandler());
        reject.setOrdStatus(OrdStatus.Rejected);
        return reject;
    }
}
