package CryptoApiRest.Model;

import javax.persistence.*;

@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idWallet;

    @Column(name = "amount")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_crypto")
    private Crypto crypto;

    public Wallet() {
    }

    public Wallet(double amount, User user, Crypto crypto) {
        super();
        this.amount = amount;
        this.user = user;
        this.crypto = crypto;
    }

    public long getId() {
        return idWallet;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public Crypto getCrypto() {
        return crypto;
    }
}