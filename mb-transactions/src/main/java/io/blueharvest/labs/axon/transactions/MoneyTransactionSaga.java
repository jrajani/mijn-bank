package io.blueharvest.labs.axon.transactions;

import io.blueharvest.labs.axon.common.command.*;
import io.blueharvest.labs.axon.common.event.*;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.axonframework.modelling.saga.SagaLifecycle.end;

@Saga
public class MoneyTransactionSaga {

    private static final Logger LOG = LoggerFactory.getLogger(MoneyTransactionSaga.class);

    private transient CommandGateway commandGateway;
    private transient DeadlineManager deadlineManager;

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setDeadlineManager(DeadlineManager deadlineManager) {
        this.deadlineManager = deadlineManager;
    }

    private Integer sourceAccountId;
    private Integer targetAccountId;
    private String transactionId;
    private String deadlineId;

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void on(TransactionInitiatedEvent event) {

        LOG.debug("In TransactionInitiatedEvent");

        this.sourceAccountId = event.getSourceAccountId();
        this.targetAccountId = event.getTargetAccountId();
        this.transactionId = event.getTransactionId();

        this.deadlineId = deadlineManager.schedule(Duration.ofSeconds(5), "transferDeadline");


        SagaLifecycle.associateWith("transactionId", transactionId);

        commandGateway.send(new WithdrawMoneyCommand(event.getSourceAccountId(), transactionId, event.getAmount()),
                new FutureCallback<WithdrawMoneyCommand, Object>() {
                    @Override
                    public void onResult(CommandMessage<? extends WithdrawMoneyCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
                        if (commandResultMessage.isExceptional()) {
                            commandGateway.send(new CancelMoneyTransactionCommand(event.getTransactionId(), event.getAmount(), "E1"));
                        }
                    }
                });
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void on(MoneyWithdrawnEvent event) {
        LOG.debug("In MoneyWithdrawnEvent");
        commandGateway.send(new DepositMoneyCommand(targetAccountId, event.getTransactionId(), event.getAmount()),
                new FutureCallback<DepositMoneyCommand, Object>() {
                    @Override
                    public void onResult(CommandMessage<? extends DepositMoneyCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
                        if (commandResultMessage.isExceptional()) {
                            commandGateway.send(new CancelMoneyTransactionCommand(event.getTransactionId(), event.getAmount(), "E2"));
                        }
                    }
                });
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void on(MoneyDepositedEvent event) {
        LOG.debug("In MoneyDepositedEvent");
        commandGateway.send(new CompleteMoneyTransactionCommand(transactionId));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void on(TransactionCompletedEvent event) {
        LOG.debug("In TransactionCompletedEvent");
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void on(TransactionCancelledEvent event) {
        LOG.debug("In TransactionCancelledEvent");
        if(event.getErrorCode().equals("E2")) {
            // Generate Compensatory action
            commandGateway.send(new BalanceCorrectionCommand(sourceAccountId, event.getTransactionId(), event.getAmount()));
        }
        end();
    }

    @DeadlineHandler(deadlineName = "transferDeadline")
    public void on() {
        // handle the Deadline
        LOG.debug("In the deadline - deadlineId [{}] ", deadlineId);
    }
}
