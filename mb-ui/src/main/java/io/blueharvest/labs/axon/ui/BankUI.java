package io.blueharvest.labs.axon.ui;

import com.vaadin.annotations.Push;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import io.blueharvest.labs.axon.common.command.CreateAccountCommand;
import io.blueharvest.labs.axon.common.command.DepositMoneyCommand;
import io.blueharvest.labs.axon.common.command.InitiateMoneyTransactionCommand;
import io.blueharvest.labs.axon.common.model.AccountSummary;
import io.blueharvest.labs.axon.common.model.TransactionHistory;
import io.blueharvest.labs.axon.ui.data.providers.AccountSummaryDataProvider;
import io.blueharvest.labs.axon.ui.data.providers.TransactionHistoryDataProvider;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@SpringUI
@Push
public class BankUI extends UI {

    private static final Logger LOG = LoggerFactory.getLogger(UI.class);

    private final transient QueryGateway queryGateway;

    private final transient CommandGateway commandGateway;

    private AccountSummaryDataProvider accountSummaryDataProvider;

    private TransactionHistoryDataProvider transactionHistoryListDataProvider;

    public BankUI(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        LOG.debug("In init");

        HorizontalLayout topHorizontalPanel = new HorizontalLayout();

        topHorizontalPanel.addComponents(addAccountPanel());
        topHorizontalPanel.addComponents(depositMoneyPanel());
        topHorizontalPanel.addComponent(withdrawMoneyPanel());
        topHorizontalPanel.addComponent(transferMoneyPanel());

        topHorizontalPanel.setSizeFull();

        HorizontalLayout bottomHorizontalPanel = new HorizontalLayout();

        bottomHorizontalPanel.addComponents(accountSummaryGrid());
        bottomHorizontalPanel.addComponents(transactionSummaryGrid());

        bottomHorizontalPanel.setSizeFull();

        GridLayout gridLayout = new GridLayout(1, 3);
        gridLayout.setSizeFull();
        gridLayout.addComponent(topHorizontalPanel, 0,0);
        gridLayout.addComponent(bottomHorizontalPanel, 0,1,0,2);
        gridLayout.setMargin(Boolean.TRUE);


        setContent(gridLayout);

        UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                Throwable cause = event.getThrowable();
                while(cause.getCause() != null) cause = cause.getCause();
                Notification.show("Error", cause.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
    }

    private Component addAccountPanel() {

        LOG.debug("In addAccountPanel");

        TextField id = new TextField("ID");
        TextField name = new TextField("Name");
        Button btnCreate = new Button("Create");

        btnCreate.addClickListener(evt -> {
            commandGateway.sendAndWait(new CreateAccountCommand(Integer.parseInt(id.getValue()), name.getValue()));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(e -> accountSummaryDataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, name, btnCreate);
        form.setMargin(true);

        Panel panel = new Panel("Create new account");
        panel.setContent(form);
        return panel;
    }

    private Component depositMoneyPanel() {

        LOG.debug("In depositMoneyPanel");

        TextField id = new TextField("Account Id");
        TextField noOfStudents = new TextField("Amount");
        Button btnDeposit = new Button("Deposit");

        btnDeposit.addClickListener(evt -> {
            commandGateway.sendAndWait(new DepositMoneyCommand(Integer.parseInt(id.getValue()), UUID.randomUUID().toString(), Integer.parseInt(noOfStudents.getValue())));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(e -> accountSummaryDataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, noOfStudents, btnDeposit);
        form.setMargin(true);

        Panel panel = new Panel("Deposit Money");
        panel.setContent(form);
        return panel;
    }

    private Component withdrawMoneyPanel() {

        LOG.debug("In withdrawMoneyPanel");

        TextField id = new TextField("Account Id");
        TextField noOfStudents = new TextField("Amount");
        Button btnDeposit = new Button("Withdraw");

        btnDeposit.addClickListener(evt -> {
            commandGateway.sendAndWait(new DepositMoneyCommand(Integer.parseInt(id.getValue()), UUID.randomUUID().toString(), Integer.parseInt(noOfStudents.getValue()) * (-1)));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(e -> accountSummaryDataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, noOfStudents, btnDeposit);
        form.setMargin(true);

        Panel panel = new Panel("Withdraw Money");
        panel.setContent(form);
        return panel;
    }

    private Component transferMoneyPanel() {

        LOG.debug("In transferMoneyPanel");

        TextField senderId = new TextField("Sender Id");
        TextField receiverId = new TextField("Receiver Id");
        TextField amount = new TextField("Amount");
        Button btnTransfer = new Button("Transfer");

        btnTransfer.addClickListener(evt -> {
            commandGateway.sendAndWait(new InitiateMoneyTransactionCommand(UUID.randomUUID().toString(), Integer.parseInt(senderId.getValue()), Integer.parseInt(receiverId.getValue()), Integer.parseInt(amount.getValue())));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(e -> accountSummaryDataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.addComponents(senderId, receiverId, amount, btnTransfer);
        form.setMargin(true);

        Panel panel = new Panel("Transfer Money");
        panel.setContent(form);
        panel.setHeight("99%");
        return panel;
    }

    private Grid accountSummaryGrid() {

        LOG.debug("In accountSummaryGrid");

        accountSummaryDataProvider = new AccountSummaryDataProvider(queryGateway);
        Grid<AccountSummary> grid = new Grid<>();
        grid.addColumn(AccountSummary::getId).setCaption("Acoount ID");
        grid.addColumn(AccountSummary::getName).setCaption("Holder Name");
        grid.addColumn(AccountSummary::getBalance).setCaption("Balance");
        grid.setSizeFull();
        grid.setDataProvider(accountSummaryDataProvider);
        return grid;
    }

    private Grid transactionSummaryGrid() {

        LOG.debug("In transactionSummaryGrid");

        transactionHistoryListDataProvider = new TransactionHistoryDataProvider(queryGateway);
        Grid<TransactionHistory> grid = new Grid<>();
        grid.addColumn(TransactionHistory::getAccountId).setCaption("Acoount ID");
        grid.addColumn(TransactionHistory::getAmount).setCaption("Amount");
        grid.addColumn(TransactionHistory::getTransactionId).setCaption("Transaction Id");
        grid.setSizeFull();
        grid.setDataProvider(transactionHistoryListDataProvider);
        return grid;
    }
}
