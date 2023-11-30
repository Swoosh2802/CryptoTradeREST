package CryptoApiRest.Repositories;

import CryptoApiRest.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByToken(@NotNull String token);
    User findByIdUser(@NotNull long idUser);
    User findByoAuthCookie(@NotNull String token);
    User findByEmail(@NotNull String email);
    User findByPseudo(@NotNull String pseudo);
}