package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Ombrellone;
import it.unicam.cs.ids.Casotto.Classi.Prenotazione;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

/**
 * {@link Repository} per l'{@link Entity} {@link Prenotazione}
 *
 */
@Repository
public interface PrenotazioniRepository extends CrudRepository<Prenotazione, Long>
{
        /**
         * Query che estrae le prenotazioni, effettuate nella data indicata, con associato l'{@link Ombrellone}
         * identificato dall'id passato come parametro
         *
         * @param id identificativo dell'{@link Ombrellone}
         * @param date data della prenotazione
         * @return una {@link List} contenente le prenotazioni, effettuate nella data indicata, con associato
         *         l'{@link Ombrellone} identificato dall'id passato come parametro
         */
        List<Prenotazione> findByOmbrelloniIdAndDataPrenotazione(long id, LocalDate date);

        /**
         * Query che estrae le prenotazioni con data uguale a quella indicata
         *
         * @param date data della prenotazione
         * @return una {@link List} contenente le prenotazioni con data uguale a quella indicata
         */
        List<Prenotazione> findByDataPrenotazione(LocalDate date);

        /**
         * Query che estrae la {@link Prenotazione}, effettuata nella data indicata, dall'{@link Account}
         * identificato dall'id passato come parametro
         *
         * @param id id dell'{@link Account} del quale estrarre la {@link Prenotazione}
         * @param date data della prenotazione
         * @return la {@link Prenotazione} effettuata dall'{@link Account} nella data indicata
         */
        Prenotazione findByAccountIdAndDataPrenotazione(long id, LocalDate date);

        /**
         * Query che estrae le prenotazioni, con associata una data precedente a quella corrente, effettuate
         * dall'{@link Account} identificato dall'id passato come parametro
         *
         * @param id id dell'{@link Account} del quale estrarre la {@link Prenotazione}
         * @param date data della prenotazione
         * @return la {@link Prenotazione} effettuata dall'{@link Account} con associata una data precedente a quella
         *         corrente
         */
        List<Prenotazione> findByAccountIdAndDataPrenotazioneBefore(long id, LocalDate date);

        /**
         * Query che estrae le prenotazioni, con associata una data successiva a quella corrente, effettuate
         * dall'{@link Account} identificato dall'id passato come parametro
         *
         * @param id id dell'{@link Account} del quale estrarre la {@link Prenotazione}
         * @param date data della prenotazione
         * @return la {@link Prenotazione} effettuata dall'{@link Account} con associata una data precedente a quella
         *         corrente
         */
        List<Prenotazione> findByAccountIdAndDataPrenotazioneAfter(long id, LocalDate date);
}













