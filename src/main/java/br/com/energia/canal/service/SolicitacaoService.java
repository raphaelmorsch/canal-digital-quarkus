package br.com.energia.canal.service;

import br.com.energia.canal.dto.SolicitacaoRequest;
import br.com.energia.canal.dto.SolicitacaoResponse;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.Solicitacao;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class SolicitacaoService {

    private static final AtomicLong SEQUENCIA = new AtomicLong(1000);

    public List<SolicitacaoResponse> listar(Cliente cliente) {
        return Solicitacao.<Solicitacao>find("cliente", Sort.descending("criadoEm"), cliente).list().stream()
                .map(SolicitacaoResponse::from)
                .toList();
    }

    @Transactional
    public SolicitacaoResponse criar(Cliente cliente, SolicitacaoRequest request) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.cliente = cliente;
        solicitacao.tipo = request.tipo();
        solicitacao.descricao = request.descricao().trim();
        solicitacao.status = Solicitacao.StatusSolicitacao.ABERTA;
        solicitacao.criadoEm = LocalDateTime.now();
        solicitacao.protocolo = gerarProtocolo();
        solicitacao.persist();
        return SolicitacaoResponse.from(solicitacao);
    }

    private String gerarProtocolo() {
        return Year.now().getValue() + "-" + String.format("%08d", SEQUENCIA.incrementAndGet());
    }
}
