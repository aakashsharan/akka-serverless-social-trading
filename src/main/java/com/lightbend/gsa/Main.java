package com.lightbend.gsa;

import com.akkaserverless.javasdk.AkkaServerless;
import com.lightbend.gsa.trade.TradeApi;
import com.lightbend.gsa.trade.domain.TradeEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lightbend.gsa.MainComponentRegistrations.withGeneratedComponentsAdded;

public final class Main {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static final AkkaServerless SERVICE = new AkkaServerless()
            .registerEventSourcedEntity(TradeEntityImpl.class,
                    TradeApi.getDescriptor().findServiceByName("TradeService"),
                    com.lightbend.gsa.trade.domain.TradeDomain.getDescriptor());

    
    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
        SERVICE.start().toCompletableFuture().get();
    }
}