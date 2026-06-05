-- Execute no SQL Server ANTES da primeira subida da aplicação.
-- Ex.: oc rsh <pod-sql> -- /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P '<senha>' -C -i create-database.sql

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'canal_digital')
BEGIN
    CREATE DATABASE canal_digital;
END
GO

USE canal_digital;
GO

-- Garante permissão do login sa (ajuste se usar outro usuário)
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = N'sa')
BEGIN
    CREATE USER [sa] FOR LOGIN [sa];
END
GO

ALTER ROLE db_owner ADD MEMBER [sa];
GO
