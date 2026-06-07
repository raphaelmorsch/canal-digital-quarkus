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
- SQL Server (OpenShift/produção) ou H2 (desenvolvimento local)

## Executar localmente

```bash
mvn quarkus:dev
```

Usa **SQL Server** (perfil `dev`). Configure `DB_*` ou port-forward do cluster:

```bash
export DB_HOST=localhost DB_PORT=1433 DB_NAME=canal_digital DB_USERNAME=sa DB_PASSWORD='...'
mvn quarkus:dev
```

## Banco de dados

| Ambiente | Perfil | Banco |
|----------|--------|-------|
| `mvn quarkus:dev` | `dev` | SQL Server (`DB_*`) |
| OpenShift / Docker | `prod` | SQL Server (Secret) |
| `mvn verify` (testes) | `test` | SQL Server via Dev Services (Docker) ou `DB_*` |

### Variáveis (dev, prod e OpenShift)

Todas via ambiente — veja [`openshift/env.example`](openshift/env.example).

| Variável | Obrigatória (prod) | Descrição |
|----------|-------------------|-----------|
| `QUARKUS_PROFILE` | Sim | `dev` local, `prod` OpenShift |
| `DB_HOST` | Sim | Nome do Service SQL Server |
| `DB_PORT` | Sim | Ex.: `1433` |
| `DB_NAME` | Sim | Ex.: `canal_digital` |
| `DB_USERNAME` | Sim | Usuário JDBC |
| `DB_PASSWORD` | Sim | Senha JDBC |
| `QUARKUS_DATASOURCE_JDBC_URL` | Não | URL completa (opcional) |
| `CANAL_FEATURE_SIMULADOR_ECONOMIA` | Sim | `true` or `false` Feature Toggle do Simulador de Economia |

Deploy OpenShift: [`openshift/README.md`](openshift/README.md)

## Deploy OpenShift (Serverless)

Namespace: **`canal-digital-<dev>-<stg>-<prod>`** (mesmo dos Canais Digitais).

Guia completo: [`openshift/README.md`](openshift/README.md)

```bash
mvn clean package -DskipTests
oc project canal-digital-old
oc apply -f openshift/ksvc.yaml   # Secret + Knative Service
oc start-build canal-digital-quarkus-git --follow
```

Validação:

```bash
curl $(oc get ksvc canal-digital-quarkus-git -o jsonpath='{.status.url}')/api/info
```

Esperado: `"buildId":"2026-06-05-sqlserver-openshift-v1"` e `"clientesCadastrados":2`

## Contas de demonstração

| Usuário | Senha |
|---------|-------|
| `maria@email.com` | `123456` |
| `joao@email.com` | `123456` |

CPF: `52998224725` ou `39053344705`

## API (resumo)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/info` | Versão e status do banco |
| POST | `/api/auth/login` | Login |
| GET | `/api/dashboard` | Dashboard |
| GET | `/api/faturas` | Faturas |
| GET | `/api/consumo` | Consumo |

Documentação interativa: `/api/docs`

## Stack

- **Backend:** Quarkus 3.17, REST, Panache, SQL Server
- **Frontend:** HTML/CSS/JS + Chart.js
- **Deploy:** OpenShift Serverless (Knative), Dockerfile JVM

## Build

```bash
mvn clean package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t canal-digital-quarkus:latest .
```
