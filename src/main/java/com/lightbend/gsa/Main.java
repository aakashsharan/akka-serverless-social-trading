package com.lightbend.gsa;

import com.akkaserverless.javasdk.AkkaServerless;
import com.lightbend.gsa.trade.TradeApi;
import com.lightbend.gsa.trade.action.TradePublishEvent;
import com.lightbend.gsa.trade.action.TradePublisherTopicAction;
import com.lightbend.gsa.trade.domain.TradeDomain;
import com.lightbend.gsa.trade.domain.TradeEntityImpl;
import com.lightbend.gsa.trade.view.TradeView;
import com.lightbend.gsa.trade.view.TradeViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lightbend.gsa.MainComponentRegistrations.withGeneratedComponentsAdded;

public final class Main {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static final AkkaServerless SERVICE = new AkkaServerless()
            // event sourced trade entity
            .registerEventSourcedEntity(TradeEntityImpl.class,
                    TradeApi.getDescriptor().findServiceByName("TradeService"),
                    com.lightbend.gsa.trade.domain.TradeDomain.getDescriptor())

            // view of the user trade id
            .registerView(TradeView.class,
                    TradeViewModel.getDescriptor().findServiceByName("TradeViewService"), "trade_by_user",
                    TradeDomain.getDescriptor(), TradeViewModel.getDescriptor())

            // consume trade events emitted to TradeEntity
            // and publish as is to 'stp-trade' topic
            .registerAction(TradePublisherTopicAction.class,
                    TradePublishEvent.getDescriptor().findServiceByName("PublishEventsToTopicService"));

    
    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
        SERVICE.start().toCompletableFuture().get();
    }
}