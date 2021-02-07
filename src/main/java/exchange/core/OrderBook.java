package exchange.core;

import exchange.events.AmendOrder;
import exchange.events.CancelOrder;
import exchange.util.Side;

public class OrderBook {

    private final String instr;
    private final BuySideBook buySide;
    private final SellSideBook sellSide;
    private IMatchingProcessor matchingProcessor;

    public OrderBook(String name, IMatchingProcessor matchingProcessor) {
        instr = name;
        buySide = new BuySideBook(matchingProcessor);
        sellSide = new SellSideBook(matchingProcessor);
        this.matchingProcessor = matchingProcessor;
    }

    public String getInstrument() {
        return instr;
    }

    public void handleNewOrder(Order order) {
        Side side = order.getSide();
        switch (side) {
            case BUY:
                sellSide.matchOrder(order);
                if( buySide.handleLeavesQty(order)) {
                    matchingProcessor.registerOrdIfAddedToBook(order);
                }
                break;
            case SELL:
                buySide.matchOrder(order);
                if( sellSide.handleLeavesQty(order)) {
                    matchingProcessor.registerOrdIfAddedToBook(order);
                }
                break;
        }
    }

    public void handleAmend(Order order) {
        Side side = order.getSide();
        switch (side) {
            case BUY:
                buySide.amend(order);
                Order topBuyOrder = buySide.getTopOfBook();
                if( order.getOrdID().equals( topBuyOrder.getOrdID())){
                    sellSide.matchOrder(order);
                    buySide.handleLeavesQtyWhenAmended(order);
                }
                break;
            case SELL:
                sellSide.amend(order);
                Order topSellOrder = sellSide.getTopOfBook();
                if( order.getOrdID().equals(topSellOrder.getOrdID())){
                    buySide.matchOrder(order);
                    sellSide.handleLeavesQtyWhenAmended(order);
                }
                break;
        }
    }

    public void handleCancel( Order order) {
        Side side = order.getSide();
        switch (side) {
            case BUY:
                buySide.cancel(order);
                break;
            case SELL:
                sellSide.cancel(order);
                break;
        }
    }

    public boolean hasOrder(Order order) {
        Side side = order.getSide();
        switch (side) {
            case BUY:
                 return buySide.contains(order);
            case SELL:
                return sellSide.contains(order);
        }
        return false;
    }
}
