package ejb.beans;

import services.CurrencyService;
import dao.jpa.JpaAccountDao;
import ejb.interfaces.AccountStorageService;
import entities.Account;
import entities.AccountGroup;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import jsf.shared.beans.UtilityBean;

/**
 *
 * @author 119848
 */
@Stateless
@DeclareRoles({"admin"})
public class AccountStorageServiceBean implements AccountStorageService {

    private final UtilityBean utility;

    @EJB
    private JpaAccountDao accountDao;

    public AccountStorageServiceBean() {
        utility = new UtilityBean();
    }

    //Return a boolean value if the email exists in the Account table
    @Override
    public synchronized Boolean checkAccountExists(String email) throws SQLException {
        return accountDao.getAccount(email) != null;
    }

    //Get an account from the email that was passed in
    @Override
    public synchronized Account getAccount(String email) throws SQLException {
        return accountDao.getAccount(email);
    }

    //Get all accounts that are in the Account table
    @RolesAllowed("admin")
    @Override
    public synchronized List<Account> getAccounts() throws SQLException {
        return accountDao.getAccounts();
    }

    //Get the user role from the email that was passed in
    @Override
    public synchronized AccountGroup getAccountRole(String email) throws SQLException {
        return accountDao.getAccountRole(email);
    }

    //Work out the selected role's default attributes before inserting the 
    //account into the Account table
    @TransactionAttribute(REQUIRED)
    @Override
    public synchronized void insertAccount(String email, String password, String currency) {

        float balance;
        String defaultRole;
        String hashedPassword;

        hashedPassword = hashPassword(password);

        if (utility.getLoggedInUser() != null) {
            //Admin user defaults
            defaultRole = "admin";
            balance = 0;
        } else if (currency.equals("FB")) {
            //Facebook user defaults
            currency = "GBP";
            defaultRole = "facebook_user";
            balance = getBalanceInChosenCurrency(currency);
        } else {
            //Regular user defaults
            defaultRole = "user";
            balance = getBalanceInChosenCurrency(currency);
        }

        //Insert the values into the Account table 
        accountDao.insertAccount(email, hashedPassword, defaultRole, currency, balance);
    }

    //Update the last logged in column for the currently logged in user
    @Override
    public synchronized void updateLastLoginDate(String email) throws SQLException {
        accountDao.getAccount(email).setLastLoggedIn(new Date());
    }

    private String hashPassword(String password) {
        try {
            //Password Hash Code from Lab Class - Week 8 by George Parisis
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwd = password;
            md.update(passwd.getBytes("UTF-8"));
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String paswdToStoreInDB = bigInt.toString(16);

            return paswdToStoreInDB;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    //Pass GBP and 1000000 as default parameters to currency service bean to 
    //retrieve the default currency amount from the user's chosen currency
    private float getBalanceInChosenCurrency(String currency) {
        float gbpBalance = 1000000;
        String defaultCurrency = "GBP";

        if (currency.equals(defaultCurrency)) {
            return gbpBalance;
        }

        return CurrencyService.getConvertedAmount(defaultCurrency, currency, gbpBalance);
    }
}
