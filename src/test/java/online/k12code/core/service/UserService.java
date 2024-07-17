package online.k12code.core.service;

import online.k12code.core.annotation.MyTransaction;
import online.k12code.core.datasource.DataSourceConnectHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Carl
 * @description:
 * @since 1.0.0
 */
@Service
public class UserService {

    @Autowired
    private DataSourceConnectHolder holder;

    @MyTransaction
    public void addUser(String username,String password){
        String sql = "INSERT INTO user (name, password) VALUES (?, ?)";
        Connection connection = holder.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 异常复现
        int aa = 1 / 0;

    }
}
