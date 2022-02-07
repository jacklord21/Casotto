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

    List<Notifica> findByGruppo(Livello gruppo);
}
