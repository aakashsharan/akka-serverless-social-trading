package com.lightbend.gsa.trade.domain;

import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.google.protobuf.Empty;
import com.lightbend.gsa.trade.TradeApi;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.assertThrows;

public class TradeEntityTest {
    private String entityId = "entityId1";
    private TradeEntityImpl entity;
    private CommandContext context = Mockito.mock(CommandContext.class);
    
    private class MockedContextFailure extends RuntimeException {};
    
    @Test
    public void createTradeTest() {
        entity = new TradeEntityImpl(entityId);
        
        Mockito.when(context.fail("The command handler for `CreateTrade` is not implemented, yet"))
            .thenReturn(new MockedContextFailure());
        
        // TODO: set fields in command, and update assertions to match implementation
        assertThrows(MockedContextFailure.class, () -> {
            entity.createTradeWithReply(TradeApi.CreateTradeItem.newBuilder().build(), context);
        });
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void acceptTradeTest() {
        entity = new TradeEntityImpl(entityId);
        
        Mockito.when(context.fail("The command handler for `AcceptTrade` is not implemented, yet"))
            .thenReturn(new MockedContextFailure());
        
        // TODO: set fields in command, and update assertions to match implementation
        assertThrows(MockedContextFailure.class, () -> {
            entity.acceptTradeWithReply(TradeApi.TradeItem.newBuilder().build(), context);
        });
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void rejectTradeTest() {
        entity = new TradeEntityImpl(entityId);
        
        Mockito.when(context.fail("The command handler for `RejectTrade` is not implemented, yet"))
            .thenReturn(new MockedContextFailure());
        
        // TODO: set fields in command, and update assertions to match implementation
        assertThrows(MockedContextFailure.class, () -> {
            entity.rejectTradeWithReply(TradeApi.TradeItem.newBuilder().build(), context);
        });
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void getTradeTest() {
        entity = new TradeEntityImpl(entityId);
        
        Mockito.when(context.fail("The command handler for `GetTrade` is not implemented, yet"))
            .thenReturn(new MockedContextFailure());
        
        // TODO: set fields in command, and update assertions to match implementation
        assertThrows(MockedContextFailure.class, () -> {
            entity.getTradeWithReply(TradeApi.TradeItem.newBuilder().build(), context);
        });
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
}