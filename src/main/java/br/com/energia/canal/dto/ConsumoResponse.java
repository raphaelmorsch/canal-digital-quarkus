package br.com.energia.canal.dto;

import br.com.energia.canal.entity.ConsumoMensal;

public record ConsumoResponse(String referencia, int consumoKwh, int mediaRegiao) {
    public static ConsumoResponse from(ConsumoMensal c) {
        return new ConsumoResponse(c.referencia, c.consumoKwh, c.mediaRegiao);
    }
}
