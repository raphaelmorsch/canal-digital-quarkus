# Canal Digital — Energia+ (Quarkus)

Aplicação completa que simula o **canal digital do cliente** de uma empresa de energia: frontend SPA + API REST em Quarkus.

## Funcionalidades

- **Autenticação** — login por CPF ou e-mail
- **Dashboard** — resumo de faturas, consumo e solicitações
- **Faturas** — consulta e simulação de pagamento (PIX, cartão, etc.)
- **Consumo** — histórico mensal com gráfico e comparação com a média da região
- **Solicitações** — abertura e acompanhamento de protocolos de serviço
- **Notificações** — avisos com marcação de lidas
- **Minha conta** — dados cadastrais e da instalação

## Pré-requisitos

- Java 17+
- Maven 3.9+

## Executar

```bash
./mvnw quarkus:dev
```

Ou, se não houver wrapper:

```bash
mvn quarkus:dev
```

Acesse:

| Recurso        | URL                          |
|----------------|------------------------------|
| Aplicação web  | http://localhost:8080        |
| API OpenAPI    | http://localhost:8080/api/docs |

## Contas de demonstração

| Usuário            | Senha  |
|--------------------|--------|
| `maria@email.com`  | 123456 |
| `joao@email.com`   | 123456 |

Também é possível entrar com o CPF (somente números): `52998224725` ou `39053344705`.

## API (resumo)

| Método | Endpoint                    | Descrição              |
|--------|-----------------------------|------------------------|
| POST   | `/api/auth/login`           | Login                  |
| POST   | `/api/auth/logout`          | Logout                 |
| GET    | `/api/dashboard`            | Dashboard              |
| GET    | `/api/cliente/me`           | Dados do cliente       |
| GET    | `/api/faturas`              | Listar faturas         |
| POST   | `/api/faturas/{id}/pagar`   | Simular pagamento      |
| GET    | `/api/consumo`              | Histórico de consumo   |
| GET/POST | `/api/solicitacoes`       | Solicitações           |
| GET    | `/api/notificacoes`         | Notificações           |
| POST   | `/api/notificacoes/{id}/ler`| Marcar como lida       |

Todas as rotas autenticadas exigem o header `Authorization: Bearer <token>`.

## Stack

- **Backend:** Quarkus 3.17, REST, Panache, H2 (memória)
- **Frontend:** HTML/CSS/JS vanilla + Chart.js (servido pelo Quarkus)

## Build

```bash
mvn package
java -jar target/quarkus-app/quarkus-run.jar
```
