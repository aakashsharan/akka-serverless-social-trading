package com.lightbend.gsa.trade.action;

import com.akkaserverless.javasdk.action.Action;
import com.akkaserverless.javasdk.action.Handler;
import com.lightbend.gsa.trade.domain.TradeDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Action that subscribed to journal and publishing messages to a topic.
@Action
public class TradePublisherTopicAction {

    private static final Logger LOG = LoggerFactory.getLogger(TradePublisherTopicAction.class);

    @Handler
    public TradeDomain.TradeOffered publishCreated(TradeDomain.TradeOffered in) {
        LOG.info("`publishAdded` publishing: {} to topic", in);
        return in;
    }

    @Handler
    public TradeDomain.TradeAccepted publishAccepted(TradeDomain.TradeAccepted in) {
        LOG.info("`publishAccepted` publishing: {} to topic", in);
        return in;
    }

    @Handler
    public TradeDomain.TradeRejected publishRejected(TradeDomain.TradeRejected in) {
        LOG.info("`publishRejected` publishing: {} to topic", in);
        return in;
    }
}
