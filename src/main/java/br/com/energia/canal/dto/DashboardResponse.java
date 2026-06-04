package br.com.energia.canal.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        ClienteResponse cliente,
        FaturaResponse proximaFatura,
        BigDecimal totalPendente,
        int faturasPendentes,
        int consumoAtualKwh,
        int mediaRegiaoKwh,
        int solicitacoesAbertas,
        int notificacoesNaoLidas,
        List<ConsumoResponse> historicoConsumo
) {}
