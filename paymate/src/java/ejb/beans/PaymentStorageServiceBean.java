package ejb.beans;

import services.CurrencyService;
import services.TimestampService;
import dao.jpa.JpaAccountDao;
import dao.jpa.JpaPaymentDao;
import dao.jpa.JpaScheduledPaymentDao;
import ejb.interfaces.PaymentStorageService;
import entities.Account;
import entities.Payment;
import entities.ScheduledPayment;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
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
@DeclareRoles({"admin"})
public class PaymentStorageServiceBean implements PaymentStorageService {

    @EJB
    private TimestampService timestampService;

    @EJB
    private JpaAccountDao accountDao;

    @EJB
    private JpaPaymentDao paymentDao;

    @EJB
    private JpaScheduledPaymentDao scheduledPaymentDao;

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

        //Determine the status of the transaction
        if (type.equals("payment")) {
            if (scheduledDate.after(today)) {
                //Insert a scheduled payment into the scheduled payment table
                schedulePayment(origin, recipient, currency, amount,
                        scheduledDate, "once");
                return;
            } else {
                //Set status to complete and calculate the account balances
                status = "completed";
                calculateBalances(origin, recipient, currency, amount);
            }
        } else {
            //If it is not a payment, it is a request and requires action 
            //from the request recipient
            status = "pending";
        }

        //Insert the transaction into the payments table
        paymentDao.insertTransaction(timestamp, type, origin, recipient, currency,
                amount, scheduledDate, status);
    }

    @TransactionAttribute(REQUIRED)
    @Override
    public synchronized void schedulePayment(Account origin, Account recipient,
            String currency, float amount, Date scheduledDate, String frequency)
            throws SQLException {
        Date nextScheduledDate = null;

        //If the scheduled payment starts today, make a payment right away and 
        //calculate the next scheduled date
        if (!scheduledDate.after(getToday())) {
            makePayment("payment", origin, recipient, currency, amount, getToday());
            nextScheduledDate = calculateNextScheduledDate(frequency);
        }

        //Insert the scheduled payment into the scheduled payments table
        scheduledPaymentDao.insertScheduledPayment(origin, recipient, currency,
                amount, nextScheduledDate, scheduledDate, frequency);
    }

    @Override
    public synchronized List<Payment> getTransactions(Account origin) {
        return paymentDao.getTransactionsByAccount(origin);
    }

    @RolesAllowed("admin")
    @Override
    public synchronized List<Payment> getAllTransactions() {
        return paymentDao.getAllTransactions();
    }

    @Override
    public synchronized List<ScheduledPayment> getRecurringPayments(Account origin) {
        return scheduledPaymentDao.getRecurringPayments(origin);
    }

    @RolesAllowed("admin")
    @Override
    public synchronized List<ScheduledPayment> getAllRecurringPayments() {
        return scheduledPaymentDao.getAllRecurringPayments();
    }

    @Override
    public synchronized Payment getTransactionById(long id) {
        return paymentDao.getTransaction(id);
    }

    @Override
    public synchronized void updateStatus(long id, String status) {
        paymentDao.updateStatus(id, status);
    }

    @TransactionAttribute(REQUIRED)
    @Override
    public synchronized void updateBalances(Payment payment) throws SQLException {
        //Calculate the origin account balance and the recipient account balance
        //in the Accounts table
        calculateBalances(payment.getOrigin(), payment.getRecipient(),
                payment.getCurrency(), payment.getAmount());

        //Update the status of the transaction in the Payments table
        updateStatus(payment.getId(), "accepted");
    }

    @TransactionAttribute(REQUIRED)
    @Override
    public synchronized void makeScheduledPayment(ScheduledPayment item) throws SQLException {
        //Insert scheduled payment into the Payments table
        makePayment("payment", item.getOrigin(), item.getRecipient(), item.getCurrency(),
                item.getAmount(), new Date());

        //Update the next scheduled date in the ScheduledPayments table
        updateNextScheduledDate(item);
    }

    @Override
    public void removeScheduledPayment(long id) {
        scheduledPaymentDao.remove(id);
    }

    @Override
    public String[] getAvailableCurrencies() {
        return CurrencyService.getAvailableCurrencies();
    }

    //Add amount to the recipients account balance and update the balance in 
    //the Accounts table
    private synchronized void addAmount(String recipient, float amount)
            throws SQLException {

        Account recipientAccount = accountDao.getAccount(recipient);

        float balance = recipientAccount.getBalance();
        float newBalance = balance + amount;

        recipientAccount.setBalance(newBalance);
    }

    //Deduct amount from the origins account balance and update the balance in 
    //the Accounts table
    private synchronized void deductAmount(String originEmail, float amount)
            throws SQLException {

        Account originAccount = accountDao.getAccount(originEmail);

        float balance = originAccount.getBalance();
        float newBalance = balance - amount;

        originAccount.setBalance(newBalance);
    }

    private float convertAmountIntoLocalCurrency(String email, String currency,
            float amount) throws SQLException {

        //Get user's selected default currency
        String localCurrency = accountDao.getAccount(email).getCurrency();

        //Return early if the selected default currency and payment currency match
        if (localCurrency.equals(currency)) {
            return amount;
        } else {
            //Pass in the default currency, the selected currency and the amount
            //to retrieve the value from the currency service
            return CurrencyService.getConvertedAmount(currency,
                    localCurrency, amount);
        }
    }

    //Calculate the account balances for each participant in their local currency
    private void calculateBalances(Account origin, Account recipient,
            String currency, float amount) throws SQLException {

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

    private void updateNextScheduledDate(ScheduledPayment payment) {
        Date nextScheduledDate = calculateNextScheduledDate(payment.getFrequency());

        //When there is no scheduledDate, the payment was only once, so remove the 
        //scheduled payment from the ScheduledPayments table
        if (nextScheduledDate == null) {
            removeScheduledPayment(payment.getId());
            return;
        }

        //Set the next scheduled date in the ScheduledPayments table
        payment.setNextScheduledDate(nextScheduledDate);
    }

    //Calculate the next scheduled date based on the selected frequency
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

    //Helper function to add days to today
    private Date addDaysToDate(int noOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, noOfDays);

        return cal.getTime();
    }

    //Helper function to add months to today
    private Date addMonthsToDate(int noOfMonths) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, noOfMonths);

        return cal.getTime();
    }

    //Helper function to return today
    private Date getToday() {
        return new Date();
    }

    //Check for scheduled payments every day at 8am server time
    @Schedule(second = "0", minute = "0", hour = "8", persistent = false)
    public void checkForScheduledPayments() throws SQLException {
        Date today = getToday();

        //Get all payments for today
        List payments = scheduledPaymentDao.getScheduledPaymentsByDate(today);

        //Iterate over each payment and make the payment
        for (Iterator<ScheduledPayment> i = payments.iterator(); i.hasNext();) {
            ScheduledPayment item = i.next();
            makeScheduledPayment(item);
        }
    }
}
