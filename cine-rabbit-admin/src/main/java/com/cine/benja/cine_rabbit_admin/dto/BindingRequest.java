package com.cine.benja.cine_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BindingRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]{2,127}", message = "Nombre de cola inválido") String queue,
    @NotBlank @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]{2,127}", message = "Nombre de exchange inválido") String exchange,
    @NotBlank @Pattern(regexp = "[A-Za-z0-9_*#.-]{1,255}", message = "Routing key inválida") String routingKey
) { }
