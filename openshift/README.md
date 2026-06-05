# OpenShift — Canal Digital Quarkus + SQL Server

## 1. Banco SQL Server

Crie o database no SQL Server (uma vez):

```sql
CREATE DATABASE canal_digital;
```

Anote o **hostname do Service** do SQL Server no namespace `canal-digital-old`
(ex.: `sqlserver`, `mssql`, `seu-sql-service`).

## 2. Secret com credenciais

Edite `ksvc.yaml` (bloco Secret) com host, usuário e senha reais, depois:

```bash
oc project canal-digital-old
oc apply -f openshift/ksvc.yaml
```

Ou crie o secret manualmente (recomendado — não commitar senha):

```bash
oc create secret generic canal-digital-db \
  --from-literal=DB_HOST=sqlserver \
  --from-literal=DB_PORT=1433 \
  --from-literal=DB_NAME=canal_digital \
  --from-literal=DB_USERNAME=sa \
  --from-literal=DB_PASSWORD='SUA_SENHA' \
  --from-literal=DB_ENCRYPT=true \
  --from-literal=DB_TRUST_CERT=true \
  -n canal-digital-old
```

## 3. Build e imagem

```bash
mvn clean package -DskipTests
# Start Build no BuildConfig existente, ou:
oc start-build canal-digital-quarkus-git --from-dir=. --follow -n canal-digital-old
```

A annotation `image.openshift.io/triggers` no `ksvc` cria **revision nova** quando `:latest` atualizar.

## 4. Forçar revision (se necessário)

```bash
oc set env ksvc/canal-digital-quarkus-git DEPLOY_VERSION=$(date +%s) -n canal-digital-old
```

## 5. Validar

```bash
URL=$(oc get ksvc canal-digital-quarkus-git -n canal-digital-old -o jsonpath='{.status.url}')
curl -s "$URL/api/info"
curl -s -X POST "$URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"identificador":"maria@email.com","senha":"123456"}'
```

Resposta esperada em `/api/info`:

```json
{"buildId":"2026-06-05-sqlserver-openshift-v1","clientesCadastrados":2,...}
```

## Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `QUARKUS_PROFILE` | Deve ser `prod` no OpenShift |
| `DB_HOST` | Host do SQL Server |
| `DB_PORT` | Porta (padrão 1433) |
| `DB_NAME` | Nome do database |
| `DB_USERNAME` | Usuário |
| `DB_PASSWORD` | Senha |
| `QUARKUS_DATASOURCE_JDBC_URL` | (opcional) URL JDBC completa |

## Desenvolvimento local

```bash
mvn quarkus:dev
```

Usa H2 em memória (perfil `dev` automático no `quarkus:dev`).

Para testar com SQL Server local ou port-forward:

```bash
export QUARKUS_PROFILE=dev
export DB_HOST=localhost
export DB_PORT=1433
export DB_NAME=canal_digital
export DB_USERNAME=sa
export DB_PASSWORD=...
mvn quarkus:dev
```

### Testes de integração (`mvn verify`)

Perfil `test` — SQL Server via Dev Services (Docker) ou desabilite devservices e use `DB_*`.
