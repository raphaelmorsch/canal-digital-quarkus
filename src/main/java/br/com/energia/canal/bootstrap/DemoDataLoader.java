package br.com.energia.canal.bootstrap;

import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.ConsumoMensal;
import br.com.energia.canal.entity.Fatura;
import br.com.energia.canal.entity.Notificacao;
import br.com.energia.canal.entity.Solicitacao;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DemoDataLoader {

    private static final Logger LOG = Logger.getLogger(DemoDataLoader.class);

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (Cliente.count() > 0) {
            LOG.info("Dados demo já carregados — ignorando seed.");
            return;
        }

        LOG.info("Carregando dados de demonstração...");

        Cliente maria = criarMaria();
        Cliente joao = criarJoao();

        seedFaturasMaria(maria);
        seedFaturasJoao(joao);
        seedConsumoMaria(maria);
        seedConsumoJoao(joao);
        seedSolicitacoes(maria);
        seedNotificacoes(maria, joao);

        LOG.info("Dados demo carregados: 2 clientes (maria@email.com / joao@email.com, senha 123456).");
    }

    private Cliente criarMaria() {
        Cliente c = new Cliente();
        c.cpf = "52998224725";
        c.nome = "Maria Silva Santos";
        c.email = "maria@email.com";
        c.senha = "123456";
        c.telefone = "(11) 98765-4321";
        c.numeroInstalacao = "INST-00012345";
        c.endereco = "Rua das Flores, 120 - Apto 42";
        c.cidade = "São Paulo";
        c.uf = "SP";
        c.cep = "01310-100";
        c.tipoTarifa = "Residencial B1";
        c.ativo = true;
        c.persist();
        return c;
    }

    private Cliente criarJoao() {
        Cliente c = new Cliente();
        c.cpf = "39053344705";
        c.nome = "João Pedro Oliveira";
        c.email = "joao@email.com";
        c.senha = "123456";
        c.telefone = "(21) 99876-5432";
        c.numeroInstalacao = "INST-00067890";
        c.endereco = "Av. Brasil, 500";
        c.cidade = "Rio de Janeiro";
        c.uf = "RJ";
        c.cep = "22041-080";
        c.tipoTarifa = "Comercial A4";
        c.ativo = true;
        c.persist();
        return c;
    }

    private void seedFaturasMaria(Cliente maria) {
        persistFatura(maria, "2026-06", LocalDate.of(2026, 6, 15), LocalDate.of(2026, 5, 28),
                new BigDecimal("187.45"), 320, Fatura.StatusFatura.PENDENTE,
                "34191090080123456789012345678901234567890123");
        persistFatura(maria, "2026-05", LocalDate.of(2026, 5, 15), LocalDate.of(2026, 4, 28),
                new BigDecimal("165.20"), 285, Fatura.StatusFatura.PAGA,
                "34191090080123456789012345678901234567890124");
        persistFatura(maria, "2026-04", LocalDate.of(2026, 4, 15), LocalDate.of(2026, 3, 28),
                new BigDecimal("142.80"), 248, Fatura.StatusFatura.PAGA,
                "34191090080123456789012345678901234567890125");
        persistFatura(maria, "2026-03", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 2, 28),
                new BigDecimal("198.90"), 342, Fatura.StatusFatura.VENCIDA,
                "34191090080123456789012345678901234567890126");
    }

    private void seedFaturasJoao(Cliente joao) {
        persistFatura(joao, "2026-06", LocalDate.of(2026, 6, 20), LocalDate.of(2026, 5, 30),
                new BigDecimal("542.10"), 890, Fatura.StatusFatura.PENDENTE,
                "34191090080223456789012345678901234567890223");
        persistFatura(joao, "2026-05", LocalDate.of(2026, 5, 20), LocalDate.of(2026, 4, 30),
                new BigDecimal("498.30"), 820, Fatura.StatusFatura.PAGA,
                "34191090080223456789012345678901234567890224");
    }

    private void persistFatura(
            Cliente cliente,
            String referencia,
            LocalDate vencimento,
            LocalDate emissao,
            BigDecimal valor,
            int kwh,
            Fatura.StatusFatura status,
            String codigoBarras) {
        Fatura f = new Fatura();
        f.cliente = cliente;
        f.referencia = referencia;
        f.dataVencimento = vencimento;
        f.dataEmissao = emissao;
        f.valor = valor;
        f.consumoKwh = kwh;
        f.status = status;
        f.codigoBarras = codigoBarras;
        f.persist();
    }

    private void seedConsumoMaria(Cliente maria) {
        persistConsumo(maria, "2026-06", 320, 295);
        persistConsumo(maria, "2026-05", 285, 290);
        persistConsumo(maria, "2026-04", 248, 285);
        persistConsumo(maria, "2026-03", 342, 280);
        persistConsumo(maria, "2026-02", 310, 275);
        persistConsumo(maria, "2026-01", 268, 270);
    }

    private void seedConsumoJoao(Cliente joao) {
        persistConsumo(joao, "2026-06", 890, 750);
        persistConsumo(joao, "2026-05", 820, 740);
        persistConsumo(joao, "2026-04", 795, 735);
    }

    private void persistConsumo(Cliente cliente, String referencia, int kwh, int media) {
        ConsumoMensal c = new ConsumoMensal();
        c.cliente = cliente;
        c.referencia = referencia;
        c.consumoKwh = kwh;
        c.mediaRegiao = media;
        c.persist();
    }

    private void seedSolicitacoes(Cliente maria) {
        Solicitacao s1 = new Solicitacao();
        s1.cliente = maria;
        s1.tipo = Solicitacao.TipoSolicitacao.SEGUNDA_VIA;
        s1.descricao = "Solicitação de segunda via da fatura 2026-03";
        s1.status = Solicitacao.StatusSolicitacao.CONCLUIDA;
        s1.criadoEm = LocalDateTime.of(2026, 4, 10, 9, 15);
        s1.atualizadoEm = LocalDateTime.of(2026, 4, 11, 14, 30);
        s1.protocolo = "2026-00001001";
        s1.persist();

        Solicitacao s2 = new Solicitacao();
        s2.cliente = maria;
        s2.tipo = Solicitacao.TipoSolicitacao.INFORMACAO_CONSUMO;
        s2.descricao = "Dúvida sobre pico de consumo em março";
        s2.status = Solicitacao.StatusSolicitacao.EM_ANDAMENTO;
        s2.criadoEm = LocalDateTime.of(2026, 5, 20, 11, 0);
        s2.atualizadoEm = LocalDateTime.of(2026, 5, 21, 8, 45);
        s2.protocolo = "2026-00001002";
        s2.persist();
    }

    private void seedNotificacoes(Cliente maria, Cliente joao) {
        persistNotificacao(maria, "Fatura disponível",
                "Sua fatura de referência 2026-06 já está disponível para consulta e pagamento.",
                LocalDateTime.of(2026, 5, 28, 8, 0), false);
        persistNotificacao(maria, "Manutenção programada",
                "Haverá interrupção programada na rede em 12/06 das 08h às 12h na sua região.",
                LocalDateTime.of(2026, 5, 25, 16, 30), false);
        persistNotificacao(maria, "Dica de economia",
                "Reduza o consumo em horários de pico (18h-21h) e economize até 15% na tarifa.",
                LocalDateTime.of(2026, 5, 15, 10, 0), true);
        persistNotificacao(joao, "Fatura disponível",
                "Fatura comercial 2026-06 disponível. Vencimento em 20/06.",
                LocalDateTime.of(2026, 5, 30, 9, 0), false);
    }

    private void persistNotificacao(
            Cliente cliente, String titulo, String mensagem, LocalDateTime criadoEm, boolean lida) {
        Notificacao n = new Notificacao();
        n.cliente = cliente;
        n.titulo = titulo;
        n.mensagem = mensagem;
        n.criadoEm = criadoEm;
        n.lida = lida;
        n.persist();
    }
}
