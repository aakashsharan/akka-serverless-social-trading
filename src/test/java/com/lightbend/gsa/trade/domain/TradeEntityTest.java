package com.lightbend.gsa.trade.domain;

import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.lightbend.gsa.trade.TradeApi;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TradeEntityTest {
    private final String entityId = "entityId1";
    private TradeEntityImpl entity;
    private final String tradeId = "trade-1209";
    private static final int CREATED = 0;
    private static final int ACCEPTED = 1;
    private static final int REJECTED = 2;
    private final CommandContext context = Mockito.mock(CommandContext.class);

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
    
    @Test
    public void createTradeTest() {
        entity = new TradeEntityImpl(entityId);

        TradeApi.CreateTradeItem command = TradeApi.CreateTradeItem.newBuilder().setTradeId(tradeId)
                .setBuyerUserId(buyerId)
                .setSellerUserId(sellerId)
                .addAllBuyerItemIds(apiBuyerList)
                .addAllSellerItemIds(apiSellerList)
                .build();

        entity.createTrade(command, context);

        TradeDomain.TradeOffered event = TradeDomain.TradeOffered.newBuilder().setTradeId(tradeId)
                .setBuyerUserId(command.getBuyerUserId())
                .setSellerUserId(command.getSellerUserId())
                .addAllBuyerItemIds(domainBuyerList)
                .addAllSellerItemIds(domainSellerList)
                .build();

        Mockito.verify(context).emit(event);
        entity.tradeOffered(event);

        TradeApi.GetTradeItem get = TradeApi.GetTradeItem.newBuilder().setTradeId(tradeId).build();

        TradeApi.Trade trade = entity.getTrade(get, context);

        assertNotNull(trade);
        assertEquals(tradeId, trade.getTradeId());
        assertEquals(buyerId, trade.getBuyerUserId());
        assertEquals(sellerId, trade.getSellerUserId());
        assertEquals(apiBuyerList, trade.getBuyerItemIdsList());
        assertEquals(apiSellerList, trade.getSellerItemIdsList());
        assertEquals(CREATED, trade.getStatus().getNumber());
    }
    
    @Test
    public void acceptTradeTest() {
        entity = new TradeEntityImpl(entityId);

        TradeDomain.TradeOffered tradeOffered = TradeDomain.TradeOffered.newBuilder()
                .setTradeId(tradeId)
                .setBuyerUserId(buyerId)
                .setSellerUserId(sellerId)
                .addAllBuyerItemIds(domainBuyerList)
                .addAllSellerItemIds(domainSellerList)
                .build();

        entity.tradeOffered(tradeOffered);

        TradeApi.AcceptTradeItem command = TradeApi.AcceptTradeItem.newBuilder().setTradeId(tradeId).build();
        entity.acceptTrade(command, context);

        TradeDomain.TradeAccepted event = TradeDomain.TradeAccepted.newBuilder().setTradeId(tradeId).build();
        Mockito.verify(context).emit(event);
        entity.tradeAccepted(event);

        TradeApi.GetTradeItem get = TradeApi.GetTradeItem.newBuilder().setTradeId(tradeId).build();
        TradeApi.Trade trade = entity.getTrade(get, context);

        assertNotNull(trade);
        assertEquals(tradeId, trade.getTradeId());
        assertEquals(buyerId, trade.getBuyerUserId());
        assertEquals(sellerId, trade.getSellerUserId());
        assertEquals(apiBuyerList, trade.getBuyerItemIdsList());
        assertEquals(apiSellerList, trade.getSellerItemIdsList());
        assertEquals(ACCEPTED, trade.getStatus().getNumber());
    }
    
    @Test
    public void rejectTradeTest() {
        entity = new TradeEntityImpl(entityId);

        TradeDomain.TradeOffered tradeOffered = TradeDomain.TradeOffered.newBuilder()
                .setTradeId(tradeId)
                .setBuyerUserId(buyerId)
                .setSellerUserId(sellerId)
                .addAllBuyerItemIds(domainBuyerList)
                .addAllSellerItemIds(domainSellerList)
                .build();

        entity.tradeOffered(tradeOffered);

        TradeApi.RejectTradeItem command = TradeApi.RejectTradeItem.newBuilder().setTradeId(tradeId).build();
        entity.rejectTrade(command, context);

        TradeDomain.TradeRejected event = TradeDomain.TradeRejected.newBuilder().setTradeId(tradeId).build();
        Mockito.verify(context).emit(event);
        entity.tradeRejected(event);

        TradeApi.GetTradeItem get = TradeApi.GetTradeItem.newBuilder().setTradeId(tradeId).build();
        TradeApi.Trade trade = entity.getTrade(get, context);

        assertNotNull(trade);
        assertEquals(tradeId, trade.getTradeId());
        assertEquals(buyerId, trade.getBuyerUserId());
        assertEquals(sellerId, trade.getSellerUserId());
        assertEquals(apiBuyerList, trade.getBuyerItemIdsList());
        assertEquals(apiSellerList, trade.getSellerItemIdsList());
        assertEquals(REJECTED, trade.getStatus().getNumber());
    }
    
    @Test
    public void getTradeTest() {
        entity = new TradeEntityImpl(entityId);

        TradeApi.CreateTradeItem command = TradeApi.CreateTradeItem.newBuilder().setTradeId(tradeId)
                .setBuyerUserId(buyerId)
                .setSellerUserId(sellerId)
                .addAllBuyerItemIds(apiBuyerList)
                .addAllSellerItemIds(apiSellerList)
                .build();

        entity.createTrade(command, context);

        TradeDomain.TradeOffered event = TradeDomain.TradeOffered.newBuilder().setTradeId(tradeId)
                .setBuyerUserId(command.getBuyerUserId())
                .setSellerUserId(command.getSellerUserId())
                .addAllBuyerItemIds(domainBuyerList)
                .addAllSellerItemIds(domainSellerList)
                .build();

        Mockito.verify(context).emit(event);
        entity.tradeOffered(event);

        TradeApi.GetTradeItem get = TradeApi.GetTradeItem.newBuilder().setTradeId(tradeId).build();
        TradeApi.Trade trade = entity.getTrade(get, context);

        assertNotNull(trade);
        assertEquals(tradeId, trade.getTradeId());
        assertEquals(buyerId, trade.getBuyerUserId());
        assertEquals(sellerId, trade.getSellerUserId());
        assertEquals(apiBuyerList, trade.getBuyerItemIdsList());
        assertEquals(apiSellerList, trade.getSellerItemIdsList());
        assertEquals(CREATED, trade.getStatus().getNumber());
    }
}