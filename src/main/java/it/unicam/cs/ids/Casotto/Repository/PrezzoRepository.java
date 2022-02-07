package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ombrellone;
import it.unicam.cs.ids.Casotto.Classi.Prezzo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Prezzo}
 *
 */
public interface PrezzoRepository extends CrudRepository<Prezzo, Long> {

    /**
     * Query che estrae i prezzi associati a un'{@link Ombrellone} identificato dall'id passato come parametro
     *
     * @param ombrelloneId identificativo dell'{@link Ombrellone}
     * @return una {@link List} contenente i prezzi associati all'{@link Ombrellone} identificato dall'id passato
     *         come parametro
     */
    List<Prezzo> findByOmbrelloniId(long ombrelloneId);
}
