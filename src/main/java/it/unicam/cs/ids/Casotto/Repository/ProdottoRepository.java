package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Prodotto;
import it.unicam.cs.ids.Casotto.Classi.Richiesta;
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

    /**
     * Query che restituisce un {@link Prodotto} con id uguale a quello passato come parametro
     *
     * @param id identificativo del {@link Prodotto} da estrarre
     * @return il {@link Prodotto} con id uguale a quello passato come parametro, o null se non esiste alcun
     *         {@link Prodotto} con id uguale a quello passato come parametro
     */
    Prodotto findById(long id);

    /**
     * Query che restituisce il {@link Prodotto} con descrizione uguale a quella passata come parametro
     *
     * @param oggetto descrizione del {@link Prodotto} da estrarre
     * @return il {@link Prodotto} con descrizione uguale a quella passata come parametro, o null se non esiste alcun
     *         {@link Prodotto} con descrizione uguale a quella passata come parametro
     */
    Prodotto findByOggetto(String oggetto);

    /**
     * Query che restituisce il {@link Prodotto} associato alla {@link Richiesta} con id uguale a quello passato come
     * parametro
     *
     * @param richiestaId id della {@link Richiesta} dalla quale estrarre il {@link Prodotto}
     * @return il {@link Prodotto} associato alla {@link Richiesta} identificata dall'id passato come parametro
     */
    Prodotto findByRichiesteId(long richiestaId);

    /**
     * Query che restituisce tutti i {@link Prodotto} che sono del {@link Tipo} indicato
     *
     * @param tipo {@link Tipo} dei {@link Prodotto} che si vogliono estrarre
     * @return una {@link List} contenente tutti i {@link Prodotto} che sono del {@link Tipo} indicato, o vuota se non
     *         esiste alcun {@link Prodotto} del {@link Tipo} indicato
     */
    List<Prodotto> findByTipo(Tipo tipo);

    /**
     * Query che controlla l'esistenza di un {@link Prodotto} attraverso l'id
     *
     * @param id identificativo del {@link Prodotto} del quale verificare l'esistenza
     * @return true se esiste un {@link Prodotto} con id uguale a quello indicato, false altrimenti
     */
    boolean existsById(long id);

    /**
     * Query che elimina il {@link Prodotto} con id uguale a quello passato come parametro
     *
     * @param id identificativo del {@link Prodotto} da eliminare
     */
    void deleteById(long id);
}
