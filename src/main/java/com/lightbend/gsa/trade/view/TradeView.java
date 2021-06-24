package com.lightbend.gsa.trade.view;

import com.akkaserverless.javasdk.view.UpdateHandler;
import com.akkaserverless.javasdk.view.View;
import com.lightbend.gsa.trade.domain.TradeDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@View
public class TradeView {

    private static final Logger LOG = LoggerFactory.getLogger(TradeView.class);

    @UpdateHandler
    public TradeViewModel.TradeViewState processCreated(
            TradeDomain.TradeOffered event,
            Optional<TradeViewModel.TradeViewState> state) {
        String tradeId = event.getTradeId();
        if(state.isPresent()) {
            LOG.info("Inside `processCreated` view for trade_id: {}", tradeId + " and state is present");
            return state.get().toBuilder().setTradeOfferedTimestamp(System.currentTimeMillis()).build();
        } else {
            LOG.info("Inside `processCreated` view for trade_id: {}", tradeId + " and state is new");
            List<TradeDomain.ItemId> domainBuyerList = event.getBuyerItemIdsList();
            List<TradeDomain.ItemId> domainSellerList = event.getSellerItemIdsList();

            LOG.info("Inside `processCreated` view for trade_id: {}", tradeId + " and buliding a new view");
            TradeViewModel.TradeViewState.Builder bd = TradeViewModel.TradeViewState.newBuilder();
            bd.addAllBuyerItemIds(convertDomainItem(domainBuyerList));
            bd.addAllSellerItemIds(convertDomainItem(domainSellerList));
            bd.setTradeId(event.getTradeId()).setBuyerUserId(event.getBuyerUserId())
                    .setSellerUserId(event.getSellerUserId()).setTradeOfferedTimestamp(System.currentTimeMillis())
                    .setStatus(TradeViewModel.TradeViewState.Status.CREATED);

            return bd.build();
        }
    }

    @UpdateHandler
    public TradeViewModel.TradeViewState processAccepted(
            TradeDomain.TradeAccepted event, TradeViewModel.TradeViewState state) {
        LOG.info("Inside `processAccepted` view for trade_id: {}", event.getTradeId());
        return state.toBuilder().setStatus(TradeViewModel.TradeViewState.Status.ACCEPTED).build();
    }

    @UpdateHandler
    public TradeViewModel.TradeViewState processRejected(
            TradeDomain.TradeRejected event, TradeViewModel.TradeViewState state) {
        LOG.info("Inside `processRejected` view for trade_id: {}", event.getTradeId());
        return state.toBuilder().setStatus(TradeViewModel.TradeViewState.Status.REJECTED).build();
    }

    // convert list of tradedomain itemid to list of tradeviewmodel itemid
    private List<TradeViewModel.ItemId> convertDomainItem(List<TradeDomain.ItemId> list) {
        List<TradeViewModel.ItemId> domainList = new LinkedList<>();

        for(TradeDomain.ItemId itemId: list) {
            String id = itemId.getItemId();

            TradeViewModel.ItemId.Builder bd = TradeViewModel.ItemId.newBuilder().setItemId(id);
            domainList.add(bd.build());
        }
        return domainList;
    }
}
