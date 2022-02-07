package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Ombrellone;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

/**
 * Repository per l'entit&agrave; {@link Ombrellone}
 *
 */
public interface OmbrelloneRepository extends CrudRepository<Ombrellone, Long> {

    /**
     * Query che restituisce tutti gli ombrelloni della spiaggia
     *
     * @return una {@link List} contenente tutti gli ombrelloni della spiaggia
     */
    @NotNull List<Ombrellone> findAll();
}