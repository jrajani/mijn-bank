package io.blueharvest.labs.axon.ui;

import com.vaadin.data.provider.CallbackDataProvider;
import io.blueharvest.labs.axon.common.model.*;
import io.blueharvest.labs.axon.common.query.CountAccountSummaryQuery;
import io.blueharvest.labs.axon.common.query.CountAccountSummaryResponse;
import io.blueharvest.labs.axon.common.query.FindAccountSummaryQuery;
import io.blueharvest.labs.axon.common.query.FindAccountSummaryResponse;
import org.axonframework.queryhandling.QueryGateway;

public class AccountSummaryDataProvider extends CallbackDataProvider<AccountSummary, Void> {
    public AccountSummaryDataProvider(QueryGateway queryGateway) {
        super(q -> {
            FindAccountSummaryQuery query = new FindAccountSummaryQuery(q.getOffset(), q.getLimit());
            FindAccountSummaryResponse response = queryGateway.query(query, FindAccountSummaryResponse.class).join();
            return response.getResults().stream();
        }, q -> {
            CountAccountSummaryQuery query = new CountAccountSummaryQuery();
            CountAccountSummaryResponse response = queryGateway.query(query, CountAccountSummaryResponse.class).join();
            return response.getCount();
        });
    }
}
