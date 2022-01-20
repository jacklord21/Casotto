package it.unicam.cs.ids2122.Casotto;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProdottoRepository extends CrudRepository<Prodotto, Long> {

    Prodotto findById(long id);

    Prodotto findByOggetto(String oggetto);

    Prodotto findByRichiesteId(long richiestaId);

    List<Prodotto> findByTipo(Tipo tipo);

    boolean existsById(long id);

    boolean existsByOggetto(String oggetto);

    void deleteById(long id);
}
