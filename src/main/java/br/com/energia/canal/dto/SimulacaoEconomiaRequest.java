package br.com.energia.canal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SimulacaoEconomiaRequest(@Min(1) @Max(50) int reducaoPercentual) {}
