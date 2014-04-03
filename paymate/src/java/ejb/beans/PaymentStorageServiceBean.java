package ejb.beans;

import dao.JdbcAccountDao;
import dao.JdbcPaymentDao;
import entities.Account;
import entities.Payment;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;

/**
 *
 * @author 119848
 */

@Stateless
public class PaymentStorageServiceBean {

    @EJB
    private TimestampServiceBean timestampService;

    @EJB
    private JdbcPaymentDao paymentDao;
    
    @EJB
    private JdbcAccountDao accountDao;
    
    public PaymentStorageServiceBean() {
        
    }
    
    @TransactionAttribute(REQUIRED)
    public synchronized void makePayment(String type, Account origin, 
            Account recipient, String currency, float amount, Date scheduledDate) 
            throws SQLException {
        
        String status;
        Date timestamp = timestampService.getTimestamp();
        Date today = new Date();
        
        if(type.equals("payment")){
            if(scheduledDate.after(today)){
                status = "scheduled";
            } else {
                status = "completed";
                calculateBalances(origin, recipient, currency, amount);
            }
        } else {
            status = "pending";
        }
        
        paymentDao.insertTransaction(timestamp, type, origin, recipient, currency, 
                amount, scheduledDate, status);
    }
    
    public synchronized List<Payment> getTransactions(Account origin) {
        return paymentDao.getTransactionsByAccount(origin);
    }
    
    public synchronized List<Payment> getAllTransactions() {
        return paymentDao.getAllTransactions();
    }
    
    public synchronized Payment getTransaction(long id){
        return paymentDao.getTransaction(id);
    }
    
    public synchronized void updateStatus(long id, String status){
        paymentDao.updateStatus(id, status);
    }
    
    @TransactionAttribute(REQUIRED)
    public synchronized void updateBalances(Payment payment) throws SQLException {
        
        calculateBalances(payment.getOrigin(), payment.getRecipient(),
            payment.getCurrency(), payment.getAmount());

        updateStatus(payment.getId(), "accepted");
    }
    
    public synchronized void addAmount(String recipient, float amount) 
            throws SQLException {
        
        Account recipientAccount = accountDao.getAccount(recipient);
        
        float balance = recipientAccount.getBalance();
        float newBalance = balance + amount;

        recipientAccount.setBalance(newBalance);
    }
    
    public synchronized void deductAmount(String originEmail, float amount) 
            throws SQLException {
        
        Account originAccount = accountDao.getAccount(originEmail);
        
        float balance = originAccount.getBalance();
        float newBalance = balance - amount;

        originAccount.setBalance(newBalance);
    }
    
    private float convertAmountIntoLocalCurrency(String email, String currency, 
            float amount) throws SQLException {
        
        String localCurrency = accountDao.getAccount(email).getCurrency();
        
        if(localCurrency.equals(currency)){
            return amount;
        } else {
            return CurrencyServiceBean.getConvertedAmount(currency,
                    localCurrency, amount);
        }
    }
    
    public void calculateBalances(Account origin, Account recipient,
            String currency, float amount) throws SQLException{
        
        float convertedOriginAmount = convertAmountIntoLocalCurrency(
                origin.getEmail(), 
                currency, 
                amount);
        float convertedRecipientAmount = convertAmountIntoLocalCurrency(
                recipient.getEmail(), 
                currency, 
                amount);
        
        deductAmount(origin.getEmail(), convertedOriginAmount);
        addAmount(recipient.getEmail(), convertedRecipientAmount);
    }
    
    public String[] getAvailableCurrencies(){
        return CurrencyServiceBean.getAvailableCurrencies();
    }
}