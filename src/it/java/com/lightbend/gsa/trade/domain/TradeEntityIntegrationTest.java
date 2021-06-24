package com.lightbend.gsa.trade.domain;

import com.lightbend.gsa.Main;
import com.lightbend.gsa.trade.TradeServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class TradeEntityIntegrationTest {
    
    /**
     * The test kit starts both the service container and the Akka Serverless proxy.
     */
    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(Main.SERVICE);
    
    /**
     * Use the generated gRPC client to call the service through the Akka Serverless proxy.
     */
    private final TradeServiceClient client;
    
    public TradeEntityIntegrationTest() {
        client = TradeServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }
    
    @Test
    public void createTradeOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.createTrade(TradeApi.CreateTradeItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void acceptTradeOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.acceptTrade(TradeApi.TradeItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void rejectTradeOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.rejectTrade(TradeApi.TradeItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void getTradeOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.getTrade(TradeApi.TradeItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
}