package ejb.beans;

import dao.JpaAccountDao;
import dao.JpaPaymentDao;
import dao.JpaScheduledPaymentDao;
import entities.Account;
import entities.Payment;
import entities.ScheduledPayment;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
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
    private JpaPaymentDao paymentDao;
    
    @EJB
    private JpaScheduledPaymentDao scheduledPaymentDao;
    
    @EJB
    private JpaAccountDao accountDao;
    
    public PaymentStorageServiceBean() {
        
    }
    
    @TransactionAttribute(REQUIRED)
    public synchronized void makePayment(String type, Account origin, 
            Account recipient, String currency, float amount, Date scheduledDate) 
            throws SQLException {
        
        String status = null;
        Date timestamp = timestampService.getTimestamp();
        Date today = new Date();
        
        if(type.equals("payment")){
            if(scheduledDate.after(today)){
                schedulePayment(origin, recipient, currency, 
                        amount, scheduledDate, "once");
                return;
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
    
    @TransactionAttribute(REQUIRED)
    public synchronized void schedulePayment(Account origin, Account recipient, 
            String currency, float amount, Date scheduledDate, String frequency) 
            throws SQLException {
        Date nextScheduledDate = null;
        
        if(!scheduledDate.after(getToday())) {
            makePayment("payment", origin, recipient, currency, amount, getToday());
            nextScheduledDate = calculateNextScheduledDate(frequency);
        }
        
        scheduledPaymentDao.insertScheduledPayment(origin, recipient, currency,
                amount, nextScheduledDate, scheduledDate, frequency);
    }
    
    public synchronized List<Payment> getTransactions(Account origin) {
        return paymentDao.getTransactionsByAccount(origin);
    }
    
    public synchronized List<ScheduledPayment> getRecurringPayments(Account origin) {
        return scheduledPaymentDao.getRecurringPayments(origin);
    }
    
    public synchronized List<ScheduledPayment> getAllRecurringPayments() {
        return scheduledPaymentDao.getAllRecurringPayments();
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
    
    @TransactionAttribute(REQUIRED)
    public void makeScheduledPayment(ScheduledPayment item) throws SQLException{
        //Insert all payments into payments DB
        makePayment("payment", item.getOrigin(), item.getRecipient(), item.getCurrency(),
                item.getAmount(), new Date()); 

        updateNextScheduledDate(item);
    }
    
    public void updateNextScheduledDate(ScheduledPayment payment){
        Date nextScheduledDate = calculateNextScheduledDate(payment.getFrequency());

        if(nextScheduledDate == null){
            removeScheduledPayment(payment.getId());
            return;
        }
            
        payment.setNextScheduledDate(nextScheduledDate);
    }
    
    public Date calculateNextScheduledDate(String frequency) {
        Date nextScheduledDate = null;

        switch (frequency) {
        case "daily": 
            nextScheduledDate = addDaysToDate(1);
            break;
        case "weekly": 
            nextScheduledDate = addDaysToDate(7);
            break;
        case "monthly": 
            nextScheduledDate = addMonthsToDate(1);
            break;
        default: 
            return nextScheduledDate;
        }
        
        return nextScheduledDate;
    }
    
    public Date addDaysToDate(int noOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, noOfDays);
        
        return cal.getTime();
    }
    
    public Date addMonthsToDate(int noOfMonths) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, noOfMonths);
        
        return cal.getTime();
    }
    
    private Date getToday() {
        return new Date();
    }
    
    public void removeScheduledPayment(long id){
        scheduledPaymentDao.remove(id);
    }
    
    //Check for scheduled payments every day at 8am server time
    @Schedule(second="0", minute="0",hour="8", persistent=false)
    public void checkForScheduledPayments() throws SQLException{
        Date today = getToday();
        
        //Get all payments for today
        List payments = scheduledPaymentDao.getScheduledPaymentsByDate(today);
        
        //Iterate over each payment and make the payment
        for(Iterator<ScheduledPayment> i = payments.iterator(); i.hasNext(); ) {
            ScheduledPayment item = i.next();
            makeScheduledPayment(item);
        }
    }
}