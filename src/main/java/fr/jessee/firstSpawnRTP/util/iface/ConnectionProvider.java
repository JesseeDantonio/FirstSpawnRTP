package fr.jessee.firstSpawnRTP.util.iface;

import java.sql.Connection;

public interface Storage {
    void connect() throws Exception;
    void disconnect() throws Exception;
    Connection getConnection();
}
