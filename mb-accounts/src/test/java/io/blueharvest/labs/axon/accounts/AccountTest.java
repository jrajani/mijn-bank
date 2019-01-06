package io.blueharvest.labs.axon.accounts;

import io.blueharvest.labs.axon.common.command.*;
import io.blueharvest.labs.axon.common.event.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

public class AccountTest {

    private FixtureConfiguration<Account> fixture;

    private final int TEST_ACCOUNT_ID = 1234;
    private final String TEST_ACCOUNT_HOLDER_NAME = "Test";
    private final String TEST_TRANSACTION_ID = "567";

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Account.class);
    }

    @Test
    public void testAccountCreated() {
        fixture.givenNoPriorActivity()
                .when(new CreateAccountCommand(TEST_ACCOUNT_ID, TEST_ACCOUNT_HOLDER_NAME))
                .expectEvents(new AccountCreatedEvent(TEST_ACCOUNT_ID, TEST_ACCOUNT_HOLDER_NAME));
    }

    @Test
    public void testDepositMoneyWithZeroBalance() {
        fixture.given(new AccountCreatedEvent(TEST_ACCOUNT_ID, TEST_ACCOUNT_HOLDER_NAME))
                .when(new DepositMoneyCommand(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 10))
                .expectEvents(new MoneyDepositedEvent(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 10, 10));
    }

    @Test
    public void testDepositMoneyWithSomeBalance() {
        fixture.given(new AccountCreatedEvent(TEST_ACCOUNT_ID, TEST_ACCOUNT_HOLDER_NAME),
                new MoneyDepositedEvent(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 5, 5))
                .when(new DepositMoneyCommand(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 10))
                .expectEvents(new MoneyDepositedEvent(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 10, 15));
    }

    @Test
    public void testWithdrawReasonableAmount() {
        fixture.given(new AccountCreatedEvent(TEST_ACCOUNT_ID, TEST_ACCOUNT_HOLDER_NAME),
                new MoneyDepositedEvent(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 15, 15))
                .when(new WithdrawMoneyCommand(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 5))
                .expectEvents(new MoneyWithdrawnEvent(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 5, 10));
    }

    @Test
    public void testWithdrawUnreasonableAmount() {
        fixture.given(new AccountCreatedEvent(TEST_ACCOUNT_ID, TEST_ACCOUNT_HOLDER_NAME))
                .when(new WithdrawMoneyCommand(TEST_ACCOUNT_ID, TEST_TRANSACTION_ID, 10))
                .expectNoEvents();
    }

}
