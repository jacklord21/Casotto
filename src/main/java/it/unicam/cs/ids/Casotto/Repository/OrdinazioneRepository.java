package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ordinazione;
import it.unicam.cs.ids.Casotto.Classi.Stato;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Stack;

@Repository
public interface OrdinazioneRepository extends CrudRepository<Ordinazione, Long> {

    Ordinazione findById(long id);

    boolean existsById(long id);

    List<Ordinazione> findByStato(Stato stato);
}
