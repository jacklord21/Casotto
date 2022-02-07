package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.AccountRepository;
import it.unicam.cs.ids.Casotto.Repository.AttivitaRepository;
import it.unicam.cs.ids.Casotto.Repository.PartecipaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Classe che rappresenta un gestore delle {@link Attivita}
 *
 */
@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreAttivita {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AttivitaRepository attivitaRepository;

    @Autowired
    PartecipaRepository partecipaRepository;

    @Autowired
    GestoreAccount gestoreAccount;

    /**
     * Restituisce tutte le {@link Attivita} per le quali l'{@link Account} passato come parametro ha effettuato una
     * {@link Partecipa}
     *
     * @param account del quale estrarre le prenotazioni delle {@link Attivita}
     * @return una {@link List} contenente tutte le {@link Attivita} per le quali l'{@link Account} ha effettuato una
     *         {@link Partecipa}, o vuota se l'{@link Account} non ha effettuato alcuna {@link Partecipa}
     */
    public List<Attivita> getAllAttivitaOf(Account account) {
        return partecipaRepository.findByPartecipanteId(account.getId())
                .stream().map(p -> attivitaRepository.findByPartecipantiId(p.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Restituisce tutte le {@link Attivita}, che si svolgono nel giorno corrente, per le quali l'{@link Account}
     * passato come parametro ha effettuato una {@link Partecipa}
     *
     * @param account del quale estrarre tutte le {@link Partecipa} delle {@link Attivita}
     * @return una {@link List} contenente tutte le {@link Attivita}, che si svolgono nel giorno corrente, per le
     *         quali l'{@link Account} ha effettuato una {@link Partecipa}, o vuota se l'{@link Account} non ha
     *         effettuato alcuna {@link Partecipa} per la data corrente
     */
    public List<Attivita> getAllAttivitaForTodayOf(Account account) {
        return this.getAllAttivitaOf(account).stream().filter(a -> a.getData().isEqual(LocalDate.now())).collect(Collectors.toList());
    }

    /**
     * Restituisce tutte le {@link Attivita} presenti nel database
     *
     * @return una {@link List} contenente tutte le {@link Attivita} presenti nel database, o vuota se non &egrave;
     *         presente alcuna {@link Attivita} nel database
     */
    public List<Attivita> getAllAttivita() {
        return StreamSupport.stream(attivitaRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Controlla se sono presenti {@link Attivita} per la data corrente
     *
     * @return true se sono presenti {@link Attivita} per la data corrente, false altrimenti
     */
    public boolean thereIsAttivitaForToday() {
        return !this.getAllAttivitaForToday().isEmpty();
    }

    /**
     * Restituisce tutte le {@link Attivita} che si svolgono nella data corrente
     *
     * @return una {@link List} contenente tutte le {@link Attivita} che si svolgono nella data corrente, o vuota se
     *         non sono previste {@link Attivita} per la data corrente
     */
    public List<Attivita> getAllAttivitaForToday(){
        return this.getAllAttivitaOf(LocalDate.now());
    }

    /**
     * Restituisce tutte le {@link Attivita} che si svolgono nella data indicata
     *
     * @param data {@link LocalDate} nella quale verificare la presenza di {@link Attivita}
     * @return una {@link List} contenente tutte le {@link Attivita} che si svolgono nella data indicata,
     *         o vuota se non sono previste {@link Attivita} nella data indicata
     */
    public List<Attivita> getAllAttivitaOf(LocalDate data) {
        return attivitaRepository.findByData(data);
    }

    /**
     * Restituisce i posti rimanenti per l'{@link Attivita} indicata
     *
     * @param attivita {@link Attivita} della quale si vogliono conoscere i posti rimanenti
     * @return i posti rimanenti per l'{@link Attivita} indicata
     */
    public int postiRimanenti(Attivita attivita) {
        int postiRimanenti = attivita.getNumeroposti();
        for(Partecipa iscrizione: partecipaRepository.findByAttivitaId(attivita.getId())){
            postiRimanenti -= iscrizione.getNumPartecipanti();
        }
        return postiRimanenti;
    }

    /**
     * Verifica se &egrave; possibile effettuare una {@link Partecipa} all'{@link Attivita} indicata, con il numero
     * di partecipanti indicato
     *
     * @param numPartecipanti numero di persone che desiderano iscriversi all'{@link Attivita}
     * @param attivita {@link Attivita} che si desidera prenotare
     *
     * @return true se &egrave; possibile effettuare una {@link Partecipa} all'{@link Attivita} indicata, false
     *         altrimenti
     */
    public boolean canPrenotate (int numPartecipanti, Attivita attivita) {
        return this.postiRimanenti(attivita) >= numPartecipanti;
    }

    /**
     * Permette di effettuare la {@link Partecipa} a un'{@link Attivita}
     *
     * @param account {@link Account} che effettua la {@link Partecipa}
     * @param numPartecipanti numero di persone che si iscrivono all'{@link Attivita}
     * @param attivita {@link Attivita} che si desidera prenotare
     *
     * @return true se la {@link Partecipa} &egrave; stata effettuata correttamente, false se non &egrave; possibile
     *         prenotare
     */
    public boolean prenotazione(Account account, int numPartecipanti, Attivita attivita) {
        if(!this.canPrenotate(numPartecipanti, attivita)) return false;

        Partecipa iscrizione = new Partecipa(numPartecipanti, account, attivita);
        partecipaRepository.save(iscrizione);
        this.gestoreAccount.updateLivelloAccount(account, Livello.PARTECIPANTE);

        return true;
    }

    /**
     * Permette di cancellare la {@link Partecipa} all'{@link Attivita} indicata
     *
     * @param account {@link Account} che desidera cancellare la {@link Partecipa} all'{@link Attivita} indicata
     * @param attivita {@link Attivita} della quale cancellare la {@link Partecipa}
     *
     * @return true se la {@link Partecipa} &egrave; stata cancellata, false altrimenti
     */
    public boolean cancellaPrenotazione(Account account, Attivita attivita){
        return this.cancellaPrenotazione(partecipaRepository.findByPartecipanteIdAndAttivitaId(account.getId(), attivita.getId()));
    }

    /**
     * Permette di cancellare la {@link Partecipa} indicata
     *
     * @param iscrizione {@link Partecipa} da cancellare
     * @return true se la {@link Partecipa} &egrave; stata cancellata, false se si tenta di cancellare una
     *         {@link Partecipa} che non esiste
     */
    private boolean cancellaPrenotazione(Partecipa iscrizione) {
        Account account = accountRepository.findByIscrizioniId(iscrizione.getId());

        if(!partecipaRepository.existsById(iscrizione.getId())) return false;

        if(!this.haveOtherIscrizioniForToday(account)) {
            account.setLivello(Livello.CLIENTE);
            accountRepository.save(account);
        }

        partecipaRepository.deleteById(iscrizione.getId());
        return true;
    }

    /**
     * Controlla se un {@link Account} ha {@link Partecipa} per il giorno corrente
     *
     * @param account {@link Account} del quale controllare la presenza di {@link Partecipa} per il giorno corrente
     * @return true se l'{@link Account} indicato ha {@link Partecipa} per il giorno corrente, false altrimenti
     */
    private boolean haveOtherIscrizioniForToday(Account account) {
        return !this.getAllAttivitaForTodayOf(account).isEmpty();
    }

    /**
     * Crea un'{@link Attivita} con i parametri indicati
     *
     * @param nome descrizione dell'{@link Attivita}
     * @param data data di svolgimento dell'{@link Attivita}
     * @param numPosti numero di posti dell'{@link Attivita}
     *
     * @return una nuova {@link Attivita}
     */
    public Attivita createAttivita(String nome, LocalDate data, int numPosti) {
        return new Attivita(nome, data, numPosti);
    }

    /**
     * Permette di modificare un'{@link Attivita}
     *
     * @param attivita {@link Attivita} da modificare
     * @param cancella booleano che indica se l'{@link Attivita} vada cancellata o meno
     * @return se l'{@link Attivita} &egrave; stata correttamente modificata
     */
    public boolean modificheAttivita(Attivita attivita, boolean cancella) {
        if(cancella) {
            this.cancellaAttivita(attivita);
            return true;
        }

        this.attivitaRepository.save(attivita);
        return true;
    }

    /**
     * Permette di cancellare un'{@link Attivita}
     *
     * @param attivita {@link Attivita} da cancellare
     */
    private void cancellaAttivita(Attivita attivita) {
        if(attivitaRepository.existsById(attivita.getId()))
            attivitaRepository.deleteById(attivita.getId());
    }
}
