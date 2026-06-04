package br.com.energia.canal.service;

import br.com.energia.canal.dto.NotificacaoResponse;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.Notificacao;
import br.com.energia.canal.exception.CanalException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class NotificacaoService {

    public List<NotificacaoResponse> listar(Cliente cliente) {
        return Notificacao.<Notificacao>find("cliente", Sort.descending("criadoEm"), cliente).list().stream()
                .map(NotificacaoResponse::from)
                .toList();
    }

    @Transactional
    public NotificacaoResponse marcarComoLida(Cliente cliente, Long id) {
        Notificacao notificacao = Notificacao.find("id = ?1 and cliente = ?2", id, cliente).firstResult();
        if (notificacao == null) {
            throw CanalException.notFound("Notificação não encontrada");
        }
        notificacao.lida = true;
        notificacao.persist();
        return NotificacaoResponse.from(notificacao);
    }
}
