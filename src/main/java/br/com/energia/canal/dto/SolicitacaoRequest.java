package br.com.energia.canal.dto;

import br.com.energia.canal.entity.Solicitacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoRequest(
        @NotNull(message = "Tipo é obrigatório") Solicitacao.TipoSolicitacao tipo,
        @NotBlank(message = "Descrição é obrigatória") String descricao
) {}
