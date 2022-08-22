package com.bobocode.pool;

import lombok.experimental.Delegate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PooledDataSource implements DataSource {

    private static final int DEF_POOL_SIZE = 10;

    private final Queue<ConnectionProxy> connections = new ConcurrentLinkedQueue<>();
    @Delegate(excludes = Exclude.class)
    private final DataSource dataSource;

    public PooledDataSource(DataSource dataSource, int poolSize) {
        this.dataSource = dataSource;
        init(poolSize);
    }
    public PooledDataSource(DataSource dataSource) {
        this(dataSource, DEF_POOL_SIZE);
    }

    private void init(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            try {
                connections.add(new ConnectionProxy(dataSource.getConnection(), connections));
            } catch (SQLException e) {
                throw new RuntimeException("Error getting new connection", e);
            }
        }
    }

    @Override
    public Connection getConnection() {
        return connections.poll();
    }

    private interface Exclude {
        Connection getConnection();
    }
}