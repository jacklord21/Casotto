package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.AccountRepository;
import it.unicam.cs.ids.Casotto.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Classe che rappresenta un gestore di {@link Account}, che permette di effettuare tutte le operazioni
 * connesse (registrazione, login, ...)
 *
 */
@Service
public class GestoreAccount {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UtenteRepository utenteRepository;

    /**
     * Metodo che permette, attraverso email e password, di effettuare il login a un {@link Account}
     *
     * @param email email associata all'{@link Account}
     * @param psw password associata all'{@link Account}
     *
     * @throws NullPointerException se uno dei parametri passati &egrave; nullo
     * @throws IllegalArgumentException se l'email passata come parametro non &egrave; associata a nessun account
     *
     * @return l'{@link Account} associato alle credenziali passate, o null se questo non esiste
     */
    public Account login(String email, String psw) {
        this.checkIsNull(email, psw);
        Account result = null;

        if (accountRepository.existsByEmailIgnoreCase(email))
            result = accountRepository.findByEmailIgnoreCaseAndPassword(email, psw.hashCode());

        return result;
    //    throw new IllegalArgumentException("L'email inserita non e' associata a nessun account");
    }

    /**
     * Metodo che permette a un {@link Utente} di registrarsi nel sistema, specificando il {@link Livello}, l'email e
     * la password da associare all'{@link Account}
     *
     * @param utente {@link Utente} che effettua la registrazione
     * @param livello {@link Livello} da associare all'{@link Account}
     * @param email email da associare all'{@link Account}
     * @param psw password da associare all'{@link Account}
     *
     * @throws NullPointerException se uno dei parametri passati &egrave; nullo
     *
     * @return true se la registrazione &egrave; andata a buon fine, false se esiste gi&agrave; un {@link Account}
     *         con associata l'email passata
     */
    public boolean registration(Utente utente, Livello livello, String email, String psw){
        this.checkIsNull(utente, email, psw, livello);
/*        if(accountRepository.existsByEmailIgnoreCase(email)) {
            return false;
        }*/
        utenteRepository.save(utente);
        accountRepository.save(new Account(email, psw, 0, livello, utente));
        return true;
    }

    public boolean checkIfUserExists(Utente u) {
        return utenteRepository.existsByNomeAndCognomeAndDataNascita(u.getNome(), u.getCognome(), u.getDataNascita());
    }

    /**
     * Metodo che permette di cambiare la password associata all'{@link Account} passato come parametro
     *
     * @param account {@link Account} del quale cambiare la password
     * @param psw nuova password da associare all'{@link Account}
     *
     * @throws NullPointerException se uno dei parametri passati &egrave; nullo
     *
     * @return true se la password &egrave; stata cambiata correttamente, false se l'{@link Account} passato come
     *         parametro non esiste
     */
    public boolean changePasswordAccount(Account account, String psw) {
        this.checkIsNull(account, psw);
        if(!accountRepository.existsById(account.getId())){
            return false;
            //    throw new IllegalArgumentException("L'account passato non esiste");
        }
        account.setPassword(psw);
        accountRepository.save(account);
        return true;
    }

    /**
     * Metodo che permette di cambiare il {@link Livello} dell'{@link Account} passato come parametro
     *
     * @param account {@link Account} del quale cambiare il {@link Livello}
     * @param livello nuovo livello da associare all'{@link Account}
     *
     * @throws NullPointerException se uno dei parametri passati &egrave; nullo
     *
     * @return true se il livello &egrave; stato cambiato correttamente, false se l'{@link Account} passato come
     *         parametro non esiste
     */
    public boolean updateLivelloAccount(Account account, Livello livello){
        this.checkIsNull(account, livello);
        if(!accountRepository.existsById(account.getId())){
            return false;
            //    throw new IllegalArgumentException("L'account passato non esiste");
        }
        account.setLivello(livello);
        accountRepository.save(account);
        return true;
    }

    /**
     * Metodo che permette di aggiornare il saldo dell'{@link Account} con il saldo passato come parametro
     *
     * @param account {@link Account} del quale aggiornare il saldo
     * @param saldo nuovo saldo dell'{@link Account}
     *
     * @throws NullPointerException se il parametro account &egrave; nullo
     *
     * @return true se il saldo &egrave; stato aggiornato correttamente, false se l'{@link Account} passato come
     *         parametro non esiste
     */
    public boolean updateSaldoAccount(Account account, double saldo){
        this.checkIsNull(account, saldo);
        if(!accountRepository.existsById(account.getId())){
            return false;
            //    throw new IllegalArgumentException("L'account passato non esiste");
        }
        account.setSaldo(saldo);
        accountRepository.save(account);
        return true;
    }

    /**
     * Metodo che permette di ottenere l'{@link Utente} proprietario dell'{@link Account} passato come parametro
     *
     * @param account {@link Account} del quale ottenere l'{@link Utente}
     *
     * @throws IllegalArgumentException se l'{@link Account} passato non esiste
     *
     * @return l'{@link Utente} associato all'account
     */
    public Utente getUtente(Account account) {
        this.checkIsNull(account);
        if(!accountRepository.existsById(account.getId())){
            return null;
         //   throw new IllegalArgumentException("L'account passato non esiste");
        }
        return utenteRepository.findByAccountId(account.getId());
    }

    private void checkIsNull(Object ... objects){
        for(Object obj: objects){
            if(obj == null){
                throw new NullPointerException("I paramentri passati sono nulli");
            }
        }
    }
}