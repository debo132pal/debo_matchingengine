package exchange.test.util;

import exchange.core.MatchingProcessorImpl;
import exchange.core.Router;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMatchingEngine {

    private MatchingProcessorImpl matchingProcessor = new MatchingProcessorImpl( 101);
    private ExecutorService      testThread;
    public  TestMatchingEngine( ){
        testThread = Executors.newSingleThreadExecutor();
    }

    public MatchingProcessorImpl getMatchingProcessor(){
        return matchingProcessor;
    }

    public void start(){
        testThread.execute( matchingProcessor );
    }

    public void stop(){
        matchingProcessor.stop();
        testThread.shutdown();

    }
}
