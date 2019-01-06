package io.blueharvest.labs.axon.common.query;

import io.blueharvest.labs.axon.common.model.TransactionHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindTransactionHistoryResponse {
    List<TransactionHistory> results;
}
