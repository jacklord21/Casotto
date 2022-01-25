package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findById(long id);

    Account findByUtente(Utente utente);

    Account findByEmail(String email);

    Account findByEmailIgnoreCaseAndPassword(String email, int password);

    boolean existsByEmailIgnoreCase(String email);

    Account findByIscrizioniId(long id);

}
