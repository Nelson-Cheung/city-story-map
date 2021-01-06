package JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author NelsonCheung
 */
public class JdbcUtils {
    //定义数据库用户名
    private String USERNAME;
    //定义数据库密码
    private String PASSWORD;
    //定义数据库驱动信息
    private String DRIVER;
    //定义数据库地址
    private String LOCATION;
    //定义数据库连接
    private Connection connection = null;
    //sql执行语句
    private PreparedStatement pstmt = null;
    //查询结果集合
    private ResultSet resultSet;
    //列信息集合
    private ResultSetMetaData metaData;

    public JdbcUtils(String username, String password, String driver, String location) {
        USERNAME = username;
        PASSWORD = password;
        DRIVER = driver;
        LOCATION = location;

        try {
            //注册JDBC驱动
            Class.forName(DRIVER);
            System.out.println("驱动注册成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            //获得对MySQL数据库连接
            connection = DriverManager.getConnection(LOCATION,USERNAME,PASSWORD);
            System.out.println("连接数据库成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * 释放连接
     */
    private void releaseConnection() {
        try {
            if (connection != null ) {
                connection.close();
            }
            if (pstmt != null ) {
                pstmt.close();
            }
            if( resultSet != null ) {
                resultSet.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新一条数据到数据库中
     * @param sql sql语句
     * @param params 填充参数
     * @return 正常返回true，否则返回false
     */
    public int update(String sql, List<Object> params ) {
        int ans = 0;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); ++i) {
                    pstmt.setObject(i + 1, params.get(i));
                }
            }
            ans = pstmt.executeUpdate();
            releaseConnection();
            System.out.println("执行sql语句: " + sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ans;
    }

    /**
     * 执行一条sql更新语句
     * @param sql sql语句
     * @return 执行成功返回true，否则返回false
     */
    public int update(String sql) {
        int ans = 0;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            ans = pstmt.executeUpdate();
            releaseConnection();
            System.out.println("执行sql语句" + sql);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ans;
    }

    /**
     * 查询数据库
     * @param sql 查询sql语句
     * @param params 填充数据
     * @return 查询结果
     */
    public List<HashMap<String, Object>> find(String sql, List<Object> params) {
        int totalColumns;
        // 查询结果
        List<HashMap<String, Object>> answerSet = new ArrayList<HashMap<String, Object>>();

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            if (params != null ) {
                for (int i = 0; i < params.size(); ++i ) {
                    pstmt.setObject(i+1, params.get(i));
                }
            }
            resultSet = pstmt.executeQuery();
            metaData = resultSet.getMetaData();
            totalColumns = metaData.getColumnCount();

            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= totalColumns; ++i) {
                    map.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                answerSet.add(map);
            }
            releaseConnection();
            System.out.println("执行sql语句"+sql);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return answerSet;
    }

    /**
     * 执行查询sql语句
     * @param sql 查询数据库sql语句
     * @return 查询结果
     */
    public List<HashMap<String, Object>> find(String sql) {
        List<HashMap<String, Object>> answerSet = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            resultSet = pstmt.executeQuery(sql);
            metaData = resultSet.getMetaData();
            int totalColumns = metaData.getColumnCount();
            answerSet = new ArrayList<HashMap<String, Object>>();

            while(resultSet.next()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                for(int i = 1; i <= totalColumns; ++i) {
                    map.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                answerSet.add(map);
            }

            releaseConnection();
            System.out.println("执行sql语句"+sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return answerSet;
    }
}
