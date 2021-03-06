package exchange.util;

public class Price {
    public static Price UNSET = new Price(Double.MIN_VALUE);
    private int factor = 1000000;
    private final long internalVal;
    private final double orginalVal;

    public Price(double price) {
        orginalVal = price;
        internalVal = (long) (price * factor);
    }

    public Price(Price price) {
        orginalVal = price.getPrice();
        internalVal = price.value();
    }

    public long value() {
        return internalVal;
    }

    public double getPrice() {
        return orginalVal;
    }
}
