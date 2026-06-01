package fr.jessee.firstSpawnRTP.feature;

import fr.jessee.firstSpawnRTP.util.iface.ConnectionProvider;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteDataSource implements ConnectionProvider {
    private final File dbFile;
    private Connection connection;

    public SQLiteDataSource(File dbFile) {
        this.dbFile = dbFile;
    }

    @Override
    public void connect() throws Exception {
        if (connection == null) {
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
        }
    }

    @Override
    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
