package com.anymind.pos.exception;

import java.util.Map;

public record ApiError(
        String error,
        Map<String, String> details
) {}
