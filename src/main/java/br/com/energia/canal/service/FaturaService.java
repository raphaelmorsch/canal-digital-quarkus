package br.com.energia.canal.service;

import br.com.energia.canal.dto.FaturaResponse;
import br.com.energia.canal.dto.PagamentoRequest;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.Fatura;
import br.com.energia.canal.entity.Notificacao;
import br.com.energia.canal.exception.CanalException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FaturaService {

    public List<FaturaResponse> listar(Cliente cliente) {
        return Fatura.<Fatura>find("cliente", Sort.descending("referencia"), cliente).list().stream()
                .map(FaturaResponse::from)
                .toList();
    }

    public FaturaResponse buscar(Cliente cliente, Long id) {
        Fatura fatura = Fatura.find("id = ?1 and cliente = ?2", id, cliente).firstResult();
        if (fatura == null) {
            throw CanalException.notFound("Fatura não encontrada");
        }
        return FaturaResponse.from(fatura);
    }

    @Transactional
    public FaturaResponse pagar(Cliente cliente, Long id, PagamentoRequest request) {
        Fatura fatura = Fatura.find("id = ?1 and cliente = ?2", id, cliente).firstResult();
        if (fatura == null) {
            throw CanalException.notFound("Fatura não encontrada");
        }
        if (fatura.status == Fatura.StatusFatura.PAGA) {
            throw CanalException.badRequest("Esta fatura já está paga");
        }

        fatura.status = Fatura.StatusFatura.PAGA;
        fatura.persist();

        Notificacao notificacao = new Notificacao();
        notificacao.cliente = cliente;
        notificacao.titulo = "Pagamento confirmado";
        notificacao.mensagem =
                "Pagamento da fatura " + fatura.referencia + " via " + request.formaPagamento()
                        + " registrado com sucesso.";
        notificacao.criadoEm = LocalDateTime.now();
        notificacao.persist();

        return FaturaResponse.from(fatura);
    }
}
