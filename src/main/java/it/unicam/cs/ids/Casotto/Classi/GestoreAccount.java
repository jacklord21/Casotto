package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.AccountRepository;
import it.unicam.cs.ids.Casotto.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Classe che rappresenta un gestore di {@link Account}, che permette di effettuare tutte le operazioni
 * connesse (registrazione, login, ...)
 *
 */
@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreAccount {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UtenteRepository utenteRepository;

    /**
     * Restituisce tutti gli {@link Account} presenti nel database
     *
     * @return una {@link List} contenente tutti gli {@link Account} presenti nel database, o vuota se nel database
     *         non &egrave; presente alcun {@link Account}
     */
    public List<Account> getAllAccount() {
        return StreamSupport.stream(this.accountRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Metodo che permette, attraverso email e password, di effettuare il login a un {@link Account}
     *
     * @param email email associata all'{@link Account}
     * @param psw password associata all'{@link Account}
     *
     * @throws NullPointerException se uno dei parametri passati &egrave; nullo
     *
     * @return l'{@link Account} associato alle credenziali passate, o null se questo non esiste
     */
    public Account login(String email, String psw) {
        this.checkIsNull(email, psw);
        Account result = null;

        if (accountRepository.existsByEmailIgnoreCase(email))
            result = accountRepository.findByEmailIgnoreCaseAndPassword(email, psw.hashCode());

        return result;
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

        utenteRepository.save(utente);
        accountRepository.save(new Account(email, psw, 0, livello, utente));
        return true;
    }

    /**
     * Controlla se un'{@link Utente} sia presente (o meno) sul database
     *
     * @param u {@link Utente} del quale verificare la presenza sul database
     * @return true se l'{@link Utente} risulta presente sul database, false altrimenti
     */
    public boolean checkIfUserExists(Utente u) {
        return utenteRepository.existsByNomeAndCognomeAndDataNascita(u.getNome(), u.getCognome(), u.getDataNascita());
    }

    /**
     * Cambia il nome dell'{@link Utente} (assoociato all'{@link Account} passato come parametro) con il nome passato
     * come parametro
     *
     * @param account {@link Account} del quale cambiare il nome dell'{@link Utente} associato
     * @param nome nuovo nome dell'{@link Utente} associato all'{@link Account} passato come parametro
     *
     * @return true se il nome dell'{@link Utente} viene correttamente cambiato, false altrimenti
     */
    public boolean changeUserName(Account account, String nome) {
        return this.aggiornaDatiUtente(account, nome, Utente::setNome);
    }

    /**
     * Cambia il cognome dell'{@link Utente} (assoociato all'{@link Account} passato come parametro) con il cognome
     * passato come parametro
     *
     * @param account {@link Account} del quale cambiare il cognome dell'{@link Utente} associato
     * @param cognome nuovo cognome dell'{@link Utente} associato all'{@link Account} passato come parametro
     *
     * @return true se il cognome dell'{@link Utente} viene correttamente cambiato, false altrimenti
     */
    public boolean changeUserSurname(Account account, String cognome) {
        return this.aggiornaDatiUtente(account, cognome, Utente::setCognome);
    }

    /**
     * Cambia la data di nascita dell'{@link Utente} (assoociato all'{@link Account} passato come parametro) con la
     * data passata come parametro
     *
     * @param account {@link Account} del quale cambiare la data di nascita dell'{@link Utente} associato
     * @param data nuova {@link LocalDate} di nascita dell'{@link Utente} associato all'{@link Account} passato
     *             come parametro
     *
     * @return true se la data di nascita dell'{@link Utente} viene correttamente cambiata, false altrimenti
     */
    public boolean changeUserBirthdayDate(Account account, LocalDate data) {
        return this.aggiornaDatiUtente(account, data, Utente::setDataNascita);
    }

    /**
     * Metodo che permette di aggiornare un dato dell'{@link Utente} associato all'{@link Account} passato come
     * parametro
     *
     * @param account del quale cambiare il dato dell'{@link Utente} associato
     * @param valore nuovo valore del dato che si desidera cambiare
     * @param consumerLocale {@link BiConsumer} che cambia il valore del dato nella memoria locale
     * @param <T> tipo del dato che si desidera cambiare
     *
     * @return true se il dato dell'{@link Utente} viene correttamente cambiato, false altrimenti
     */
    private <T> boolean aggiornaDatiUtente(Account account, T valore, BiConsumer<Utente, T> consumerLocale) {
        this.checkIsNull(account, valore);
        if(!accountRepository.existsById(account.getId())) return false;

        Utente u = this.getUtenteOf(account);
        consumerLocale.accept(u, valore);
        this.utenteRepository.save(u);
        return true;
    }

    /**
     * Cambia l'email dell'{@link Account} passato
     *
     * @param account {@link Account} del quale cambiare l'email
     * @param email nuova email da associare all'{@link Account}
     *
     * @return true se l'email dell'{@link Account} viene correttamente cambiata, false altrimenti
     */
    public boolean changeAccountEmail(Account account, String email) {
        this.checkIsNull(account, email);
        if(!accountRepository.existsById(account.getId())) return false;

        account.setEmail(email);
        this.accountRepository.updateAccountEmailById(account.getId(), email);
        return true;
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
        if(!accountRepository.existsById(account.getId())) return false;

        account.setPassword(psw);
        this.accountRepository.updateAccountPasswordById(account.getId(), psw.hashCode());
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
        if(!accountRepository.existsById(account.getId())) return false;

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
    public boolean updateSaldoAccount(Account account, double saldo) {
        this.checkIsNull(account, saldo);
        if(!accountRepository.existsById(account.getId())) return false;

        account.setSaldo(saldo);
        accountRepository.updateAccountSaldoById(account.getId(), account.getSaldo());
        return true;
    }

    /**
     * Restituisce l'{@link Account} che ha effettuato la {@link Prenotazione} passata come parametro
     *
     * @param prenotazione {@link Prenotazione} dalla quale estrarre l'{@link Account} che l'ha effettuata
     * @return l'{@link Account} che ha effettuato la {@link Prenotazione}
     */
    public Account getAccountOf(Prenotazione prenotazione) {
        return this.accountRepository.findByPrenotazioniId(prenotazione.getId());
    }

    /**
     * Metodo che permette di ottenere l'{@link Utente} proprietario dell'{@link Account} passato come parametro
     *
     * @param account {@link Account} del quale ottenere l'{@link Utente}
     *
     * @throws NullPointerException se il parametro account &egrave; nullo
     *
     * @return l'{@link Utente} associato all'account
     */
    public Utente getUtenteOf(Account account) {
        this.checkIsNull(account);
        if(!accountRepository.existsById(account.getId())) return null;

        return utenteRepository.findByAccountId(account.getId());
    }

    /**
     * Controlla se i parametri passati sono nulli o meno. Se almeno un parametro risulta nullo, viene lanciata una
     * {@link NullPointerException}
     *
     * @param objects parametri dei quali si verifica l'eventuale nullit&agrave;
     *
     * @exception NullPointerException se uno dei parametri passati &egrave; nullo
     */
    private void checkIsNull(Object ... objects){
        for(Object obj: objects){
            if(obj == null) {
                throw new NullPointerException("I parametri passati sono nulli");
            }
        }
    }
}