package br.com.energia.canal;

import io.quarkus.runtime.Quarkus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

/**
 * Cria o database SQL Server (conectando em {@code master}) antes do Quarkus/Hibernate iniciar.
 */
public class Main {

    private static final Pattern SAFE_DB_NAME = Pattern.compile("^[A-Za-z0-9_]+$");

    public static void main(String[] args) {
        ensureDatabaseExists();
        Quarkus.run(args);
    }

    static void ensureDatabaseExists() {
        if (!Boolean.parseBoolean(env("CANAL_DB_BOOTSTRAP", "true"))) {
            return;
        }
        if ("test".equalsIgnoreCase(env("QUARKUS_PROFILE", ""))) {
            return;
        }

        String dbName = env("DB_NAME", "canal_digital");
        if (!SAFE_DB_NAME.matcher(dbName).matches()) {
            throw new IllegalStateException("DB_NAME inválido para bootstrap: " + dbName);
        }

        String host = env("DB_HOST", "localhost");
        String port = env("DB_PORT", "1433");
        String user = env("DB_USERNAME", "sa");
        String password = env("DB_PASSWORD", "");
        String encrypt = env("DB_ENCRYPT", "true");
        String trustCert = env("DB_TRUST_CERT", "true");

        if (password.isBlank()) {
            System.err.println("ERRO: DB_PASSWORD não definido. Configure o Secret canal-digital-db ou variáveis de ambiente.");
            System.exit(1);
        }

        String masterUrl = "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=master;encrypt=" + encrypt
                + ";trustServerCertificate=" + trustCert
                + ";loginTimeout=15";

        System.out.println("Bootstrap SQL Server: verificando database '" + dbName + "' em " + host + ":" + port + " ...");

        try (Connection connection = DriverManager.getConnection(masterUrl, user, password);
                Statement statement = connection.createStatement()) {
            statement.execute(
                    "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'" + dbName + "') "
                            + "CREATE DATABASE [" + dbName + "]");
            System.out.println("Bootstrap SQL Server: database '" + dbName + "' OK.");
        } catch (SQLException e) {
            System.err.println("ERRO ao conectar no SQL Server (master) com usuário '" + user + "':");
            System.err.println("  " + e.getMessage());
            System.err.println("Verifique: DB_HOST, DB_USERNAME, DB_PASSWORD no Secret canal-digital-db");
            System.err.println("  e se a senha coincide com a do SQL Server (login 'sa').");
            System.exit(1);
        }
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
