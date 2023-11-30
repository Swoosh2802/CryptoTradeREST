package CryptoApiRest.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTransaction;

    @NotNull
    @Column(name = "priceAtTime")
    private double priceAtTime; //Prix en euro de la transaction

    @NotNull
    @Column(name = "date")
    private Date date;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cryptoFrom")
    private Crypto cryptoFrom;

    @NotNull
    @Column(name = "amountFrom")
    private double amountFrom;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cryptoTo")
    private Crypto cryptoTo;

    @NotNull
    @Column(name = "amountTo")
    private double amountTo;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public Transaction() {
    }

    public Transaction(final double amountFrom, final double amountTo, final double priceAtTime, final Date date, final Crypto cryptoFrom, final Crypto cryptoTo, final User user) {
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.priceAtTime = priceAtTime;
        this.date = date;
        this.cryptoFrom = cryptoFrom;
        this.cryptoTo = cryptoTo;
        this.user = user;
    }

    public long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public double getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public String getDate() {
        return date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Crypto getCryptoFrom() {
        return cryptoFrom;
    }

    public void setCryptoFrom(Crypto cryptoFrom) {
        this.cryptoFrom = cryptoFrom;
    }

    public double getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public Crypto getCryptoTo() {
        return cryptoTo;
    }

    public void setCryptoTo(Crypto cryptoTo) {
        this.cryptoTo = cryptoTo;
    }

    public double getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(double amountTo) {
        this.amountTo = amountTo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
