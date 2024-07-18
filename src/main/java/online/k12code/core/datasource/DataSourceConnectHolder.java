package online.k12code.core.datasource;

import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Carl
 * @since 1.0.0
 */
@Component
public class DataSourceConnectHolder {


    private final DataSource dataSource;

    public DataSourceConnectHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    ThreadLocal<Connection> resource = new NamedThreadLocal<Connection>("Transactional resources");

    public Connection getConnection() {
        Connection connection = resource.get();
        if (connection != null) {
            return connection;
        }
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resource.set(connection);
        return connection;
    }
    public void cleanHolder(){
        Connection connection = resource.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            resource.remove();
        }
    }
}
