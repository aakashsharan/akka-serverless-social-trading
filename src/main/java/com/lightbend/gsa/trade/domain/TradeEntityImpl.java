package com.lightbend.gsa.trade.domain;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import com.lightbend.gsa.trade.TradeApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "event-sourced-trade")
public class TradeEntityImpl extends TradeEntityInterface {

    private static final Logger LOG = LoggerFactory.getLogger(TradeEntityImpl.class);
    @SuppressWarnings("unused")
    private final String entityId;
    private TradeApi.Trade trade;
    
    public TradeEntityImpl(@EntityId String entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public TradeDomain.TradeState snapshot() {
        LOG.info("Inside `snapshot` method for trade_id: {}", trade.getTradeId());
        return convert(trade);
    }
    
    @Override
    public void handleSnapshot(TradeDomain.TradeState snapshot) {
        LOG.info("Inside `snapshot handler` method for trade_id: {}", trade.getTradeId());
        this.trade = convert(snapshot);
    }
    
    @Override
    protected Empty createTrade(TradeApi.CreateTradeItem command, CommandContext ctx) {
        LOG.info("Inside `createTrade` command for trade_id: {}", command.getTradeId());
        TradeDomain.TradeOffered.Builder bd = TradeDomain.TradeOffered.newBuilder();
        List<TradeApi.ItemId> apiBuyerList = command.getBuyerItemIdsList();
        List<TradeApi.ItemId> apiSellerList = command.getSellerItemIdsList();

        List<TradeDomain.ItemId> domainBuyerList = convertApiItem(apiBuyerList);
        List<TradeDomain.ItemId> domainSellerList = convertApiItem(apiSellerList);
        bd.addAllBuyerItemIds(domainBuyerList);
        bd.addAllSellerItemIds(domainSellerList);
        bd.setTradeId(command.getTradeId());
        TradeDomain.TradeOffered tradeOffered = bd.build();

        // emit trade offered event
        ctx.emit(tradeOffered);
        return Empty.getDefaultInstance();
    }
    
    @Override
    protected Empty acceptTrade(TradeApi.AcceptTradeItem command, CommandContext ctx) {
        String tradeId = command.getTradeId();
        LOG.info("Inside `acceptTrade` command for trade_id: {}", tradeId);
        if(trade == null || tradeId.isEmpty() || !trade.getTradeId().equalsIgnoreCase(tradeId)) {
            throw ctx.fail("Cannot accept a trade " + tradeId + " that is invalid!");
        }

        TradeDomain.TradeAccepted.Builder bd = TradeDomain.TradeAccepted.newBuilder();
        bd.setTradeId(command.getTradeId());
        TradeDomain.TradeAccepted tradeAccepted = bd.build();

        // emit trade accepted event
        ctx.emit(tradeAccepted);
        return Empty.getDefaultInstance();
    }
    
    @Override
    protected Empty rejectTrade(TradeApi.RejectTradeItem command, CommandContext ctx) {
        String tradeId = command.getTradeId();
        LOG.info("Inside `rejectTrade` command for trade_id: {}", tradeId);
        if(trade == null || tradeId.isEmpty() || !trade.getTradeId().equalsIgnoreCase(tradeId)) {
            throw ctx.fail("Cannot reject a trade " + tradeId + " that is invalid!");
        }
        TradeDomain.TradeRejected.Builder bd = TradeDomain.TradeRejected.newBuilder();
        bd.setTradeId(command.getTradeId());
        TradeDomain.TradeRejected tradeRejected = bd.build();

        // emit trade rejected event
        ctx.emit(tradeRejected);
        return Empty.getDefaultInstance();
    }
    
    @Override
    protected TradeApi.Trade getTrade(TradeApi.GetTradeItem command, CommandContext ctx) {
        LOG.info("Inside `getTrade` command for trade_id: {}", command.getTradeId());
        TradeApi.Trade.Builder bd = TradeApi.Trade.newBuilder();

        List<TradeApi.ItemId> buyerList = trade.getBuyerItemIdsList();
        List<TradeApi.ItemId> sellerList = trade.getSellerItemIdsList();

        List<TradeApi.ItemId> newBuyerList = new LinkedList<>(buyerList);
        List<TradeApi.ItemId> newSellerList = new LinkedList<>(sellerList);
        bd.addAllBuyerItemIds(newBuyerList);
        bd.addAllSellerItemIds(newSellerList);

        // return the items to the client
        return bd.setTradeId(trade.getTradeId())
                .setBuyerUserId(trade.getBuyerUserId())
                .setSellerUserId(trade.getSellerUserId())
                .setStatusValue(trade.getStatusValue())
                .setTradeOfferedTimestamp(trade.getTradeOfferedTimestamp())
                .setStatus(trade.getStatus())
                .build();
    }
    
    @Override
    public void tradeOffered(TradeDomain.TradeOffered event) {
        LOG.info("Inside `tradeOffered` event for trade_id: {}", event.getTradeId());
        trade = TradeApi.Trade.newBuilder().build();

        List<TradeDomain.ItemId> domainBuyerList = event.getBuyerItemIdsList();
        List<TradeDomain.ItemId> domainSellerList = event.getSellerItemIdsList();
        List<TradeApi.ItemId> apiBuyerList = convertDomainItem(domainBuyerList);
        List<TradeApi.ItemId> apiSellerList = convertDomainItem(domainSellerList);

        // update the state of the trade
        trade = trade.toBuilder().setStatus(TradeApi.Trade.Status.CREATED)
                .addAllBuyerItemIds(apiBuyerList)
                .addAllSellerItemIds(apiSellerList)
                .setTradeId(event.getTradeId())
                .setBuyerUserId(event.getBuyerUserId())
                .setSellerUserId(event.getSellerUserId())
                .setTradeOfferedTimestamp(System.currentTimeMillis())
                .build();
    }
    
    @Override
    public void tradeAccepted(TradeDomain.TradeAccepted event) {
        LOG.info("Inside `tradeAccepted` event for trade_id: {}", event.getTradeId());
        // update the state of the trade with status accepted
        trade = trade.toBuilder().setStatus(TradeApi.Trade.Status.ACCEPTED).build();
    }
    
    @Override
    public void tradeRejected(TradeDomain.TradeRejected event) {
        LOG.info("Inside `tradeRejected` event for trade_id: {}", event.getTradeId());
        // update the state of the trade with status rejected
        trade = trade.toBuilder().setStatus(TradeApi.Trade.Status.REJECTED).build();
    }

    // convert tradeapi item to tradedomain item
    private TradeDomain.TradeState convert(TradeApi.Trade trade) {
        TradeDomain.TradeState.Builder bd = createBuilder();

        return bd.setTradeId(trade.getTradeId())
                .setBuyerUserId(trade.getBuyerUserId())
                .setSellerUserId(trade.getSellerUserId())
                .setStatusValue(trade.getStatusValue())
                .build();
    }

    // create a builder for tradedomain trade
    private TradeDomain.TradeState.Builder createBuilder() {
        TradeDomain.TradeState.Builder bd = TradeDomain.TradeState.newBuilder();
        List<TradeApi.ItemId> buyerList = trade.getBuyerItemIdsList();
        List<TradeApi.ItemId> sellerList = trade.getSellerItemIdsList();

        List<TradeDomain.ItemId> newBuyerList = convertApiItem(buyerList);
        List<TradeDomain.ItemId> newSellerList = convertApiItem(sellerList);
        bd.addAllBuyerItemIds(newBuyerList);
        bd.addAllSellerItemIds(newSellerList);

        return bd;
    }

    // convert tradedomain item to tradeapi item
    private TradeApi.Trade convert(TradeDomain.TradeState trade) {
        TradeApi.Trade.Builder bd = createBuilder(trade);

        return bd.setTradeId(trade.getTradeId())
                .setBuyerUserId(trade.getBuyerUserId())
                .setSellerUserId(trade.getSellerUserId())
                .setStatusValue(trade.getStatusValue())
                .build();
    }

    // create a builder for tradeapi trade
    private TradeApi.Trade.Builder createBuilder(TradeDomain.TradeState trade) {
        TradeApi.Trade.Builder bd = TradeApi.Trade.newBuilder();
        List<TradeDomain.ItemId> buyerList = trade.getBuyerItemIdsList();
        List<TradeDomain.ItemId> sellerList = trade.getSellerItemIdsList();

        List<TradeApi.ItemId> newBuyerList = convertDomainItem(buyerList);
        List<TradeApi.ItemId> newSellerList = convertDomainItem(sellerList);
        bd.addAllBuyerItemIds(newBuyerList);
        bd.addAllSellerItemIds(newSellerList);

        return bd;
    }

    // convert list of tradeapi itemid to list of tradedomain itemid
    private List<TradeDomain.ItemId> convertApiItem(List<TradeApi.ItemId> list) {
        List<TradeDomain.ItemId> domainList = new LinkedList<>();

        for(TradeApi.ItemId itemId: list) {
            String id = itemId.getItemId();

            TradeDomain.ItemId.Builder bd = TradeDomain.ItemId.newBuilder().setItemId(id);
            domainList.add(bd.build());
        }
        return domainList;
    }

    // convert list of tradedomain itemid to list of tradeapi itemid
    private List<TradeApi.ItemId> convertDomainItem(List<TradeDomain.ItemId> list) {
        List<TradeApi.ItemId> domainList = new LinkedList<>();

        for(TradeDomain.ItemId itemId: list) {
            String id = itemId.getItemId();

            TradeApi.ItemId.Builder bd = TradeApi.ItemId.newBuilder().setItemId(id);
            domainList.add(bd.build());
        }
        return domainList;
    }
}