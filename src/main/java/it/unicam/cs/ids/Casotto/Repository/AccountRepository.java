package it.unicam.cs.ids.Casotto.Repository;

import it.unicam.cs.ids.Casotto.Classi.Account;
import it.unicam.cs.ids.Casotto.Classi.Utente;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findById(long id);

    Account findByUtente(Utente utente);

    Account findByEmail(String email);

    Account findByEmailIgnoreCaseAndPassword(String email, int password);

    boolean existsByEmailIgnoreCase(String email);

    Account findByIscrizioniId(long id);

    Account findByPrenotazioniId(long id);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.saldo = ?2 WHERE a.id = ?1")
    void updateAccountSaldoById(long id, double saldo);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.email = ?2 WHERE a.id = ?1")
    void updateAccountEmailById(long id, String email);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.password = ?2 WHERE a.id = ?1")
    void updateAccountPasswordById(long id, int psw);


}
