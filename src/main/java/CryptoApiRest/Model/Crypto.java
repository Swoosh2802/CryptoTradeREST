package CryptoApiRest.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "crypto")
public class Crypto {
    @Id
    @Column(name = "shortName")
    private String shortName;

    @NotNull
    @Column(name = "currentValue")
    private double currentValue; // Value in euro

    public Crypto() {
    }

    public Crypto(final String shortName) {
        super();
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(final double currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crypto crypto = (Crypto) o;
        return Objects.equals(shortName, crypto.shortName);
    }
}