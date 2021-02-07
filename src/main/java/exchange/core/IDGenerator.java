package exchange.core;

import java.util.concurrent.atomic.AtomicLong;

public class IDGenerator {
    private AtomicLong  id = new AtomicLong();
    private final String prefix ;
    public IDGenerator( String p ){
        prefix = p;
    }

    public  String getID(){
        return prefix + id.getAndIncrement();
    }



}
