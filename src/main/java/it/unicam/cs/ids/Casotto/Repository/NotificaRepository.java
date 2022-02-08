package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Livello;
import it.unicam.cs.ids.Casotto.Classi.Notifica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Notifica}
 *
 */
@Repository
public interface NotificaRepository extends CrudRepository<Notifica, Long> {

    /**
     * Query che estrae tutte le {@link Notifica} associate al {@link Livello} indicato
     *
     * @param gruppo {@link Livello} del quale estrarre le notifiche associate
     * @return una {@link List} contenente tutte le {@link Notifica} associate al {@link Livello} indicato, o vuota
     *         se non ci sono {@link Notifica} associate al {@link Livello} indicato
     */
    List<Notifica> findByGruppo(Livello gruppo);
}
