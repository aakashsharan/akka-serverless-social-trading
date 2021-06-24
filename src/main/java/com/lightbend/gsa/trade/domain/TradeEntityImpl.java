package com.lightbend.gsa.trade.domain;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import com.lightbend.gsa.trade.TradeApi;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "event-sourced-trade")
public class TradeEntityImpl extends TradeEntityInterface {
    @SuppressWarnings("unused")
    private final String entityId;
    
    public TradeEntityImpl(@EntityId String entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public TradeDomain.TradeState snapshot() {
        // TODO: produce state snapshot here
        return TradeDomain.TradeState.newBuilder().build();
    }
    
    @Override
    public void handleSnapshot(TradeDomain.TradeState snapshot) {
        // TODO: restore state from snapshot here
        
    }
    
    @Override
    protected Empty createTrade(TradeApi.CreateTradeItem command, CommandContext ctx) {
        throw ctx.fail("The command handler for `CreateTrade` is not implemented, yet");
    }
    
    @Override
    protected Empty acceptTrade(TradeApi.AcceptTradeItem command, CommandContext ctx) {
        throw ctx.fail("The command handler for `AcceptTrade` is not implemented, yet");
    }
    
    @Override
    protected Empty rejectTrade(TradeApi.RejectTradeItem command, CommandContext ctx) {
        throw ctx.fail("The command handler for `RejectTrade` is not implemented, yet");
    }
    
    @Override
    protected TradeApi.Trade getTrade(TradeApi.GetTradeItem command, CommandContext ctx) {
        throw ctx.fail("The command handler for `GetTrade` is not implemented, yet");
    }
    
    @Override
    public void tradeOffered(TradeDomain.TradeOffered event) {
        throw new RuntimeException("The event handler for `TradeOffered` is not implemented, yet");
    }
    
    @Override
    public void tradeAccepted(TradeDomain.TradeAccepted event) {
        throw new RuntimeException("The event handler for `TradeAccepted` is not implemented, yet");
    }
    
    @Override
    public void tradeRejected(TradeDomain.TradeRejected event) {
        throw new RuntimeException("The event handler for `TradeRejected` is not implemented, yet");
    }
}