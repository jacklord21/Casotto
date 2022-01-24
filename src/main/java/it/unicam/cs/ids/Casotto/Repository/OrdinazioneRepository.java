package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ordinazione;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdinazioneRepository extends CrudRepository<Ordinazione, Long> {

    Ordinazione findById(long id);

    boolean existsById(long id);
}
