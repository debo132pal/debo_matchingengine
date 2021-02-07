package exchange.core;

import exchange.util.IMessageHandler;

import java.util.HashMap;

public class Router {

    private HashMap<String, IMessageHandler> router = new HashMap<>();

    public void setUpMessageHandler( HashMap<String, IMessageHandler>  router ) {
        this.router = router;
    }

    public IMessageHandler getMessageHandler( String instr ){
         if( router.containsKey( instr ) )
         {
             return router.get( instr );
         }
          return null;
    }


}
