package com.anymind.pos.dto;

public record AdditionalItemDTO(String last4,
                                String bankAccount,
                                String chequeNumber,
                                String courier) {
}
