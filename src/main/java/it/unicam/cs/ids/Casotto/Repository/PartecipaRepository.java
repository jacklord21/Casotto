package it.unicam.cs.ids.Casotto.Repository;

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

    List<Partecipa> findByPartecipanteId(long id);

    List<Partecipa> findByAttivitaId(long id);

    Partecipa findByPartecipanteIdAndAttivitaId(long partecipanteId, long attivitaId);
}
