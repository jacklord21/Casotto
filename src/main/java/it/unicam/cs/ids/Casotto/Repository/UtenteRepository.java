package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.Entity;

/**
 * {@link Repository} per l'{@link Entity} {@link Utente}
 *
 */
@Repository
@Transactional
public interface UtenteRepository extends CrudRepository<Utente, Long>{

    /**
     * Query che estrae l'{@link Utente} proprietario dell'{@link Account} identificato dall'id passato come parametro
     *
     * @param accountID id dell'{@link Account}, del quale estrarre l'{@link Utente}
     * @return l'{@link Utente} proprietario dell'{@link Account}, o null se questo non esiste
     */
    Utente findByAccountId(long accountID);
}