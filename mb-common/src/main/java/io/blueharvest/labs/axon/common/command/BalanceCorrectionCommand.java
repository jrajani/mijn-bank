package io.blueharvest.labs.axon.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceCorrectionCommand {

    @TargetAggregateIdentifier
    Integer accountId;

    String transactionId;

    Integer amount;
}
