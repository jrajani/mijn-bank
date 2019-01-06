package io.blueharvest.labs.axon.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

    private Long id;

    private Integer accountId;
    private String transactionId;
    private Integer amount;

    public TransactionHistory(Integer accountId, String transactionId, Integer amount) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.amount = amount;
    }
}
