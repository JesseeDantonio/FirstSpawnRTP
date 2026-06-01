package fr.jessee.firstSpawnRTP.util.iface;

import java.sql.Connection;

public interface ConnectionProvider {
    void connect() throws Exception;
    void disconnect() throws Exception;
    Connection getConnection();
}
