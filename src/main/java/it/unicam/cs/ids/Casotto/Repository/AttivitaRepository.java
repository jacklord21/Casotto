package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Attivita;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttivitaRepository extends CrudRepository<Attivita, Long> {

    Attivita findById(long id);

    List<Attivita> findByData(LocalDate data);

    Attivita findByPartecipantiId(long id);
}
