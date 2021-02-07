package exchange.core;

import exchange.events.*;

public interface IMatchingProcessor {

    void handle(NewOrder msg);

    void handle(AmendOrder msg);

    void handle(CancelOrder msg);

    void handle(Trade msg);

    void handle(Reject msg);

    void handle(Canceled msg);

    void handle(Amended msg);

    void handle( Event msg );

    void stop();

    public void registerOrdIfAddedToBook(Order ord);

    public void removeOrder(Order ord);
}
