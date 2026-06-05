package br.com.energia.canal.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoEconomiaResponse(
        int consumoAtualKwh,
        int consumoSimuladoKwh,
        BigDecimal valorAtualEstimado,
        BigDecimal valorSimuladoEstimado,
        BigDecimal economiaMensal,
        BigDecimal economiaAnual,
        List<DicaEconomiaResponse> dicas) {}
