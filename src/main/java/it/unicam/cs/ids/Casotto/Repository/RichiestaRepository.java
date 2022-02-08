package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ordinazione;
import it.unicam.cs.ids.Casotto.Classi.Richiesta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Richiesta}
 *
 */
@Repository
public interface RichiestaRepository extends CrudRepository<Richiesta, Long> {

    /**
     * Query che estrae le {@link Richiesta} dell'{@link Ordinazione} che ha associato l'identificativo passato come
     * parametro
     *
     * @param ordinazioneId identificativo dell'{@link Ordinazione} della quale estrarre le {@link Richiesta}
     * @return una {@link List} contenente tutte le {@link Richiesta} dell'{@link Ordinazione} che ha associato
     *         l'identificativo passato come parametro
     */
    List<Richiesta> findByOrdinazioneId(long ordinazioneId);
}
