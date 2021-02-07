package exchange.events;

import exchange.util.IMessageHandler;

public abstract class Event {

    private final MsgType     msgType;
    private IMessageHandler msgHandler;

    public Event( MsgType msgType ) {
         this.msgType = msgType;
    }

    public MsgType getMsgType() {
        return msgType;
     }

    public IMessageHandler getMsgHandler(){
        return msgHandler;
    }

    public void setMsgHandler( IMessageHandler msgHandler ){
        this.msgHandler = msgHandler;
    }
}
