package io.blueharvest.labs.axon.common.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountCommand {

    @TargetAggregateIdentifier
    Integer accountId;

    String accountHolderName;
}
