package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Partecipa;
import it.unicam.cs.ids.Casotto.Classi.Prenotazione;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository per l'entit&agrave; {@link Account}
 *
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    /**
     * Query che estrae l'{@link Account} con associati email e password indicati
     *
     * @param email email dell'{@link Account}
     * @param password password dell'{@link Account}
     * @return l'{@link Account} con associati email e password indicati, o null se questo non esiste
     */
    Account findByEmailIgnoreCaseAndPassword(String email, int password);

    /**
     * Query che controlla l'esistenza di un {@link Account} attraverso l'email, ignorando le lettere maiuscole
     *
     * @param email email dell'{@link Account}
     * @return true se esiste un'{@link Account} con email uguale a quella indicata, false altrimenti
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Query che estrae un {@link Account} in base all'identificativo di una {@link Partecipa}
     *
     * @param id identificativo della {@link Partecipa}
     * @return l'{@link Account} associato alla {@link Partecipa} identificata dall'id passato come parametro
     */
    Account findByIscrizioniId(long id);

    /**
     * Query che estrae un {@link Account} in base all'identificativo di una {@link Prenotazione}
     *
     * @param id identificativo della {@link Prenotazione}
     * @return l'{@link Account} associato alla {@link Prenotazione} identificata dall'id passato come parametro
     */
    Account findByPrenotazioniId(long id);

    /**
     * Aggiorna il saldo dell'{@link Account} (con identificativo uguale a quello passato come parametro) con il saldo
     * indicato
     *
     * @param id identificativo dell'{@link Account} del quale aggiornare il saldo
     * @param saldo nuovo saldo dell'{@link Account}
     */
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.saldo = ?2 WHERE a.id = ?1")
    void updateAccountSaldoById(long id, double saldo);

    /**
     * Aggiorna l'email dell'{@link Account} (con identificativo uguale a quello passato come parametro) con l'email
     * indicata
     *
     * @param id identificativo dell'{@link Account} del quale aggiornare l'email
     * @param email nuova email dell'{@link Account}
     */
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.email = ?2 WHERE a.id = ?1")
    void updateAccountEmailById(long id, String email);

    /**
     * Aggiorna la password dell'{@link Account} (con identificativo uguale a quello passato come parametro) con la
     * password indicata
     *
     * @param id identificativo dell'{@link Account} del quale aggiornare la password
     * @param psw nuova password dell'{@link Account}
     */
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.password = ?2 WHERE a.id = ?1")
    void updateAccountPasswordById(long id, int psw);
}