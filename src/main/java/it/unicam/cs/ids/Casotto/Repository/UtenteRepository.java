package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

/**
 * Repository per l'entit&agrave; {@link Utente}
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

    /**
     * Query che controlla l'esistenza di un {@link Utente} attraverso il nome, il cognome e la data di nascita
     *
     * @param nome nome dell'{@link Utente}
     * @param cognome cognome dell'{@link Utente}
     * @param dataNascita {@link LocalDate} dell'{@link Utente}
     * @return true se esiste un'{@link Utente} con nome, cognome e data di nascita uguali a quelli indicati, false
     *         altrimenti
     */
    boolean existsByNomeAndCognomeAndDataNascita(String nome, String cognome, LocalDate dataNascita);
}