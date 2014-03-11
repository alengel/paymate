/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package payments;

import ejb.account.AccountStorageServiceBean;
import ejb.payment.PaymentStorageServiceBean;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class PaymentsBean implements Serializable {
    
    private String type;
    private String originEmail;
    private String recipient;
    private String currency;
    private String amount;
    private Date scheduledDate;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean transactionStore;
    
    public PaymentsBean(){
        originEmail = "alena@wa.com";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginEmail() {
        return originEmail;
    }

    public void setOriginEmail(String originEmail) {
        this.originEmail = originEmail;
    }
    
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public String makePayment(){
        if(validateFormFields()){
            return null;
        }
        
        type = "payment";
        
        //Insert payment into the DB payments table
        transactionStore.insertTransaction(type, originEmail, recipient, currency, 
                amount, scheduledDate);
        return "success";
    }
    
    public String requestFunds(){
        if(validateFormFields()){
            return null;
        }
        
        type = "request";
        
        return "success";
    }

    public Boolean validateFormFields(){
        if(!checkIfAccountExists()){
            return true;
        }
        
        if(!checkIfOriginIsNotRecipient()){
            return true;
        }
        
        if(scheduledDate == null){
            scheduledDate = new Date();
        }
        
        return false;
    }
    
    public Boolean checkIfAccountExists(){
        if(accountStore.checkAccountExists(recipient)){
            return true;
        }
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage("The recipient does not have an account with PayMate."));
        
        return false;
    }
    
    public Boolean checkIfOriginIsNotRecipient(){
        if(originEmail.equals(recipient)){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("You can't send funds to yourself."));

            return false;
        }
        
        return true;
    }
    
    public String backToPaymentsPage(){
        return "payments";
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("PaymentsBean: PostConstruct");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("PaymentsBean: PreDestroy");
    }
}
