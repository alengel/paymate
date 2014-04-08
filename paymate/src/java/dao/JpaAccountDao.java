package dao;

import entities.Account;
import entities.AccountGroup;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author 119848
 */
@Stateless
@LocalBean
public class JpaAccountDao implements AccountDao {

    @PersistenceContext(unitName = "paymatePU")
    private EntityManager em;

    public JpaAccountDao() {
    }

    @Override
    public List<Account> getAccounts() throws SQLException {
        TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a", Account.class);
        return query.getResultList();
    }

    @Override
    public Account getAccount(String email) throws SQLException {
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.email = :email", Account.class);
            return query.setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public AccountGroup getAccountRole(String email) throws SQLException {
        TypedQuery<AccountGroup> query = em.createQuery(
                "SELECT ag FROM AccountGroup ag WHERE ag.email = :email", AccountGroup.class);
        return query.setParameter("email", email).getSingleResult();
    }

    @Override
    public void insertAccount(String email, String hashedPassword,
            String defaultRole, String currency, float balance) {

        Account account = new Account(email, hashedPassword, currency, balance, defaultRole, new Date());
        AccountGroup accountGroup = new AccountGroup(email, defaultRole);

        em.persist(account);
        em.persist(accountGroup);
        em.flush();
    }

    @Override
    public void updateBalance(Account account, float balance) {
        System.out.print(account);
        System.out.print(balance);

        em.setProperty("balance", balance);
    }
}
