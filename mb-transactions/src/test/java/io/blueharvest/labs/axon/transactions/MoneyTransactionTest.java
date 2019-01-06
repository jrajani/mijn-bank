package io.blueharvest.labs.axon.transactions;

import io.blueharvest.labs.axon.common.command.CompleteMoneyTransactionCommand;
import io.blueharvest.labs.axon.common.command.InitiateMoneyTransactionCommand;
import io.blueharvest.labs.axon.common.event.TransactionCompletedEvent;
import io.blueharvest.labs.axon.common.event.TransactionInitiatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

public class MoneyTransactionTest {

    private FixtureConfiguration<MoneyTransaction> fixture;

    private final String TEST_TRANSACTION_ID = "567";
    private final Integer TEST_SOURCE_ACCOUNT_ID = 1234;
    private final Integer TEST_TARGET_ACCOUNT_ID = 2345;
    
    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(MoneyTransaction.class);
    }

    @Test
    public void createNewTransaction() {
        fixture.givenNoPriorActivity()
                .when(new InitiateMoneyTransactionCommand(TEST_TRANSACTION_ID, TEST_SOURCE_ACCOUNT_ID, TEST_TARGET_ACCOUNT_ID, 100))
                .expectEvents(new TransactionInitiatedEvent(TEST_TRANSACTION_ID, TEST_SOURCE_ACCOUNT_ID, TEST_TARGET_ACCOUNT_ID, 100));
    }

    @Test
    public void finishTransaction() {
        fixture.given(new TransactionInitiatedEvent(TEST_TRANSACTION_ID, TEST_SOURCE_ACCOUNT_ID, TEST_TARGET_ACCOUNT_ID, 100))
                .when(new CompleteMoneyTransactionCommand(TEST_TRANSACTION_ID))
                .expectEvents(new TransactionCompletedEvent(TEST_TRANSACTION_ID));
    }
}
