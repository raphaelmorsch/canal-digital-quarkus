package br.com.energia.canal.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "CPF ou e-mail é obrigatório") String identificador,
        @NotBlank(message = "Senha é obrigatória") String senha
) {}
