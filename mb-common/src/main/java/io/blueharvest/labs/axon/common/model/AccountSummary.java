package io.blueharvest.labs.axon.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummary {

    private Integer id;
    private String name;
    private int balance;
}
