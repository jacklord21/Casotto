package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ordinazione;
import it.unicam.cs.ids.Casotto.Classi.Stato;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Stack;

@Repository
public interface OrdinazioneRepository extends CrudRepository<Ordinazione, Long> {

    boolean existsById(long id);

    List<Ordinazione> findByStatoAndData(Stato stato, LocalDate data);
}
