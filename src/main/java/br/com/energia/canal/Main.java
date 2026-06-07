package br.com.energia.canal;

import io.quarkus.runtime.Quarkus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Cria o database SQL Server (conectando em {@code master}) antes do Quarkus/Hibernate iniciar.
 */
public class Main {

    private static final Pattern SAFE_DB_NAME = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final int WAIT_ONLINE_SECONDS = 90;

    private static final String SQL_DATABASE_EXISTS =
            "SELECT 1 FROM sys.databases WHERE name = ?";

    private static final String SQL_DATABASE_STATE =
            "SELECT state_desc FROM sys.databases WHERE name = ?";

    private static final String SQL_CREATE_DATABASE =
            "DECLARE @name sysname = ?;"
                    + " DECLARE @sql nvarchar(200) = N'CREATE DATABASE ' + QUOTENAME(@name);"
                    + " EXEC(@sql);";

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

        String masterUrl = jdbcUrl(host, port, "master", encrypt, trustCert, 30, 30_000);
        String appUrl = jdbcUrl(host, port, dbName, encrypt, trustCert, 60, 120_000);

        System.out.println("Bootstrap SQL Server: verificando database '" + dbName + "' em " + host + ":" + port + " ...");

        try (Connection connection = DriverManager.getConnection(masterUrl, user, password)) {
            createDatabaseIfNotExists(connection, dbName);
            waitUntilOnline(connection, dbName);
        } catch (SQLException e) {
            failBootstrap(user, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("ERRO: bootstrap interrompido ao aguardar database ONLINE.");
            System.exit(1);
        }

        try (Connection connection = DriverManager.getConnection(appUrl, user, password);
                PreparedStatement statement = connection.prepareStatement("SELECT 1");
                ResultSet rs = statement.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException("Conexão de teste em '" + dbName + "' não retornou resultado.");
            }
            System.out.println("Bootstrap SQL Server: conexão em '" + dbName + "' OK.");
        } catch (SQLException e) {
            failBootstrap(user, e);
        }
    }

    private static void createDatabaseIfNotExists(Connection connection, String dbName) throws SQLException {
        if (databaseExists(connection, dbName)) {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement(SQL_CREATE_DATABASE)) {
            statement.setString(1, dbName);
            statement.execute();
        }
    }

    private static boolean databaseExists(Connection connection, String dbName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DATABASE_EXISTS)) {
            statement.setString(1, dbName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void waitUntilOnline(Connection connection, String dbName)
            throws SQLException, InterruptedException {
        for (int attempt = 1; attempt <= WAIT_ONLINE_SECONDS; attempt++) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_DATABASE_STATE)) {
                statement.setString(1, dbName);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next() && "ONLINE".equalsIgnoreCase(rs.getString(1))) {
                        System.out.println("Bootstrap SQL Server: database '" + dbName + "' ONLINE.");
                        return;
                    }
                }
            }
            Thread.sleep(1000);
        }
        throw new SQLException("Database '" + dbName + "' não ficou ONLINE em " + WAIT_ONLINE_SECONDS + "s.");
    }

    private static String jdbcUrl(
            String host, String port, String database, String encrypt, String trustCert, int loginTimeout, int socketTimeout) {
        return "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=" + database
                + ";encrypt=" + encrypt
                + ";trustServerCertificate=" + trustCert
                + ";loginTimeout=" + loginTimeout
                + ";socketTimeout=" + socketTimeout;
    }

    private static void failBootstrap(String user, SQLException e) {
        System.err.println("ERRO ao conectar no SQL Server com usuário '" + user + "':");
        System.err.println("  " + e.getMessage());
        System.err.println("Verifique: DB_HOST, DB_USERNAME, DB_PASSWORD no Secret canal-digital-db");
        System.exit(1);
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
