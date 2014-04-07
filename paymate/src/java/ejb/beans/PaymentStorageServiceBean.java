package ejb.beans;

import dao.JpaAccountDao;
import dao.JpaPaymentDao;
import dao.JpaScheduledPaymentDao;
import ejb.interfaces.PaymentStorageService;
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
public class PaymentStorageServiceBean implements PaymentStorageService {

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
    @Override
    public synchronized void makePayment(String type, Account origin, 
            Account recipient, String currency, float amount, Date scheduledDate) 
            throws SQLException {
        
        String status;
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
    @Override
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
    
    @Override
    public synchronized List<Payment> getTransactions(Account origin) {
        return paymentDao.getTransactionsByAccount(origin);
    }
    
    @Override
    public synchronized List<Payment> getAllTransactions() {
        return paymentDao.getAllTransactions();
    }
    
    @Override
    public synchronized List<ScheduledPayment> getRecurringPayments(Account origin) {
        return scheduledPaymentDao.getRecurringPayments(origin);
    }
    
    @Override
    public synchronized List<ScheduledPayment> getAllRecurringPayments() {
        return scheduledPaymentDao.getAllRecurringPayments();
    }
    
    @Override
    public synchronized Payment getTransactionById(long id){
        return paymentDao.getTransaction(id);
    }
    
    @Override
    public synchronized void updateStatus(long id, String status){
        paymentDao.updateStatus(id, status);
    }
    
    @TransactionAttribute(REQUIRED)
    @Override
    public synchronized void updateBalances(Payment payment) throws SQLException {
        
        calculateBalances(payment.getOrigin(), payment.getRecipient(),
            payment.getCurrency(), payment.getAmount());

        updateStatus(payment.getId(), "accepted");
    }
    
    @TransactionAttribute(REQUIRED)
    @Override
    public synchronized void makeScheduledPayment(ScheduledPayment item) throws SQLException{
        //Insert all payments into payments DB
        makePayment("payment", item.getOrigin(), item.getRecipient(), item.getCurrency(),
                item.getAmount(), new Date()); 

        updateNextScheduledDate(item);
    }
    
    @Override
    public void removeScheduledPayment(long id){
        scheduledPaymentDao.remove(id);
    }
    
    @Override
    public String[] getAvailableCurrencies(){        
        return CurrencyServiceBean.getAvailableCurrencies();
    }
    
    private synchronized void addAmount(String recipient, float amount) 
            throws SQLException {
        
        Account recipientAccount = accountDao.getAccount(recipient);
        
        float balance = recipientAccount.getBalance();
        float newBalance = balance + amount;

        recipientAccount.setBalance(newBalance);
    }
    
    private synchronized void deductAmount(String originEmail, float amount) 
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
    
    private void calculateBalances(Account origin, Account recipient,
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
    
    private void updateNextScheduledDate(ScheduledPayment payment){
        Date nextScheduledDate = calculateNextScheduledDate(payment.getFrequency());

        if(nextScheduledDate == null){
            removeScheduledPayment(payment.getId());
            return;
        }
            
        payment.setNextScheduledDate(nextScheduledDate);
    }
    
    private Date calculateNextScheduledDate(String frequency) {
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
    
    private Date addDaysToDate(int noOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, noOfDays);
        
        return cal.getTime();
    }
    
    private Date addMonthsToDate(int noOfMonths) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, noOfMonths);
        
        return cal.getTime();
    }
    
    private Date getToday() {
        return new Date();
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