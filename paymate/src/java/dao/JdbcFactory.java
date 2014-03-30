package dao;

/**
 *
 * @author 119848
 */

public class JdbcFactory extends DAOFactory{
    public static final String DRIVER = "org.apache.derby.jdbc.ClientDriver";
    public static final String DBURL= "jdbc:derby://localhost:1527/PayMateDB";
    
//    public static Connection createConnection() throws SQLException{
//        //Based on http://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html
//        Connection conn;
//        Properties connectionProps = new Properties();
//        connectionProps.put("username", "username");
//        connectionProps.put("password", "password");
//
//        conn = DriverManager.getConnection(DBURL, connectionProps);
//        
//        System.out.println("Connected to database");
//        return conn;
//    }
    
    @Override
    public AccountDao getAccountDAO() {
        return new JdbcAccountDao();
    }
}
