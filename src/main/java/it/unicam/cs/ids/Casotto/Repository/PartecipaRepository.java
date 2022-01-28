package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Partecipa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartecipaRepository extends CrudRepository<Partecipa, Long> {

    Partecipa findById(long id);

    List<Partecipa> findByPartecipanteId(long id);

    List<Partecipa> findByAttivitaId(long id);

    Partecipa findByPartecipanteIdAndAttivitaId(long partecipanteId, long attivitaId);
}
