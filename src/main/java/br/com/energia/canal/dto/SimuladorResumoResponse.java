package br.com.energia.canal.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimuladorResumoResponse(
        int consumoAtualKwh,
        int mediaRegiaoKwh,
        BigDecimal tarifaMediaKwh,
        List<DicaEconomiaResponse> dicas) {}
