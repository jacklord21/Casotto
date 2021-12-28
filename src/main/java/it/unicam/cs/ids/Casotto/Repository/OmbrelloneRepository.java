package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ombrellone;
import it.unicam.cs.ids.Casotto.Classi.Prenotazione;
import it.unicam.cs.ids.Casotto.Classi.Prezzo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.persistence.Entity;
import java.util.List;

/**
 * {@link Repository} per l'{@link Entity} {@link Ombrellone}
 *
 */
public interface OmbrelloneRepository extends CrudRepository<Ombrellone, Long>{

    /**
     * Query che restituisce l'{@link Ombrellone} posizionato nella fila indicata e con associato il numero indicato
     *
     * @param fila fila dell'{@link Ombrellone}
     * @param numero numero dell'{@link Ombrellone}
     * @return l'{@link Ombrellone} posizionato nella fila indicata e con associato il numero indicato, o null se
     *         non esiste
     */
    Ombrellone findByFilaAndNumero(char fila, int numero);

    /**
     * Query che estrae gli ombrelloni presenti nella fila indicata
     *
     * @param fila fila della spiaggia dalla quale estrarre gli ombrelloni
     * @return una {@link List} contenente tutti gli ombrelloni presenti nella fila indicata
     */
    List<Ombrellone> findByFila(char fila);

    /**
     * Query che estrae gli ombrelloni con associato il numero indicato, indipendentemente dalla fila
     *
     * @param numero numero dell'{@link Ombrellone}
     * @return una {@link List} contenente tutti gli ombrelloni con associato il numero indicato
     */
    List<Ombrellone> findByNumero(int numero);

    /**
     * Query che estrae gli ombrelloni con associato il {@link Prezzo} identificato dall'id passato come parametro
     *
     * @param prezzoId id del {@link Prezzo}
     * @return una {@link List} contenente gli ombrelloni con associato il {@link Prezzo} identificato dall'id
     *         passato come parametro
     */
    List<Ombrellone> findByPrezziId(long prezzoId);

    /**
     * Query che estrae gli ombrelloni associati alla {@link Prenotazione} identificata dall'id passato come parametro
     *
     * @param prenotazioneId id della {@link Prenotazione} della quale estrarre gli ombrelloni
     * @return una {@link List} contenente gli ombrelloni associati alla {@link Prenotazione} identificata
     *         dall'id passato come parametro
     */
    List<Ombrellone> findByPrenotazioniId(long prenotazioneId);

    /**
     * Query che restituisce tutti gli ombrelloni della spiaggia
     *
     * @return una {@link List} contenente tutti gli ombrelloni della spiaggia
     */
    List<Ombrellone> findAll();
}
