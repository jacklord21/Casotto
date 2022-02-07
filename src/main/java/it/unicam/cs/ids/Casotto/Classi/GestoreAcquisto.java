package it.unicam.cs.ids.Casotto.Classi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un gestore per l'acquisto di {@link Prodotto}
 *
 */
@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreAcquisto {

    @Autowired
    private GestoreProdotti gestoreProdotti;

    private final List<Richiesta> richieste;

    @Autowired
    private GestoreOrdinazione gestoreOrdinazione;

    /**
     * Classe che inizializza un gestore per l'acquisto di {@link Prodotto}
     *
     */
    public GestoreAcquisto() {
        this.richieste = new ArrayList<>();
    }

    /**
     * Aggiunge un {@link Prodotto} all'acquisto, creando una nuova {@link Richiesta} con associato il {@link Prodotto}
     * passato come parametro
     *
     * @param prodotto {@link Prodotto} che si vuole acquistare
     * @param quantita quantit&agrave; del {@link Prodotto} desiderata
     * @param modifiche modifiche che si desiderano sul {@link Prodotto} ordinato
     *
     * @return true se la {@link Richiesta} &egrave; stata correttamente registrata, false se la quantit&agrave;
     *         desiderata del {@link Prodotto} non &egrave; disponibile
     */
    public boolean addRichiesta(Prodotto prodotto, int quantita, String modifiche) {
        if(!gestoreProdotti.isPresent(prodotto, quantita)) return false;

        Richiesta richiesta = new Richiesta(prodotto, quantita, modifiche);
        richiesta.setPrezzo(prodotto.getPrezzo()*quantita);
        richieste.add(richiesta);
        return true;
    }

    /**
     * Restituisce tutte le {@link Richiesta} presenti nell'acquisto
     *
     * @return tutte le {@link Richiesta} presenti nell'acquisto
     */
    public List<Richiesta> getAllRichieste() {
        return this.richieste;
    }

    /**
     * Rimuove una {@link Richiesta} dall'acquisto
     *
     * @param richiesta {@link Richiesta} che si vuole rimuovere
     * @return true se la {@link Richiesta} &egrave; stata correttamente rimossa, false altrimenti
     */
    public boolean cancellaRichiesta(Richiesta richiesta) {
        return this.richieste.remove(richiesta);
    }

    /**
     * Permette di svuotare la {@link List} contenente i {@link Prodotto} selezionati
     *
     */
    public void cancellaAcquisto(){
        this.richieste.clear();
    }

    /**
     * Permette di confermare l'acquisto, creando una nuova {@link Ordinazione}
     *
     * @param ombrellone {@link Ombrellone} da associare all'{@link Ordinazione} che verr&agrave; creata
     *
     * @return una nuova {@link Ordinazione}, o null se almeno 1 {@link Prodotto} con la quantit&agrave; desiderata
     *         non &egrave; disponibile
     */
    public Ordinazione confirmOrdinazione(Ombrellone ombrellone) {
        return this.gestoreOrdinazione.creaOrdinazione(richieste, ombrellone);
    }

    /**
     * Restituisce l'attuale prezzo totale dell'acquisto
     *
     * @return l'attuale prezzo totale dell'acquisto
     */
    public double getPrezzoTotale() {
        double prezzoTotale = 0;

        for(Richiesta richiesta: this.richieste)
            prezzoTotale += richiesta.getPrezzo();

        return prezzoTotale;
    }
}