package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.persistence.Entity;

/**
 * {@link Repository} per l'{@link Entity} {@link Account}
 *
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    /**
     * Query che estrae l'{@link Account} con associate le credenziali indicate
     *
     * @param email email associata all'{@link Account}
     * @param password password associata all'{@link Account}
     * @return l'{@link Account} con associate le credenziali indicate, o null se questo non esiste
     */
    Account findByEmailIgnoreCaseAndPassword(String email, int password);

    /**
     * Query per controllare se esiste un'{@link Account} associato all'email indicata
     *
     * @param email email associata all'{@link Account}
     * @return true se esiste un'{@link Account} associato all'email indicata, false altrimenti
     */
    boolean existsByEmailIgnoreCase(String email);
}
