package cmri.utils.dao;

import cmri.utils.configuration.ConfigManager;

import java.sql.*;

/**
 * Created by zhuyin on 7/6/14.
 */
public class JdbcDAO {
    String url = ConfigManager.get("db.url", ConfigManager.get("spring.datasource.url"));
    String username=ConfigManager.get("db.username", ConfigManager.get("spring.datasource.username"));
    String password=ConfigManager.get("db.password", ConfigManager.get("spring.datasource.password"));
    Connection conn = null;

    static {
        try {
            // registered jdbc driver
            Class.forName(ConfigManager.get("db.driverClass", ConfigManager.get("spring.datasource.driverClassName", "com.mysql.jdbc.Driver")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String getUrl() {
        return url;
    }

    /**
     * @param  url a database url of the form <code>jdbc:<em>subprotocol</em>:<em>subname</em></code>
     * <br> MySQL的JDBC URL格式：
            jdbc:mysql//[hostname][:port]/[dbname][?param1=value1][&param2=value2]….
       <br> for example：jdbc:mysql://localhost:3306/sample_db?user=root&password=your_password
     */
    public JdbcDAO setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public JdbcDAO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public JdbcDAO setPassword(String password) {
        this.password = password;
        return this;
    }
    /**
     * get db connection
     *
     * @return db connection
     */
    public Connection getConn() throws SQLException {
        if (this.conn != null)
            return this.conn;
        this.conn = DriverManager.getConnection(this.url, this.username, this.password);
        return this.conn;
    }

    /**
     * 行查询数据库的SQL语句，返回一个结果集（ResultSet）对象
     * @throws SQLException
     */
    public Object executeQuery(String sql, ResultSetFetcher fetcher) throws SQLException {
        try (Statement stat = this.getConn().createStatement()) {
            try(ResultSet rs = stat.executeQuery(sql)) {
                return fetcher.fetch(rs);
            }
        }
    }

    /**
     * 用于执行INSERT、UPDATE或DELETE语句以及SQL DDL语句，如：CREATE TABLE和DROP TABLE等
     * @throws SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Statement stat = this.getConn().createStatement()) {
            return stat.executeUpdate(sql);
        }
    }

    public interface ResultSetFetcher{
        Object fetch(ResultSet rs) throws SQLException;
    }
}
