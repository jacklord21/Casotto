package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

/**
 * Classe che rappresenta l'entit&agrave; 'Contatore Oggetti'
 *
 */
@Entity
public class ContatoreOggetti {

    @Id
    @GeneratedValue
    private long id;
    private String oggetto;
    private int quantita;
    private double prezzo;

    /**
     * Costruttore di default che inizializza un contatore oggetti per la creazione della corrispondente
     * tabella nel database
     *
     */
    public ContatoreOggetti() {
    }

    /**
     * Costruttore che inizializza un contatore oggetti con i parametri passati: questi NON possono essere nulli
     *
     * @param oggetto descrizione dell'oggetto del quale si vuole memorizzare la quantit&agrave;
     * @param quantita quantit&agrave; di oggetti del tipo descritto
     * @param prezzo prezzo di un'unit&agrave; dell'oggetto
     */
    public ContatoreOggetti(String oggetto, int quantita, double prezzo) {
        this.oggetto = oggetto;
        this.quantita = quantita;
        this.prezzo = prezzo;
    }

    /**
     * Restituisce l'identificativo dell'oggetto
     *
     * @return l'identificativo dell'oggetto
     */
    public long getId() {
        return id;
    }

    /**
     * Restituisce la descrizione dell'oggetto
     *
     * @return la descrizione dell'oggetto
     */
    public String getOggetto() {
        return oggetto;
    }

    /**
     * Imposta la descrizione dell'oggetto con la descrizione passata come parametro: se quest'ultima &egrave; nulla,
     * la descrizione corrente rimane inalterata
     *
     * @param oggetto nuova descrizione dell'oggetto
     */
    public void setOggetto(String oggetto) {
        this.oggetto = (oggetto==null) ? this.oggetto : oggetto;
    }

    /**
     * Restituisce la quantit&agrave; dell'oggetto
     *
     * @return la quantit&agrave; dell'oggetto
     */
    public int getQuantita() {
        return quantita;
    }

    /**
     * Imposta la quantit&agrave; di oggetti rimasti con la quantit&agrave; passata come parametro: se
     * quest'ultima &egrave; negativa, la quantit&agrave; corrente rimane inalterata
     *
     * @param quantita quantit&agrave; di oggetti rimasti
     */
    public void setQuantita(int quantita) {
        this.quantita = (quantita<0) ? this.quantita : quantita;
    }

    /**
     * Restituisce il prezzo di un'unit&agrave; dell'oggetto
     *
     * @return il prezzo di un'unit&agrave; dell'oggetto
     */
    public double getPrezzo() {
        return prezzo;
    }

    /**
     * Imposta il prezzo di un'unit&agrave; dell'oggetto con il prezzo passato come parametro: se quest'ultimo
     * &egrave; negativo, il prezzo corrente rimane inalterato
     *
     * @param prezzo nuovo prezzo di un'unit&agrave; dell'oggetto
     */
    public void setPrezzo(double prezzo) {
        this.prezzo = (prezzo<0) ? this.prezzo : prezzo;
    }
}
