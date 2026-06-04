package br.com.energia.canal.dto;

import br.com.energia.canal.entity.Fatura;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturaResponse(
        Long id,
        String referencia,
        LocalDate dataVencimento,
        LocalDate dataEmissao,
        BigDecimal valor,
        int consumoKwh,
        String status,
        String codigoBarras
) {
    public static FaturaResponse from(Fatura f) {
        return new FaturaResponse(
                f.id,
                f.referencia,
                f.dataVencimento,
                f.dataEmissao,
                f.valor,
                f.consumoKwh,
                f.status.name(),
                f.codigoBarras
        );
    }
}
