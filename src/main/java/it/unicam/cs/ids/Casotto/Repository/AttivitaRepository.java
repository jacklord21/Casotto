package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Attivita;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Attivita}
 *
 */
@Repository
public interface AttivitaRepository extends CrudRepository<Attivita, Long> {

    /**
     * Query che estrae tutte le {@link Attivita} che si svolgono nella {@link LocalDate} indicata
     *
     * @param data {@link LocalDate} di svolgimento dell'{@link Attivita}
     * @return una {@link List} contenente tutte le {@link Attivita} che si svolgono nella {@link LocalDate} indicata,
     *         o vuota se non ci sono {@link Attivita} che si svolgono nella {@link LocalDate} indicata
     */
    List<Attivita> findByData(LocalDate data);

    /**
     * Query che estrae l'{@link Attivita} in base all'identificativo dell'{@link Account} indicato
     *
     * @param id identificativo dell'{@link Account} del quale estrarre l'{@link Attivita}
     * @return l'{@link Attivita} in base all'identificativo dell'{@link Account} indicato, oppure null se questa non
     *         esiste
     */
    Attivita findByPartecipantiId(long id);
}
