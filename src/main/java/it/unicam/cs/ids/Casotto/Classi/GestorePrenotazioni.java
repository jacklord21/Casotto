package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.PrenotazioniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta un gestore delle {@link Prenotazione}, che permette di effettuare le operazioni
 * connesse (conoscere il costo, sapere lo storico delle prenotazioni di un'utente, ...)
 *
 */
@Controller
public class GestorePrenotazioni {

    @Autowired
    PrenotazioniRepository pr;

    @Autowired
    GestoreAccount gestoreAccount;

    @Autowired
    Spiaggia gs;

    /**
     * Metodo che permette di registrare una {@link Prenotazione}
     *
     * @param prenotazione prenotazione da registrare
     *
     * @throws NullPointerException se la {@link Prenotazione} passata &egrave; nulla
     * @throws IllegalArgumentException se gli ombrelloni associati alla prenotazione sono gi&agrave; occupati o se
     *                                  non sono pi&ugrave; disponibili le sdraie o i lettini
     *
     * @return true se la prenotazione &egrave; stata registrata correttamente
     */
    public boolean registrazionePrenotazione(Prenotazione prenotazione) {
        if(Objects.isNull(prenotazione)) throw new NullPointerException("La prenotazione passata è nulla.");

        for(Ombrellone ombrellone : prenotazione.getOmbrelloni()){
            if(gs.notIsFree(ombrellone, prenotazione.getDataPrenotazione(), prenotazione.getDurata())){
                throw new IllegalArgumentException("Ombrelloni selezionati gia' prenotati");
            }
        }

        if(prenotazione.getLettini()>gs.lettiniDisponibili(prenotazione.getDataPrenotazione(), prenotazione.getDurata())
                || prenotazione.getSdraie()>gs.sdraieDisponibili(prenotazione.getDataPrenotazione(), prenotazione.getDurata()))
            throw new IllegalArgumentException("Sdraie o lettini non disponibili");

        pr.save(prenotazione);
        return true;
    }


    /**
     * Controlla se un {@link Account} ha {@link Prenotazione} nella data e per la {@link Durata} indicata
     *
     * @param account {@link Account} del quale controllare la presenza di {@link Prenotazione}
     * @param date {@link LocalDate} data nella quale controllare la presenza di {@link Prenotazione}
     * @param durata {@link Durata} durata della {@link Prenotazione}
     *
     * @return true se un {@link Account} ha {@link Prenotazione} nella data e per la {@link Durata} indicata,
     *         false altrimenti
     */
    public boolean haPrenotazioni(Account account, LocalDate date, Durata durata) {
        List<Prenotazione> prenotazioni = this.pr.findByDataPrenotazione(date).stream()
                                                  .filter(p->p.getAccount().getId()==account.getId()).collect(Collectors.toList());

        for(Prenotazione pren : prenotazioni)
            if(pren.getDurata()==Durata.INTERO || pren.getDurata().equals(durata) || durata==Durata.INTERO) return true;

        return false;
    }

    /**
     * Metodo che permette di ottenere la {@link Prenotazione} effettuata dall'{@link Account} con data uguale a
     * quella passata come parametro
     *
     * @param account {@link Account} del quale estrarre la {@link Prenotazione}
     * @param dataPrenotazione data della {@link Prenotazione} da estrarre
     * @return la {@link Prenotazione} con data uguale a quella passata come parametro
     */
    public Prenotazione getPrenotazioneOf(Account account, LocalDate dataPrenotazione) {
        return this.pr.findByAccountIdAndDataPrenotazione(account.getId(), dataPrenotazione);
    }

    /**
     * Metodo che restituisce una {@link List} contenente le prenotazioni valide, cio&egrave; quelle
     * con data successiva rispetto a quella corrente
     *
     * @param account {@link Account} del quale estrarre le prenotazioni valide
     * @return una {@link List} contenente le prenotazioni valide associate all'{@link Account}
     */
    public List<Prenotazione> getCurrentPrenotazioni(Account account) {
        return this.pr.findByAccountIdAndDataPrenotazioneAfter(account.getId(), LocalDate.now());
    }

    /**
     * Metodo che restituisce una {@link List} contenente le prenotazioni passate, cio&egrave; quelle
     * con data precedente rispetto a quella corrente
     *
     * @param account {@link Account} del quale estrarre le prenotazioni passate
     * @return una {@link List} contenente le prenotazioni passate associate all'{@link Account}
     */
    public List<Prenotazione> getPrenotazioniHistory(Account account){
        return this.pr.findByAccountIdAndDataPrenotazioneBefore(account.getId(), LocalDate.now());
    }

    /**
     * Metodo che permette di cancellare una {@link Prenotazione}
     *
     * @param prenotazione {@link Prenotazione} da cancellare
     * @return true se la {@link Prenotazione} &egrave; stata eliminata, false se la prenotazione &egrave; uguale a null
     *         o non esiste
     */
    public boolean cancellazionePrenotazione(Prenotazione prenotazione) {
        if(Objects.isNull(prenotazione) || !this.pr.existsById(prenotazione.getId())) return false;
        LocalDateTime now = LocalDateTime.now();

        if(now.until(prenotazione.getDataPrenotazione().atStartOfDay(), ChronoUnit.HOURS)<72) return false;

        Account a = this.gestoreAccount.getAccountOf(prenotazione);
        this.gestoreAccount.updateSaldoAccount(a, prenotazione.getPrezzo()+a.getSaldo());
        this.pr.deleteById(prenotazione.getId());
        return true;
    }

}