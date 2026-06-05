# OpenShift — Canal Digital Quarkus + SQL Server (S2I + Serverless)

Toda a configuração de banco vem de **variáveis de ambiente**.  
Referência completa: [`env.example`](env.example)

## Variáveis obrigatórias (prod / OpenShift)

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `QUARKUS_PROFILE` | Perfil Quarkus | `prod` |
| `DB_HOST` | Nome do **Service** do SQL Server no namespace | `mssql` |
| `DB_PORT` | Porta | `1433` |
| `DB_NAME` | Database | `canal_digital` |
| `DB_USERNAME` | Usuário | `sa` |
| `DB_PASSWORD` | Senha | *(secret)* |

## Variáveis opcionais

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_ENCRYPT` | `true` | Criptografia JDBC |
| `DB_TRUST_CERT` | `true` | Trust server certificate |
| `DB_LOG_SQL` | `false` | Log SQL Hibernate |
| `CANAL_DEMO_SEED_ENABLED` | `true` | Carrega dados demo se tabelas vazias |
| `QUARKUS_DATASOURCE_JDBC_URL` | *(montada de DB_*)* | URL JDBC completa (sobrescreve montagem) |

Quarkus também aceita `QUARKUS_DATASOURCE_USERNAME` e `QUARKUS_DATASOURCE_PASSWORD` (prioridade sobre `DB_*`).

---

## Opção A — S2I + Serverless (console)

1. **Serverless → Services → canal-digital-quarkus-git → Environment**
2. Adicione cada variável de `env.example` (valores reais do seu SQL Server)
3. `QUARKUS_PROFILE` = `prod`
4. Salve → nova **Revision** é criada automaticamente

Descubra o host do SQL Server:

```bash
oc get svc -n canal-digital-old
# DB_HOST = coluna NAME do Service do SQL Server (ex.: mssql)
```

---

## Opção B — Secret + ksvc.yaml

```bash
oc project canal-digital-old

# Edite secret.example.yaml com valores reais, depois:
oc apply -f openshift/secret.example.yaml

# Aplique o Knative Service (referencia o Secret):
oc apply -f openshift/ksvc.yaml
```

Ou via arquivo de env:

```bash
cp openshift/env.example openshift/env.local   # edite env.local
oc create secret generic canal-digital-db --from-env-file=openshift/env.local --dry-run=client -o yaml | oc apply -f -
oc apply -f openshift/ksvc.yaml
```

---

## Build S2I

```bash
oc start-build canal-digital-quarkus-git --follow -n canal-digital-old
```

A annotation `image.openshift.io/triggers` no `ksvc` cria revision nova quando `:latest` atualizar.

---

## Validar

```bash
URL=$(oc get ksvc canal-digital-quarkus-git -n canal-digital-old -o jsonpath='{.status.url}')
curl -s "$URL/api/info"
```

Esperado: `"clientesCadastrados":2` (se seed habilitado e banco vazio na 1ª subida).

---

## Desenvolvimento local

```bash
export DB_HOST=localhost DB_PORT=1433 DB_NAME=canal_digital DB_USERNAME=sa DB_PASSWORD='...'
mvn quarkus:dev
```

Ou port-forward:

```bash
oc port-forward svc/<nome-service-sql> 1433:1433 -n canal-digital-old
export DB_HOST=localhost DB_PASSWORD='...'
mvn quarkus:dev
```

## Testes (`mvn verify`)

Perfil `test` — SQL Server via Dev Services (Docker).
