package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author 119848
 */
@Entity
@NamedQuery(
        name = "getAccountWithEmail",
        query = "SELECT COUNT(c) FROM Account c WHERE c.email = :email"
)
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Column(unique = true)
    @NotNull
    String email;

    String password;

    String currency;

    float balance;

    @NotNull
    String permissionRole;

    @NotNull
    @Temporal(javax.persistence.TemporalType.DATE)
    Date registrationDate;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date lastLoggedIn;

    public Account() {
    }

    public Account(String email, String password, String currency, float balance,
            String permissionRole, Date registrationDate) {
        this.email = email;
        this.password = password;
        this.currency = currency;
        this.balance = balance;
        this.permissionRole = permissionRole;
        this.registrationDate = registrationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getPermissionRole() {
        return permissionRole;
    }

    public void setPermissionRole(String permissionRole) {
        this.permissionRole = permissionRole;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 23 * hash + Objects.hashCode(this.email);
        hash = 23 * hash + Objects.hashCode(this.password);
        hash = 23 * hash + Objects.hashCode(this.currency);
        hash = 23 * hash + Float.floatToIntBits(this.balance);
        hash = 23 * hash + Objects.hashCode(this.permissionRole);
        hash = 23 * hash + Objects.hashCode(this.registrationDate);
        hash = 23 * hash + Objects.hashCode(this.lastLoggedIn);
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
        final Account other = (Account) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (Float.floatToIntBits(this.balance) != Float.floatToIntBits(other.balance)) {
            return false;
        }
        if (!Objects.equals(this.permissionRole, other.permissionRole)) {
            return false;
        }
        if (!Objects.equals(this.registrationDate, other.registrationDate)) {
            return false;
        }
        if (!Objects.equals(this.lastLoggedIn, other.lastLoggedIn)) {
            return false;
        }
        return true;
    }

}
