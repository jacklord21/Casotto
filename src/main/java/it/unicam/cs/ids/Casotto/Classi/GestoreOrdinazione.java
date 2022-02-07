package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.OrdinazioneRepository;
import it.unicam.cs.ids.Casotto.Repository.RichiestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un gestore di {@link Ordinazione}
 *
 */
@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreOrdinazione {

    @Autowired
    private GestoreProdotti gestoreProdotti;

    @Autowired
    private OrdinazioneRepository ordinazioneRepository;

    @Autowired
    private RichiestaRepository richiestaRepository;


    /**
     * Restituisce tutte le {@link Richiesta} di un'{@link Ordinazione}
     *
     * @param ordinazione {@link Ordinazione} della quale estrarre le {@link Richiesta}
     * @return una {@link List} contenente tutte le {@link Richiesta} di un'{@link Ordinazione}
     */
    public List<Richiesta> getRichiesteOf(Ordinazione ordinazione){
        this.checkIsNull(ordinazione);
        if(!ordinazioneRepository.existsById(ordinazione.getId())){
            throw new IllegalArgumentException("L'ordinazione passata non esiste");
        }
        return richiestaRepository.findByOrdinazioneId(ordinazione.getId());
    }

    /**
     * Restituisce tutte le {@link Ordinazione} che sono nello {@link Stato} indicato
     *
     * @param stato {@link Stato} dell'{@link Ordinazione}
     * @return una {@link List} contenente tutte le {@link Ordinazione} nello {@link Stato} indicato, o vuota se non
     *         ve ne &egrave; presente alcuna nello {@link Stato} indicato
     */
    public List<Ordinazione> getOrdinazioneWith(Stato stato) {
        this.checkIsNull(stato);
        return ordinazioneRepository.findByStatoAndData(stato, LocalDate.now());
    }

    /**
     * Crea un'{@link Ordinazione}, associata all'{@link Ombrellone} indicato, con le {@link Richiesta} indicate
     *
     * @param richieste {@link Richiesta} che formeranno l'{@link Ordinazione}
     * @param ombrellone {@link Ombrellone} al quale associare l'{@link Ordinazione}
     *
     * @return una nuova {@link Ordinazione}, o null se uno dei {@link Prodotto} non &egrave; disponibile nella
     *         quantit&agrave; indicata nella corrispondente {@link Richiesta}
     */
    public Ordinazione creaOrdinazione(List<Richiesta> richieste, Ombrellone ombrellone) {
        this.checkIsNull(richieste, ombrellone);
        Ordinazione ordinazione = new Ordinazione(ombrellone);

        if(!this.checkProdotti(richieste)) return null;
        if(!this.areThereRequiredChanges(richieste)) ordinazione.setPrezzoTot(this.getPrezzoTotaleRichieste(richieste));

        ordinazioneRepository.save(ordinazione);
        this.setStato(ordinazione, Stato.DA_PAGARE);
        ordinazioneRepository.save(ordinazione);

        for(Richiesta richiesta: richieste) {
            richiesta.setOrdinazione(ordinazione);
            gestoreProdotti.decrementoQuantitaProdotto(richiesta.getProdotto(), richiesta.getQuantita());
        }

        richiestaRepository.saveAll(richieste);
        return ordinazione;
    }

    /**
     * Imposta il prezzo di una {@link Richiesta} con il prezzo indicato
     *
     * @param richiesta {@link Richiesta} della quale impostare il prezzo
     * @param prezzo nuovo prezzo della {@link Richiesta}
     *
     * @return true se il prezzo della {@link Richiesta} viene impostato correttamente, false se si tenta d'impostare
     *         il prezzo di una {@link Richiesta} che non esiste
     */
    public boolean impostaPrezzoRichiesta(Richiesta richiesta, double prezzo) {
        this.checkIsNull(richiesta, prezzo);
        if(!richiestaRepository.existsById(richiesta.getId())) return false;

        richiesta.setPrezzo(prezzo*richiesta.getQuantita());
        richiestaRepository.save(richiesta);
        return true;
    }

    /**
     * Calcola il prezzo finale di un'{@link Ordinazione} contenente richieste di modifiche ai {@link Prodotto}
     *
     * @param ordinazione {@link Ordinazione} della quale ottenere il prezzo finale
     * @return il prezzo finale dell'{@link Ordinazione} (comprese le modifiche), o il prezzo che l'{@link Ordinazione}
     *         ha gi&agrave; se questa NON contiene richieste di modifiche ai {@link Prodotto}
     */
    public double ricalcolaPrezzoFinale(Ordinazione ordinazione) {
        this.checkIsNull(ordinazione);
        if(ordinazione.getPrezzoTot() != 0) {
            return ordinazione.getPrezzoTot();
        }

        ordinazione.setPrezzoTot(this.getPrezzoTotaleRichieste(richiestaRepository.findByOrdinazioneId(ordinazione.getId())));
        ordinazioneRepository.save(ordinazione);
        return ordinazione.getPrezzoTot();
    }

    /**
     * Restituisce tutte le {@link Richiesta} di un'{@link Ordinazione} che presentano richieste di modifiche al
     * {@link Prodotto} associato
     *
     * @param ordinazione {@link Ordinazione} della quale estrarre le {@link Richiesta}
     * @return una {@link List} contenente tutte le {@link Richiesta} di un'{@link Ordinazione} che presentano richieste
     *         di modifiche al {@link Prodotto} associato, o vuota se non ve ne &egrave; presente alcuna con modifiche
     */
    public List<Richiesta> listaRichiesteConModifiche(Ordinazione ordinazione){
        List<Richiesta> richieste = new ArrayList<>();
        for(Richiesta richiesta: richiestaRepository.findByOrdinazioneId(ordinazione.getId())){
            if(!richiesta.getModifiche().isEmpty()){
                richieste.add(richiesta);
            }
        }
        return richieste;
    }

    /**
     * Imposta lo {@link Stato} di un'{@link Ordinazione}
     *
     * @param ordinazione {@link Ordinazione} della quale impostare lo {@link Stato}
     * @param stato {@link Stato} da associare all'{@link Ordinazione}
     */
    public void setStato(Ordinazione ordinazione, Stato stato){
        this.checkIsNull(ordinazione, stato);
        if(!ordinazioneRepository.existsById(ordinazione.getId())) return;

        ordinazione.setStato(stato);
        ordinazioneRepository.save(ordinazione);
    }

    /**
     * Verifica se ci sono {@link Richiesta} che presentano richieste di modifiche al {@link Prodotto} associato
     *
     * @param richieste {@link Richiesta} delle quali controllare la presenza di modifiche desiderate
     * @return true se ci sono {@link Richiesta} che presentano richieste di modifiche al {@link Prodotto} associato,
     *         false altrimenti
     */
    private boolean areThereRequiredChanges(List<Richiesta> richieste) {
        this.checkIsNull(richieste);
        for(Richiesta richiesta: richieste)
            if(!richiesta.getModifiche().isEmpty()) return true;

        return false;
    }

    /**
     * Verifica se i {@link Prodotto} associati alle {@link Richiesta} sono disponibili nelle quantit&agrave; indicate
     *
     * @param richieste {@link Richiesta} delle quali controllare la disponibilit&agrave; dei {@link Prodotto} associati
     * @return true se tutti i {@link Prodotto} associati alle {@link Richiesta} sono disponibili nelle quantit&agrave;
     *         indicate, false altrimenti
     */
    private boolean checkProdotti(List<Richiesta> richieste) {
        for (Richiesta richiesta : richieste)
            if (!gestoreProdotti.isPresent(richiesta.getProdotto(), richiesta.getQuantita())) return false;

        return true;
    }

    /**
     * Calcola il prezzo totale di una {@link List} di richieste
     *
     * @param richieste {@link List} di {@link Richiesta} della quale calcolare il prezzo totale
     * @return il prezzo totale della {@link List} di richieste indicata
     */
    private double getPrezzoTotaleRichieste(List<Richiesta> richieste) {
        double prezzoFinale = 0;
        for(Richiesta richiesta: richieste) {
            prezzoFinale+=richiesta.getPrezzo();
        }
        return prezzoFinale;
    }

    /**
     * Controlla se i parametri passati sono nulli o meno. Se almeno un parametro risulta nullo, viene lanciata una
     * {@link NullPointerException}
     *
     * @param objects parametri dei quali si verifica l'eventuale nullit&agrave;
     *
     * @exception NullPointerException se uno dei parametri passati &egrave; nullo
     */
    private void checkIsNull(Object ... objects) {
        for(Object obj: objects){
            if(obj == null){
                throw new NullPointerException("I parametri passati sono nulli");
            }
        }
    }

}
