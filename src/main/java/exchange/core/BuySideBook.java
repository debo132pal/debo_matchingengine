package exchange.core;

import exchange.events.Amended;
import exchange.events.CancelOrder;
import exchange.events.Canceled;
import exchange.events.Trade;
import exchange.util.IndexMaxPQ;
import exchange.util.OrdType;
import exchange.util.Price;

public class BuySideBook extends Book {

    private IndexMaxPQ<Order> buyBook = new IndexMaxPQ<Order>(MAX_ORDERS);
    public int idx = 0;

    public BuySideBook(IMatchingProcessor matchingProcessor) {
        super(matchingProcessor);
    }

    @Override
    public boolean handleLeavesQty(Order order) {
        if (order.getLeavesQty() > 0 && order.getOrdType() == OrdType.LIMIT) {
            idx++;
            orderIdToIdx.put(order.getOrdID(), idx);
            buyBook.insert(idx, order);
            return true;
        } else {
            Canceled canceled = Canceled.createCanceled(order);
            matchingProcessor.handle(canceled);
        }
        return false;
    }

    @Override
    public Price getTradePrice(Order sellOrder) {
        Order buyOrder = getTopOfBook();
        if (buyOrder == null)
            return Price.UNSET;
        if (sellOrder.getOrdType() == OrdType.MKT)
            return buyOrder.getPrice();
        else if (buyOrder.getPrice().value() < sellOrder.getPrice().value()) {
            return Price.UNSET;
        }
        return buyOrder.getPrice();
    }

    @Override
    public boolean contains(Order order) {
        return buyBook.contains( orderIdToIdx.get(order.getOrdID()) );
    }

    @Override
    public Order getTopOfBook() {
        if (buyBook.size() > 0)
            return buyBook.maxKey();
        return null;
    }

    @Override
    protected void removeFromBook( ) {
        if (buyBook.size() > 0) {
            Order ord = buyBook.maxKey();
            buyBook.delMax();
            orderIdToIdx.remove(ord.getOrdID());
            matchingProcessor.removeOrder(ord);
        }

    }

    @Override
    public void amend(Order order) {
        int idx = orderIdToIdx.get(order.getOrdID());
        buyBook.changeKey( idx, order);
        Amended amended = Amended.createAmended( order );
        matchingProcessor.handle( amended );
    }

    @Override
    public void cancel(Order order) {
        int idx = orderIdToIdx.get(order.getOrdID());
        buyBook.delete(idx);
        orderIdToIdx.remove(order.getOrdID());
        Canceled canceled = Canceled.createCanceled( order );
        matchingProcessor.handle( canceled);
    }

}
