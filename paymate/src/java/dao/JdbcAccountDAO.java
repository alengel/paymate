package dao;

import entities.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author 119848
 */

@Stateless
public class JdbcAccountDAO implements AccountDAO {
    
    @PersistenceContext(unitName = "paymatePU")
    private EntityManager em;
    
    public JdbcAccountDAO() {
    }

//    @PersistenceContext(unitName = "paymatePU")
//    public void setEntityManager(EntityManager entityManager) {
//        this.em = entityManager;
//    }
    
    @Override
    public List<Account> getAccounts() throws SQLException{
        
//        Connection conn = null;
//        PreparedStatement pstmt;
//        
//        try {
//            conn = JdbcFactory.createConnection();
//            pstmt = conn.prepareStatement("SELECT a FROM Account a");
//            pstmt.executeUpdate();
//            
//            conn.commit();
//            pstmt.close();
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(JdbcAccountDAO.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (conn != null) conn.close();
//        }
//        
//        if(em == null){
//            System.out.print(em + " is very null");
//        }
        TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a", Account.class);
        return query.getResultList();
//        return null;
    }
}
