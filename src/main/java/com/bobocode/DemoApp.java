package com.bobocode;

import com.bobocode.pool.PooledDataSource;
import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class DemoApp {
    @SneakyThrows
    public static void main(String[] args) {
        var dataSource = initializePooledDataSource();
        var total = 0.0;
        var start = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            try (var connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                try (var statement = connection.createStatement()) {
                    var rs = statement.executeQuery("select random() from products");
                    rs.next();
                    total += rs.getDouble(1);
                    System.out.println("i = " + i + "; total = " + total);
                }
                connection.rollback();
            }
        }
        System.out.println((System.nanoTime() - start) / 1000_000 + " ms");
        System.out.println(total);


    }

    private static DataSource initializeDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("admin");
        return dataSource;
    }

    private static DataSource initializePooledDataSource() {
        DataSource dataSource = new PooledDataSource(initializeDataSource());
        return dataSource;
    }
}
