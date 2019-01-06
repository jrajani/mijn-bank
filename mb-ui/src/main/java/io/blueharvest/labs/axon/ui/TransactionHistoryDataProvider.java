package io.blueharvest.labs.axon.ui;

import com.vaadin.data.provider.CallbackDataProvider;
import io.blueharvest.labs.axon.common.model.TransactionHistory;
import io.blueharvest.labs.axon.common.query.*;
import org.axonframework.queryhandling.QueryGateway;

public class TransactionHistoryDataProvider extends CallbackDataProvider<TransactionHistory, Void> {
    public TransactionHistoryDataProvider(QueryGateway queryGateway) {
        super(q -> {
            FindTransactionHistoryQuery query = new FindTransactionHistoryQuery(q.getOffset(), q.getLimit());
            FindTransactionHistoryResponse response = queryGateway.query(query, FindTransactionHistoryResponse.class).join();
            return response.getResults().stream();
        }, q -> {
            CountTransactionHistoryQuery query = new CountTransactionHistoryQuery();
            CountTransactionHistoryResponse response = queryGateway.query(query, CountTransactionHistoryResponse.class).join();
            return response.getCount();
        });
    }
}
