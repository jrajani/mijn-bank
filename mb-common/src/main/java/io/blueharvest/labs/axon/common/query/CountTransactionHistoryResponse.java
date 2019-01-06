package io.blueharvest.labs.axon.common.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountTransactionHistoryResponse {
    private int count;
}
