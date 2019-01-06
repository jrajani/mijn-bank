package io.blueharvest.labs.axon.queries.handler;

import io.blueharvest.labs.axon.common.event.MoneyDepositedEvent;
import io.blueharvest.labs.axon.common.event.MoneyWithdrawnEvent;
import io.blueharvest.labs.axon.common.model.TransactionHistory;
import io.blueharvest.labs.axon.common.query.CountTransactionHistoryQuery;
import io.blueharvest.labs.axon.common.query.CountTransactionHistoryResponse;
import io.blueharvest.labs.axon.common.query.FindTransactionHistoryQuery;
import io.blueharvest.labs.axon.common.query.FindTransactionHistoryResponse;
import io.blueharvest.labs.axon.queries.repository.TransactionHistoryRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TransactionEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionEventHandler.class);

    @Autowired
    private TransactionHistoryRepository repository;


    @EventHandler
    public void on(MoneyDepositedEvent event) {
        LOG.debug("In MoneyDepositedEvent");
        repository.save(new TransactionHistory(event.getAccountId(), event.getTransactionId(), event.getAmount()));
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        LOG.debug("In MoneyWithdrawnEvent");
        repository.save(new TransactionHistory(event.getAccountId(), event.getTransactionId(), -event.getAmount()));
    }

    @QueryHandler
    public FindTransactionHistoryResponse handle(FindTransactionHistoryQuery query) {
        LOG.debug("In FindTransactionHistoryQuery");
        List<TransactionHistory> transactionHistoryList = new ArrayList<>();
        Iterator<TransactionHistory> iterator = repository.findAll(new PageRequest(query.getOffset(), query.getLimit())).iterator();
        iterator.forEachRemaining(transactionHistoryList::add);
        return new FindTransactionHistoryResponse(transactionHistoryList);
    }

    @QueryHandler
    public CountTransactionHistoryResponse handle(CountTransactionHistoryQuery query) {
        LOG.debug("In CountTransactionHistoryQuery");
        return new CountTransactionHistoryResponse(repository.findAll().size());
    }


}
