package CryptoApiRest.Repositories;

import CryptoApiRest.Model.Crypto;
import CryptoApiRest.Model.User;
import CryptoApiRest.Model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>{
    List<Wallet> findWalletByUser(final User user);
    Wallet findByUserAndCrypto(final User user, final Crypto crypto);

    @Query("SELECT w FROM Wallet w ORDER BY w.crypto.shortName ASC")
    List<Wallet> getAllWalletOrderByCrypto();

    @Query("SELECT w FROM Wallet w WHERE w.amount <> 0 AND w.crypto.shortName = ?1")
    List<Wallet> getAllWalletByCrypto(final String shortName);
}