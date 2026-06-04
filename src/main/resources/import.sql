-- Clientes demo (senha: 123456)
INSERT INTO clientes (id, cpf, nome, email, senha, telefone, numero_instalacao, endereco, cidade, uf, cep, tipo_tarifa, ativo) VALUES
(1, '52998224725', 'Maria Silva Santos', 'maria@email.com', '123456', '(11) 98765-4321', 'INST-00012345', 'Rua das Flores, 120 - Apto 42', 'São Paulo', 'SP', '01310-100', 'Residencial B1', true),
(2, '39053344705', 'João Pedro Oliveira', 'joao@email.com', '123456', '(21) 99876-5432', 'INST-00067890', 'Av. Brasil, 500', 'Rio de Janeiro', 'RJ', '22041-080', 'Comercial A4', true);

-- Faturas Maria
INSERT INTO faturas (id, cliente_id, referencia, data_vencimento, data_emissao, valor, consumo_kwh, status, codigo_barras) VALUES
(1, 1, '2026-06', DATE '2026-06-15', DATE '2026-05-28', 187.45, 320, 'PENDENTE', '34191090080123456789012345678901234567890123'),
(2, 1, '2026-05', DATE '2026-05-15', DATE '2026-04-28', 165.20, 285, 'PAGA', '34191090080123456789012345678901234567890124'),
(3, 1, '2026-04', DATE '2026-04-15', DATE '2026-03-28', 142.80, 248, 'PAGA', '34191090080123456789012345678901234567890125'),
(4, 1, '2026-03', DATE '2026-03-15', DATE '2026-02-28', 198.90, 342, 'VENCIDA', '34191090080123456789012345678901234567890126');

-- Faturas João
INSERT INTO faturas (id, cliente_id, referencia, data_vencimento, data_emissao, valor, consumo_kwh, status, codigo_barras) VALUES
(5, 2, '2026-06', DATE '2026-06-20', DATE '2026-05-30', 542.10, 890, 'PENDENTE', '34191090080223456789012345678901234567890223'),
(6, 2, '2026-05', DATE '2026-05-20', DATE '2026-04-30', 498.30, 820, 'PAGA', '34191090080223456789012345678901234567890224');

-- Consumo Maria
INSERT INTO consumo_mensal (id, cliente_id, referencia, consumo_kwh, media_regiao) VALUES
(1, 1, '2026-06', 320, 295),
(2, 1, '2026-05', 285, 290),
(3, 1, '2026-04', 248, 285),
(4, 1, '2026-03', 342, 280),
(5, 1, '2026-02', 310, 275),
(6, 1, '2026-01', 268, 270);

-- Consumo João
INSERT INTO consumo_mensal (id, cliente_id, referencia, consumo_kwh, media_regiao) VALUES
(7, 2, '2026-06', 890, 750),
(8, 2, '2026-05', 820, 740),
(9, 2, '2026-04', 795, 735);

-- Solicitações
INSERT INTO solicitacoes (id, cliente_id, tipo, descricao, status, criado_em, atualizado_em, protocolo) VALUES
(1, 1, 'SEGUNDA_VIA', 'Solicitação de segunda via da fatura 2026-03', 'CONCLUIDA', TIMESTAMP '2026-04-10 09:15:00', TIMESTAMP '2026-04-11 14:30:00', '2026-00001001'),
(2, 1, 'INFORMACAO_CONSUMO', 'Dúvida sobre pico de consumo em março', 'EM_ANDAMENTO', TIMESTAMP '2026-05-20 11:00:00', TIMESTAMP '2026-05-21 08:45:00', '2026-00001002');

-- Notificações
INSERT INTO notificacoes (id, cliente_id, titulo, mensagem, criado_em, lida) VALUES
(1, 1, 'Fatura disponível', 'Sua fatura de referência 2026-06 já está disponível para consulta e pagamento.', TIMESTAMP '2026-05-28 08:00:00', false),
(2, 1, 'Manutenção programada', 'Haverá interrupção programada na rede em 12/06 das 08h às 12h na sua região.', TIMESTAMP '2026-05-25 16:30:00', false),
(3, 1, 'Dica de economia', 'Reduza o consumo em horários de pico (18h-21h) e economize até 15% na tarifa.', TIMESTAMP '2026-05-15 10:00:00', true),
(4, 2, 'Fatura disponível', 'Fatura comercial 2026-06 disponível. Vencimento em 20/06.', TIMESTAMP '2026-05-30 09:00:00', false);
