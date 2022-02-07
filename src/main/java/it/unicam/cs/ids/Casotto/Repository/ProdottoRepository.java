package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Prodotto;
import it.unicam.cs.ids.Casotto.Classi.Tipo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Repository per l'entit&agrave; {@link Prodotto}
 *
 */
@Repository
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface ProdottoRepository extends CrudRepository<Prodotto, Long> {

    Prodotto findById(long id);

    Prodotto findByOggetto(String oggetto);

    Prodotto findByRichiesteId(long richiestaId);

    List<Prodotto> findByTipo(Tipo tipo);

    boolean existsById(long id);

    void deleteById(long id);
}
