package com.example.test.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReservationRequestDTO(@NotNull String name) {}
