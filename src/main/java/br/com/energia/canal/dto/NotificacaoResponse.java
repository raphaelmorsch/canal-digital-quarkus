package br.com.energia.canal.dto;

import br.com.energia.canal.entity.Notificacao;
import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id, String titulo, String mensagem, LocalDateTime criadoEm, boolean lida
) {
    public static NotificacaoResponse from(Notificacao n) {
        return new NotificacaoResponse(n.id, n.titulo, n.mensagem, n.criadoEm, n.lida);
    }
}
