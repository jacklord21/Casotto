package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Richiesta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RichiestaRepository extends CrudRepository<Richiesta, Long> {

    Richiesta findById(long id);

    List<Richiesta> findByOrdinazioneId(long ordinazioneId);
}
