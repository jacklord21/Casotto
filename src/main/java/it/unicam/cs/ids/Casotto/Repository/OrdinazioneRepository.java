package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ombrellone;
import it.unicam.cs.ids.Casotto.Classi.Ordinazione;
import it.unicam.cs.ids.Casotto.Classi.Stato;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Ordinazione}
 *
 */
@Repository
@SuppressWarnings("BooleanMethodIsAlwaysInverted")

public interface OrdinazioneRepository extends CrudRepository<Ordinazione, Long> {

    /**
     * Query che controlla l'esistenza di un {@link Ombrellone} tramite l'identificativo passato come parametro
     *
     * @param id identificativo dell'ombrellone
     * @return true se esiste un {@link Ombrellone} con l'identificativo indicato, false altrimentis
     */
    boolean existsById(long id);

    /**
     * Query che estrae le {@link Ordinazione} effettuate nella data indicata e con lo {@link Stato} indicato
     *
     * @param stato {@link Stato} delle {@link Ordinazione} da estrarre
     * @param data {@link LocalDate} di effettuazione delle {@link Ordinazione}
     * @return una {@link List} contenente tutte le {@link Ordinazione} effettuate nella data indicata e con
     *         lo {@link Stato} indicato, o vuota se non ne esiste alcuna
     */
    List<Ordinazione> findByStatoAndData(Stato stato, LocalDate data);
}