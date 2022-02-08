package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Attivita;
import it.unicam.cs.ids.Casotto.Classi.Partecipa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Partecipa}
 *
 */
@Repository
public interface PartecipaRepository extends CrudRepository<Partecipa, Long> {

    /**
     * Query che estrae le {@link Partecipa} effettuate dall'{@link Account} con id uguale a quello passato come
     * parametro
     *
     * @param id identificativo dell'{@link Account}
     * @return una {@link List} contenente le {@link Partecipa} effettuate dall'{@link Account} con id uguale a
     *         quello passato come parametro, o vuota se l'{@link Account} non ha effettuato alcuna {@link Partecipa}
     */
    List<Partecipa> findByPartecipanteId(long id);

    /**
     * Query che estrae tutte le {@link Partecipa} associate all'{@link Attivita} con id uguale a quello passato come
     * parametro
     *
     * @param id identificativo dell'{@link Attivita} della quale estrarre le {@link Partecipa}
     * @return una {@link List} contenente le {@link Partecipa} associate all'{@link Attivita} con id uguale
     *         a quello passato come, o vuota se non ne esiste alcuna
     */
    List<Partecipa> findByAttivitaId(long id);

    /**
     * Query che estrae una {@link Partecipa} effettuata per l'{@link Attivita} con id uguale a quello passato come
     * parametro dall'{@link Account} con id uguale a quello indicato
     *
     * @param partecipanteId identificativo dell'{@link Account} del quale estrarre la {@link Partecipa}
     * @param attivitaId identificativo dell'{@link Attivita} per la quale estrarre la {@link Partecipa}
     * @return la {@link Partecipa} effettuata per l'{@link Attivita} con id uguale a quello passato come
     *         parametro dall'{@link Account} con id uguale a quello indicato
     */
    Partecipa findByPartecipanteIdAndAttivitaId(long partecipanteId, long attivitaId);
}
