package io.blueharvest.labs.axon.transactions;

import io.blueharvest.labs.axon.common.command.CancelMoneyTransactionCommand;
import io.blueharvest.labs.axon.common.command.CompleteMoneyTransactionCommand;
import io.blueharvest.labs.axon.common.command.InitiateMoneyTransactionCommand;
import io.blueharvest.labs.axon.common.event.TransactionCancelledEvent;
import io.blueharvest.labs.axon.common.event.TransactionCompletedEvent;
import io.blueharvest.labs.axon.common.event.TransactionInitiatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;


@NoArgsConstructor
@Aggregate
public class MoneyTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(MoneyTransaction.class);

    @AggregateIdentifier
    private String transactionId;

    @CommandHandler
    public MoneyTransaction(InitiateMoneyTransactionCommand cmd) {
        LOG.debug("In InitiateMoneyTransactionCommand");
        apply(new TransactionInitiatedEvent(cmd.getTransactionId(), cmd.getSourceAccountId(), cmd.getTargetAccountId(),
                cmd.getAmount()));
    }

    @CommandHandler
    public void handle(CompleteMoneyTransactionCommand cmd) {
        LOG.debug("In CompleteMoneyTransactionCommand");
        apply(new TransactionCompletedEvent(transactionId));
    }

    @CommandHandler
    public void handle(CancelMoneyTransactionCommand cmd) {
        LOG.debug("In CancelMoneyTransactionCommand");
        apply(new TransactionCancelledEvent(transactionId, cmd.getAmount(), cmd.getErrorCode()));
    }

    @EventSourcingHandler
    protected void on(TransactionInitiatedEvent event) {
        LOG.debug("In TransactionInitiatedEvent");
        this.transactionId = event.getTransactionId();
    }

    @EventSourcingHandler
    protected void on(TransactionCompletedEvent event) {
        LOG.debug("In TransactionCompletedEvent");
    }
}
