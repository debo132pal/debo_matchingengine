package exchange.core;

import exchange.events.Amended;
import exchange.events.Canceled;
import exchange.util.IndexMinPQ;
import exchange.util.OrdType;
import exchange.util.Price;

public class SellSideBook extends Book {

    private IndexMinPQ<Order> sellBook = new IndexMinPQ<Order>(MAX_ORDERS);
    private int idx = 0;

    public SellSideBook(IMatchingProcessor matchingProcessor) {
        super(matchingProcessor);
    }

    @Override
    public boolean handleLeavesQty(Order order) {
        if (order.getLeavesQty() > 0 && order.getOrdType() == OrdType.LIMIT) {
            idx++;
            orderIdToIdx.put(order.getOrdID(), idx);
            sellBook.insert(idx, order);
            return true;
        } else {
            Canceled canceled = Canceled.createCanceled(order);
            matchingProcessor.handle(canceled);
        }
        return false;
    }

    @Override
    public Price getTradePrice(Order buyOrder) {
        Order sellOrder = getTopOfBook();
        if (sellOrder == null)
            return Price.UNSET;
        if (buyOrder.getOrdType() == OrdType.MKT)
            return sellOrder.getPrice();
        else if (buyOrder.getPrice().value() < sellOrder.getPrice().value()) {
            return Price.UNSET;
        }
        return sellOrder.getPrice();
    }

    @Override
    public boolean contains(Order order) {
        return sellBook.contains(orderIdToIdx.get(order.getOrdID()));
    }

    @Override
    public Order getTopOfBook() {
        if (sellBook.size() > 0) {
            return sellBook.minKey();
        }
        return null;
    }

    @Override
    protected void removeFromBook() {
        if (sellBook.size() > 0) {
            Order ord = sellBook.minKey();
            sellBook.delMin();
            orderIdToIdx.remove(ord.getOrdID());
            matchingProcessor.removeOrder(ord);
        }
    }

    @Override
    public void amend(Order order) {
        int idx = orderIdToIdx.get(order.getOrdID());
        sellBook.changeKey(idx, order);
        Amended amended = Amended.createAmended(order);
        matchingProcessor.handle(amended);
    }

    @Override
    public void cancel(Order order) {
        int idx = orderIdToIdx.get(order.getOrdID());
        sellBook.delete(idx);
        orderIdToIdx.remove(order.getOrdID());
        Canceled canceled = Canceled.createCanceled(order);
        matchingProcessor.handle(canceled);
    }

}
