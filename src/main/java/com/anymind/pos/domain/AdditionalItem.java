package com.anymind.pos.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdditionalItem {
    private String last4;
    private String bankAccount;
    private String chequeNumber;
    private String courier;
}
