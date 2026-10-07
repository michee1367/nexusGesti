package com.nexusgesti.common.tenancy;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DelegatingDataSource;

public class TenantAwareDataSource extends DelegatingDataSource {

    public static final String SETTING = "app.tenant_id";

    private static final String APPLY = "SELECT set_config('" + SETTING + "', ?, false)";
    private static final String CLEAR = "SELECT set_config('" + SETTING + "', '', false)";

    public TenantAwareDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrap(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrap(super.getConnection(username, password));
    }

    private Connection wrap(Connection connection) {
        try {
            TenantContext.get().ifPresentOrElse(
                    etablissementId -> apply(connection, etablissementId.toString()),
                    () -> clear(connection));
            return newResetOnClose(connection);
        } catch (RuntimeException e) {
            closeQuietly(connection);
            throw e;
        }
    }

    private void apply(Connection connection, String value) {
        try (PreparedStatement ps = connection.prepareStatement(APPLY)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
            }
        } catch (SQLException e) {
            throw new TenantSettingException("Failed to apply " + SETTING + "=" + value, e);
        }
    }

    private void clear(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(CLEAR)) {
            ps.execute();
        } catch (SQLException e) {
            throw new TenantSettingException("Failed to clear " + SETTING, e);
        }
    }

    private Connection newResetOnClose(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                connection.getClass().getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if (!"close".equals(method.getName())) {
                        return invoke(connection, method, args);
                    }
                    try {
                        clear(connection);
                    } finally {
                        connection.close();
                    }
                    return null;
                });
    }

    private Object invoke(Connection connection, Method method, Object[] args)
            throws Throwable {
        try {
            return method.invoke(connection, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}