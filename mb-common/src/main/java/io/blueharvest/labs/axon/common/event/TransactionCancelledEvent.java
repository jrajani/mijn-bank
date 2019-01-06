package io.blueharvest.labs.axon.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCancelledEvent {
    String transactionId;
    Integer amount;
    String errorCode;
}
