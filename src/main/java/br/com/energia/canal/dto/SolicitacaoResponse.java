package br.com.energia.canal.dto;

import br.com.energia.canal.entity.Solicitacao;
import java.time.LocalDateTime;

public record SolicitacaoResponse(
        Long id,
        String tipo,
        String descricao,
        String status,
        String protocolo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static SolicitacaoResponse from(Solicitacao s) {
        return new SolicitacaoResponse(
                s.id,
                s.tipo.name(),
                s.descricao,
                s.status.name(),
                s.protocolo,
                s.criadoEm,
                s.atualizadoEm
        );
    }
}
