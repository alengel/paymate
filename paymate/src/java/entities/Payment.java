package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author 119848
 */

@Entity
@NamedQuery(name = "findAllPayments", query = "SELECT p FROM Payment p ")
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    
    @NotNull
    @Temporal(javax.persistence.TemporalType.DATE)
    Date paymentTimestamp;
    
    @NotNull
    String type;
    
    @OneToOne
    Account origin;
    
    @OneToOne
    Account recipient;
    
    @NotNull
    String currency;
    
    @NotNull
    float amount;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    Date scheduledDate;
    
    @NotNull
    String status;

    public Payment() {

    }

    public Payment(Date paymentTimestamp, String type, Account origin, Account recipient,
            String currency, float amount, Date scheduledDate, String status) {
        this.paymentTimestamp = paymentTimestamp;
        this.type = type;
        this.origin = origin;
        this.recipient = recipient;
        this.currency = currency;
        this.amount = amount;
        this.scheduledDate = scheduledDate;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(Date paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Account getOrigin() {
        return origin;
    }

    public void setOrigin(Account origin) {
        this.origin = origin;
    }

    public Account getRecipient() {
        return recipient;
    }

    public void setRecipient(Account recipient) {
        this.recipient = recipient;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 79 * hash + Objects.hashCode(this.paymentTimestamp);
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + Objects.hashCode(this.origin);
        hash = 79 * hash + Objects.hashCode(this.recipient);
        hash = 79 * hash + Objects.hashCode(this.currency);
        hash = 79 * hash + Float.floatToIntBits(this.amount);
        hash = 79 * hash + Objects.hashCode(this.scheduledDate);
        hash = 79 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Payment other = (Payment) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.paymentTimestamp, other.paymentTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.origin, other.origin)) {
            return false;
        }
        if (!Objects.equals(this.recipient, other.recipient)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (Float.floatToIntBits(this.amount) != Float.floatToIntBits(other.amount)) {
            return false;
        }
        if (!Objects.equals(this.scheduledDate, other.scheduledDate)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        return true;
    }

}
