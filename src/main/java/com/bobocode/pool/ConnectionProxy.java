package com.bobocode.pool;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.sql.Connection;
import java.util.Queue;

@AllArgsConstructor
public class ConnectionProxy implements Connection {

    @Delegate(excludes = Exclude.class)
    private final Connection connection;
    private final Queue<ConnectionProxy> connections;

    @Override
    public void close() {
        connections.add(this);
    }

    private interface Exclude {
        void close();
    }
}
