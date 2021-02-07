package exchange.core;

import exchange.events.Trade;
import exchange.util.Price;

import java.util.HashMap;
import java.util.Map;

public abstract class Book {

    public static int MAX_ORDERS = 1000000;
    protected IMatchingProcessor matchingProcessor;
    protected Map<String, Integer> orderIdToIdx = new HashMap<>();

    public Book(IMatchingProcessor matchingProcessor) {
        this.matchingProcessor = matchingProcessor;
    }

    public abstract boolean handleLeavesQty(Order order);

    public abstract Price getTradePrice(Order order);

    public abstract boolean contains(Order order);

    public abstract Order getTopOfBook();

    protected abstract  void removeFromBook();

    public abstract void amend(Order order);

    public abstract  void cancel(Order order);

    public void handleLeavesQtyWhenAmended( Order order ){
        if( order.getLeavesQty() <= 0 ){
            removeFromBook();
        }
    }

    public void matchOrder(Order ord) {
        while (ord.getLeavesQty() > 0) {
            Order oppOrder = getTopOfBook();
            Price price    = getTradePrice(ord);
            if (price != Price.UNSET) {
                long currQty = ord.getLeavesQty();
                long otherSideQty = oppOrder.getLeavesQty();
                long executedQty = 0;
                if (currQty == otherSideQty) {
                    ord.setLeavesQty(0);
                    oppOrder.setLeavesQty(0);
                    removeFromBook();
                    executedQty = currQty;
                } else if (currQty > otherSideQty) {
                    oppOrder.setLeavesQty(0);
                    removeFromBook();
                    ord.setLeavesQty(currQty - otherSideQty);
                    executedQty = otherSideQty;
                } else if (currQty < otherSideQty) {
                    ord.setLeavesQty(0);
                    oppOrder.setLeavesQty(otherSideQty - currQty);
                    executedQty = currQty;
                }
                matchingProcessor.handle(Trade.createTrade(ord, price, executedQty));
                matchingProcessor.handle(Trade.createTrade(oppOrder, price, executedQty));
            } else {
                break;
            }
        }
    }

}
