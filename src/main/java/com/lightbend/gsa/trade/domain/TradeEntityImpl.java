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
    private TradeDomain.TradeState state;
    
    public TradeEntityImpl(@EntityId String entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public TradeDomain.TradeState snapshot() {
        // TODO: produce state snapshot here
        //return TradeDomain.TradeState.newBuilder().build();
        state = convert();
        return state;
    }
    
    @Override
    public void handleSnapshot(TradeDomain.TradeState snapshot) {
        // TODO: restore state from snapshot here
        this.trade = convert(state);
    }
    
    @Override
    protected Empty createTrade(TradeApi.CreateTradeItem command, CommandContext ctx) {
        // throw ctx.fail("The command handler for `CreateTrade` is not implemented, yet");

        TradeDomain.TradeOffered.Builder bd = TradeDomain.TradeOffered.newBuilder();

        List<TradeApi.ItemId> buyerList = command.getBuyerItemIdsList();
        List<TradeApi.ItemId> sellerList = command.getSellerItemIdsList();

        LOG.info("buyer list length: {}", buyerList.size());
        LOG.info("seller list length: {}", sellerList.size());

        List<TradeDomain.ItemId> newBuyerList = convertItem(buyerList);
        List<TradeDomain.ItemId> newSellerList = convertItem(sellerList);

        bd.addAllBuyerItemIds(newBuyerList);
        bd.addAllSellerItemIds(newSellerList);

        TradeDomain.TradeOffered tradeOffered = bd.setTradeId(command.getTradeId())
                .setBuyerUserId(command.getBuyerUserId())
                .setSellerUserId(command.getSellerUserId())
                .build();

        ctx.emit(tradeOffered);
        return Empty.getDefaultInstance();
    }

    private List<TradeDomain.ItemId> convertItem(List<TradeApi.ItemId> list) {
        List<TradeDomain.ItemId> domainList = new LinkedList<>();

        for(TradeApi.ItemId itemId: list) {
            String id = itemId.getItemId();

            TradeDomain.ItemId.Builder bd = TradeDomain.ItemId.newBuilder().setItemId(id);
            domainList.add(bd.build());
        }
        return domainList;
    }
    
    @Override
    protected Empty acceptTrade(TradeApi.AcceptTradeItem command, CommandContext ctx) {
        // throw ctx.fail("The command handler for `AcceptTrade` is not implemented, yet");
        String tradeId = command.getTradeId();
        LOG.info("Inside createTrade, trade id is: {}", tradeId);
        if(trade == null || tradeId.isEmpty() || !trade.getTradeId().equalsIgnoreCase(tradeId)) {
            throw ctx.fail("Cannot accept a trade " + tradeId + " that is invalid!");
        }

        TradeDomain.TradeAccepted.Builder bd = TradeDomain.TradeAccepted.newBuilder();
        bd.setTradeId(command.getTradeId());

        ctx.emit(bd.build());
        return Empty.getDefaultInstance();
    }
    
    @Override
    protected Empty rejectTrade(TradeApi.RejectTradeItem command, CommandContext ctx) {
        // throw ctx.fail("The command handler for `RejectTrade` is not implemented, yet");
        String tradeId = command.getTradeId();
        if(trade == null || tradeId.isEmpty() || !trade.getTradeId().equalsIgnoreCase(tradeId)) {
            throw ctx.fail("Cannot reject a trade " + tradeId + " that is invalid!");
        }
        TradeDomain.TradeRejected.Builder bd = TradeDomain.TradeRejected.newBuilder();
        bd.setTradeId(command.getTradeId());

        ctx.emit(bd.build());
        return Empty.getDefaultInstance();
    }
    
    @Override
    protected TradeApi.Trade getTrade(TradeApi.GetTradeItem command, CommandContext ctx) {
        //throw ctx.fail("The command handler for `GetTrade` is not implemented, yet");
        TradeApi.Trade.Builder bd = TradeApi.Trade.newBuilder();

        List<TradeApi.ItemId> buyerList = trade.getBuyerItemIdsList();
        List<TradeApi.ItemId> sellerList = trade.getSellerItemIdsList();

        List<TradeApi.ItemId> newBuyerList = new LinkedList<>(buyerList);
        List<TradeApi.ItemId> newSellerList = new LinkedList<>(sellerList);
        bd.addAllBuyerItemIds(newBuyerList);
        bd.addAllBuyerItemIds(newSellerList);

        return bd.setTradeId(trade.getTradeId())
                .setBuyerUserId(trade.getBuyerUserId())
                .setSellerUserId(trade.getSellerUserId())
                .setStatusValue(trade.getStatusValue())
                .build();
    }
    
    @Override
    public void tradeOffered(TradeDomain.TradeOffered event) {
        //throw new RuntimeException("The event handler for `TradeOffered` is not implemented, yet");
        trade.toBuilder().setStatus(TradeApi.Trade.Status.CREATED);
    }
    
    @Override
    public void tradeAccepted(TradeDomain.TradeAccepted event) {
        //throw new RuntimeException("The event handler for `TradeAccepted` is not implemented, yet");
        trade.toBuilder().setStatus(TradeApi.Trade.Status.ACCEPTED);
    }
    
    @Override
    public void tradeRejected(TradeDomain.TradeRejected event) {
        //throw new RuntimeException("The event handler for `TradeRejected` is not implemented, yet");
        trade.toBuilder().setStatus(TradeApi.Trade.Status.REJECTED);
    }

    // convert tradeapi item to tradedomain item
    private TradeDomain.TradeState convert() {
        TradeDomain.TradeState.Builder bd = createBuilder();

        return bd.setTradeId(trade.getTradeId())
                .setBuyerUserId(trade.getBuyerUserId())
                .setSellerUserId(trade.getSellerUserId())
                .setStatusValue(trade.getStatusValue())
                .build();
    }

    // create a builder for tradedomain trade
    private TradeDomain.TradeState.Builder createBuilder() {
        List<TradeApi.ItemId> buyerList = trade.getBuyerItemIdsList();
        List<TradeApi.ItemId> sellerList = trade.getSellerItemIdsList();
        int i = 0, j = 0;
        TradeDomain.TradeState.Builder bd = TradeDomain.TradeState.newBuilder();

        for(TradeApi.ItemId id: buyerList) {
            TradeDomain.ItemId.Builder item = TradeDomain.ItemId.newBuilder();
            bd.setBuyerItemIds(i, item.setItemId(id.getItemId()));
            i++;
        }

        for(TradeApi.ItemId id: sellerList) {
            TradeDomain.ItemId.Builder item = TradeDomain.ItemId.newBuilder();
            bd.setSellerItemIds(j, item.setItemId(id.getItemId()));
            j++;
        }

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
        List<TradeDomain.ItemId> buyersList = trade.getBuyerItemIdsList();
        List<TradeDomain.ItemId> sellersList = trade.getSellerItemIdsList();
        int i = 0, j = 0;
        TradeApi.Trade.Builder bd = TradeApi.Trade.newBuilder();

        for(TradeDomain.ItemId id: buyersList) {
            TradeApi.ItemId.Builder item = TradeApi.ItemId.newBuilder();
            bd.setBuyerItemIds(i, item.setItemId(id.getItemId()));
            i++;
        }

        for(TradeDomain.ItemId id: sellersList) {
            TradeApi.ItemId.Builder item = TradeApi.ItemId.newBuilder();
            bd.setSellerItemIds(j, item.setItemId(id.getItemId()));
            j++;
        }

        return bd;
    }
}