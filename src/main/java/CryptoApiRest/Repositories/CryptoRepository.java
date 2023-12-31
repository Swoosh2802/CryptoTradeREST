package CryptoApiRest.Repositories;

import CryptoApiRest.Model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long>{
    Crypto findByShortName(final String shortName);
}