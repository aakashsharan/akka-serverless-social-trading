package com.lightbend.gsa.trade.domain;

import com.lightbend.gsa.Main;
import com.lightbend.gsa.trade.TradeApi;
import com.lightbend.gsa.trade.TradeServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.concurrent.TimeUnit.*;

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class TradeEntityIntegrationTest {
    private final String entityId = "entityId1";
    private final String tradeId = "trade-1209";
    String buyerId = "Eren";
    String sellerId = "Mikasa";

    TradeApi.ItemId apiItem1 = TradeApi.ItemId.newBuilder().setItemId("1").build();
    TradeApi.ItemId apiItem2 = TradeApi.ItemId.newBuilder().setItemId("2").build();
    TradeApi.ItemId apiItem3 = TradeApi.ItemId.newBuilder().setItemId("3").build();
    TradeApi.ItemId apiItem4 = TradeApi.ItemId.newBuilder().setItemId("4").build();

    TradeDomain.ItemId domainItem1 = TradeDomain.ItemId.newBuilder().setItemId("1").build();
    TradeDomain.ItemId domainItem2 = TradeDomain.ItemId.newBuilder().setItemId("2").build();
    TradeDomain.ItemId domainItem3 = TradeDomain.ItemId.newBuilder().setItemId("3").build();
    TradeDomain.ItemId domainItem4 = TradeDomain.ItemId.newBuilder().setItemId("4").build();

    List<TradeApi.ItemId> apiBuyerList = Arrays.asList(apiItem1, apiItem2);
    List<TradeApi.ItemId> apiSellerList = Arrays.asList(apiItem3, apiItem4);
    List<TradeDomain.ItemId> domainBuyerList = Arrays.asList(domainItem1, domainItem2);
    List<TradeDomain.ItemId> domainSellerList = Arrays.asList(domainItem3, domainItem4);
    
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
        TradeApi.CreateTradeItem command = TradeApi.CreateTradeItem.newBuilder().setTradeId(tradeId)
                .setBuyerUserId(buyerId)
                .setSellerUserId(sellerId)
                .addAllBuyerItemIds(apiBuyerList)
                .addAllSellerItemIds(apiSellerList)
                .build();

         client.createTrade(command).toCompletableFuture().get(10, SECONDS);
    }
    
    @Test
    public void acceptTradeOnNonExistingEntity() throws Exception {
        TradeApi.AcceptTradeItem command = TradeApi.AcceptTradeItem.newBuilder().setTradeId(tradeId).build();
        client.acceptTrade(command).toCompletableFuture().get(10, SECONDS);
    }
    
    @Test
    public void rejectTradeOnNonExistingEntity() throws Exception {
        TradeApi.RejectTradeItem command = TradeApi.RejectTradeItem.newBuilder().setTradeId(tradeId).build();
        client.rejectTrade(command).toCompletableFuture().get(10, SECONDS);
    }
    
    @Test
    public void getTradeOnNonExistingEntity() throws Exception {
        TradeApi.GetTradeItem get = TradeApi.GetTradeItem.newBuilder().setTradeId(tradeId).build();
        client.getTrade(get).toCompletableFuture().get(10, SECONDS);
    }
}