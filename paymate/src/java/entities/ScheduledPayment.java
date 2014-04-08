package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author 119848
 */
@Entity
public class ScheduledPayment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @OneToOne
    Account origin;

    @OneToOne
    Account recipient;

    @NotNull
    String currency;

    @NotNull
    float amount;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date nextScheduledDate;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date startDate;

    String frequency;

    public ScheduledPayment() {
    }

    public ScheduledPayment(Account origin, Account recipient, String currency,
            float amount, Date nextScheduledDate, Date startDate, String frequency) {
        this.origin = origin;
        this.recipient = recipient;
        this.currency = currency;
        this.amount = amount;
        this.nextScheduledDate = nextScheduledDate;
        this.startDate = startDate;
        this.frequency = frequency;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getNextScheduledDate() {
        return nextScheduledDate;
    }

    public void setNextScheduledDate(Date nextScheduledDate) {
        this.nextScheduledDate = nextScheduledDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 61 * hash + Objects.hashCode(this.origin);
        hash = 61 * hash + Objects.hashCode(this.recipient);
        hash = 61 * hash + Objects.hashCode(this.currency);
        hash = 61 * hash + Float.floatToIntBits(this.amount);
        hash = 61 * hash + Objects.hashCode(this.nextScheduledDate);
        hash = 61 * hash + Objects.hashCode(this.startDate);
        hash = 61 * hash + Objects.hashCode(this.frequency);
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
        final ScheduledPayment other = (ScheduledPayment) obj;
        if (this.id != other.id) {
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
        if (!Objects.equals(this.nextScheduledDate, other.nextScheduledDate)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.frequency, other.frequency)) {
            return false;
        }
        return true;
    }
}
