package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Classe che rappresenta un gestore di {@link Prodotto}
 *
 */
@Service
@SuppressWarnings( {"UnusedReturnValue", "BooleanMethodIsAlwaysInverted"} )
public class GestoreProdotti {

    @Autowired
    private ProdottoRepository prodottoRepository;

    /**
     * Restituisce la quantit&agrave; del {@link Prodotto} la cui descrizione corrisponde a quella passata come parametro
     *
     * @param oggetto descrizione del {@link Prodotto} del quale si vuole conoscere la quantit&agrave;
     * @return la quantit&agrave; del {@link Prodotto} la cui descrizione corrisponde a quella passata come parametro
     */
    public int getQuantitaOf(String oggetto) {
        return prodottoRepository.findByOggetto(oggetto).getQuantita();
    }

    /**
     * Restituisce il prezzo del {@link Prodotto} la cui descrizione corrisponde a quella passata come parametro
     *
     * @param oggetto descrizione del {@link Prodotto} del quale si vuole conoscere il prezzo
     * @return il prezzo del {@link Prodotto} la cui descrizione corrisponde a quella passata come parametro
     */
    public double getPrezzoOf(String oggetto) {
        return prodottoRepository.findByOggetto(oggetto).getPrezzo();
    }

    /**
     * Restituisce il {@link Prodotto} associato alla {@link Richiesta} passata come parametro
     *
     * @param richiesta {@link Richiesta} della quale si vuole conoscere il {@link Prodotto} associato
     * @return il {@link Prodotto} associato alla {@link Richiesta} passata come parametro
     */
    public Prodotto getProdottoOf(Richiesta richiesta) {
        return prodottoRepository.findByRichiesteId(richiesta.getId());
    }

    /**
     * Decrementa la quantit&agrave; del {@link Prodotto} indicato della quantit&agrave; passata come parametro. In
     * altre parole, la nuova quantit&agrave; del {@link Prodotto} sar&agrave;:
     * quantit&agrave; del {@link Prodotto} - la quantit&agrave; passata come parametro
     *
     * @param prodotto {@link Prodotto} del quale modificare la quantit&agrave;
     * @param quantita quantit&agrave; da sottrarre a quella del {@link Prodotto}
     */
    public void decrementoQuantitaProdotto(Prodotto prodotto, int quantita) {
        this.modificaQuantitaProdotto(prodotto, quantita * -1);
    }

    /**
     * Modifica la quantit&agrave; del {@link Prodotto} indicato della quantit&agrave; passata come parametro.
     * In altre parole, la nuova quantit&agrave; del {@link Prodotto} sar&agrave;:
     * quantit&agrave; del {@link Prodotto} + o - la quantit&agrave; passata come parametro.
     * Il metodo ritorna se il {@link Prodotto} non esiste oppure se esiste, ma non &egrave; presente nella quantit&agrave;
     * indicata
     *
     * @param prodotto {@link Prodotto} del quale modificare la quantit&agrave;
     * @param quantita quantit&agrave; da aggiungere o sottrarre a quella del {@link Prodotto}
     */
    private void modificaQuantitaProdotto(Prodotto prodotto, int quantita) {
        this.checkIsNull(prodotto, quantita);

        if(!prodottoRepository.existsById(prodotto.getId())) return;
        if(!this.isPresent(prodotto, quantita)) return;

        prodotto.setQuantita(prodotto.getQuantita()+quantita);
        prodottoRepository.save(prodotto);
    }

    /**
     * Crea un {@link Prodotto} con i parametri indicati
     *
     * @param oggetto descrizione del prodotto
     * @param prezzo prezzo unitario del prodotto
     * @param quantita quantit&agrave; disponibile del prodotto
     * @param tipo {@link Tipo} del prodotto
     *
     * @return un nuovo {@link Prodotto}
     */
    public Prodotto creazioneProdotto(String oggetto, double prezzo, int quantita, Tipo tipo){
        this.checkIsNull(oggetto, prezzo, quantita);
        return new Prodotto(oggetto, prezzo, quantita, tipo);
    }

    /**
     * Cancella un {@link Prodotto} dal database.
     * Il metodo ritorna se il {@link Prodotto} che si vuole cancellare non esiste
     *
     * @param prodotto {@link Prodotto} da cancellare dal database
     */
    private void cancellaProdotto(Prodotto prodotto) {
        this.checkIsNull(prodotto);
        if(!prodottoRepository.existsById(prodotto.getId())) return;

        prodottoRepository.deleteById(prodotto.getId());
    }

    /**
     * Permette di modificare un {@link Prodotto}
     *
     * @param prodotto {@link Prodotto} da modificare
     * @param cancella booleano che indica se il {@link Prodotto} vada cancellato o meno
     * @return true se il {@link Prodotto} &egrave; stato correttamente modificato
     */
    public boolean modificheProdotti(Prodotto prodotto, boolean cancella) {
        if(cancella) {
            this.cancellaProdotto(prodotto);
            return true;
        }

        this.prodottoRepository.save(prodotto);
        return true;
    }

    /**
     * Restituisce tutti i {@link Prodotto} di {@link Tipo} bevande presenti nel database
     *
     * @return una {@link List} contenente tutti i {@link Prodotto} di {@link Tipo} bevande presenti nel database,
     *         o vuota se non &egrave; presente alcun {@link Prodotto} di {@link Tipo} bevande
     */
    public List<Prodotto> getBevande() {
        return prodottoRepository.findByTipo(Tipo.BEVANDE);
    }

    /**
     * Restituisce tutti i {@link Prodotto} di {@link Tipo} cibo presenti nel database
     *
     * @return una {@link List} contenente tutti i {@link Prodotto} di {@link Tipo} cibo presenti nel database,
     *         o vuota se non &egrave; presente alcun {@link Prodotto} di {@link Tipo} cibo
     */
    public List<Prodotto> getCibo(){
        return prodottoRepository.findByTipo(Tipo.CIBO);
    }

    /**
     * Restituisce tutti i {@link Prodotto} di {@link Tipo} cibo o bevande presenti nel database
     *
     * @return una {@link List} contenente tutti i {@link Prodotto} di {@link Tipo} cibo o bevande presenti nel database,
     *         o vuota se non &egrave; presente alcun {@link Prodotto} di {@link Tipo} cibo o bevande
     */
    public List<Prodotto> getAllAlimenti() {
        List<Prodotto> lista = this.getBevande();
        lista.addAll(this.getCibo());
        return lista;
    }

    /**
     * Restituisce tutti i {@link Prodotto} presenti nel database
     *
     * @return una {@link List} contenente tutti i {@link Prodotto} presenti nel database, o vuota se non &egrave;
     *         presente alcun {@link Prodotto}
     */
    public List<Prodotto> getAll() {
        return StreamSupport.stream(prodottoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Controlla se il {@link Prodotto} &egrave; presente nella quantit&agrave; indicata
     *
     * @param prodotto {@link Prodotto} del quale controllare la quantit&agrave;
     * @param quantita quantit&agrave; da verificare
     * @return true se il {@link Prodotto} &egrave; presente nella quantit&agrave; indicata, false se questo non &egrave;
     *         vero, se il {@link Prodotto} non esiste o se la quantit&agrave; indicata &egrave; minore di ZERO
     */
    public boolean isPresent(Prodotto prodotto, int quantita) {
        this.checkIsNull(prodotto, quantita);
        if(!prodottoRepository.existsById(prodotto.getId()) || quantita < 0) return false;

        return prodottoRepository.findById(prodotto.getId()).getQuantita() >= quantita;
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
