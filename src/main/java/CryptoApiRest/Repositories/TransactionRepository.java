package CryptoApiRest.Repositories;

import CryptoApiRest.Model.Transaction;
import CryptoApiRest.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findTransactionByUserOrderByIdTransactionDesc(final User user);
}