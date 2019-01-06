package io.blueharvest.labs.axon.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyWithdrawnEvent implements BalanceUpdatedEvent {
    Integer accountId;
    String transactionId;
    Integer amount;
    Integer balance;
}
