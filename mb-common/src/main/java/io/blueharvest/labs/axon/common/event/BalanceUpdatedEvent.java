package io.blueharvest.labs.axon.common.event;

public interface BalanceUpdatedEvent {

    Integer getAccountId();

    Integer getBalance();
}
