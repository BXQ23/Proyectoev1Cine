package com.cine.benja.cine_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ExchangeRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]{2,127}", message = "Nombre de exchange inválido") String name,
    @NotBlank @Pattern(regexp = "direct|topic|fanout", message = "Tipo permitido: direct, topic o fanout") String type,
    Boolean durable
) { }
