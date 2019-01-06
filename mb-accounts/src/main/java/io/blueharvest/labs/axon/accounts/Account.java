package io.blueharvest.labs.axon.accounts;

import io.blueharvest.labs.axon.common.command.BalanceCorrectionCommand;
import io.blueharvest.labs.axon.common.command.CreateAccountCommand;
import io.blueharvest.labs.axon.common.command.DepositMoneyCommand;
import io.blueharvest.labs.axon.common.command.WithdrawMoneyCommand;
import io.blueharvest.labs.axon.common.event.AccountCreatedEvent;
import io.blueharvest.labs.axon.common.event.BalanceUpdatedEvent;
import io.blueharvest.labs.axon.common.event.MoneyDepositedEvent;
import io.blueharvest.labs.axon.common.event.MoneyWithdrawnEvent;
import io.blueharvest.labs.axon.common.exception.DepositNotPermittedException;
import io.blueharvest.labs.axon.common.exception.InsufficientBalanceException;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;


@Aggregate
@NoArgsConstructor
public class Account {

    private static final Logger LOG = LoggerFactory.getLogger(Account.class);

    @AggregateIdentifier
    private Integer accountId;
    private Integer balance;
    private String accountHolderName;

    @CommandHandler
    public Account(CreateAccountCommand cmd) {
        LOG.debug("In CreateAccountCommand");
        apply(new AccountCreatedEvent(cmd.getAccountId(), cmd.getAccountHolderName()));
    }

    @CommandHandler
    public void handle(WithdrawMoneyCommand cmd) throws InsufficientBalanceException {
        LOG.debug("In WithdrawMoneyCommand");
        if (balance < cmd.getAmount()) {
            throw new InsufficientBalanceException("Insufficient Amount");
        }
        apply(new MoneyWithdrawnEvent(accountId, cmd.getTransactionId(), cmd.getAmount(), balance - cmd.getAmount()));
    }

    @CommandHandler
    public void handle(DepositMoneyCommand cmd) throws DepositNotPermittedException {
        LOG.debug("In DepositMoneyCommand");
        // Following is just a hypothetical business rule for demo purpose
        if ((balance + cmd.getAmount()) > 100) {
            throw new DepositNotPermittedException("Too much balance");
        }
        apply(new MoneyDepositedEvent(cmd.getAccountId(), cmd.getTransactionId(), cmd.getAmount(), balance + cmd.getAmount()));
    }

    @CommandHandler
    public void handle(BalanceCorrectionCommand cmd) {
        LOG.debug("In BalanceCorrectionCommand");
        apply(new MoneyDepositedEvent(cmd.getAccountId(), cmd.getTransactionId(), cmd.getAmount(), balance + cmd.getAmount()));
    }

    @EventSourcingHandler
    protected void on(AccountCreatedEvent event) {
        LOG.debug("In AccountCreatedEvent");
        this.accountId = event.getAccountId();
        this.accountHolderName = event.getAccountHolderName();
        this.balance = 0;
    }

    @EventSourcingHandler
    protected void on(BalanceUpdatedEvent event) {
        LOG.debug("In BalanceUpdatedEvent");
        this.accountId = event.getAccountId();
        this.balance = event.getBalance();
    }
}

