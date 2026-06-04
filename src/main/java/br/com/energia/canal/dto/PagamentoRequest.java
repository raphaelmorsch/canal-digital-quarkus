package br.com.energia.canal.dto;

import jakarta.validation.constraints.NotBlank;

public record PagamentoRequest(@NotBlank(message = "Forma de pagamento é obrigatória") String formaPagamento) {}
