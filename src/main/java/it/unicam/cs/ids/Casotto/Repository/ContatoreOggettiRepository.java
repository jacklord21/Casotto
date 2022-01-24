package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.ContatoreOggetti;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;

/**
 * {@link Repository} per l'{@link Entity} {@link ContatoreOggetti}
 *
 */
@Transactional
public interface ContatoreOggettiRepository extends CrudRepository<ContatoreOggetti, Long> {

    /**
     * Query che restituisce il {@link ContatoreOggetti} con associata la descrizione indicata
     *
     * @param oggetto descrizione dell'oggetto
     * @return il {@link ContatoreOggetti} con associata la descrizione indicata, o null se questo non esiste
     */
    ContatoreOggetti findByOggetto(String oggetto);
}
