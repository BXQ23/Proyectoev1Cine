package com.cine.benja.cine_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record QueueRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]{2,127}", message = "Nombre de cola inválido") String name,
    Boolean durable,
    @Pattern(regexp = "|[A-Za-z0-9][A-Za-z0-9._-]{2,127}", message = "Nombre de DLQ inválido") String deadLetterQueue
) { }
