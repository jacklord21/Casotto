package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;

/**
 * Classe che rappresenta l'entit&agrave; Richiesta, associabile a un'{@link Ordinazione} e a un {@link Prodotto}
 *
 */
@Entity
@SuppressWarnings("unused")
public class Richiesta {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordinazione_id", nullable = false)
    private Ordinazione ordinazione;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    private int quantita;
    private double prezzo;
    private String modifiche;


    /**
     * Costruttore di default che inizializza una richiesta per la creazione della corrispondente tabella nel database
     *
     */
    public Richiesta() {
    }

    /**
     * Costruttore che crea una richiesta con i parametri passati
     *
     * @param prodotto {@link Prodotto} da associare alla richiesta
     * @param quantita quantit&agrave; desiderata del prodotto
     * @param modifiche modifiche al prodotto richieste
     */
    public Richiesta(Prodotto prodotto, int quantita, String modifiche) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.modifiche = modifiche;
    }

    /**
     * Restituisce l'identificativo della richiesta
     *
     * @return l'identificativo della richiesta
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce l'{@link Ordinazione} alla quale &egrave; associata la richiesta
     *
     * @return l'{@link Ordinazione} alla quale &egrave; associata la richiesta
     */
    public Ordinazione getOrdinazione() {
        return this.ordinazione;
    }

    /**
     * Restituisce il {@link Prodotto} associato alla richiesta
     *
     * @return il {@link Prodotto} associato alla richiesta
     */
    public Prodotto getProdotto() {
        return this.prodotto;
    }

    /**
     * Restituisce la quantit&agrave; richiesta del {@link Prodotto} associato alla richiesta
     *
     * @return la quantit&agrave; richiesta del {@link Prodotto} associato alla richiesta
     */
    public int getQuantita() {
        return quantita;
    }

    /**
     * Restituisce il prezzo della richiesta
     *
     * @return il prezzo della richiesta
     */
    public double getPrezzo() {
        return this.prezzo;
    }

    /**
     * Restituisce le modifiche richieste al {@link Prodotto} associato alla richiesta
     *
     * @return le modifiche richieste al {@link Prodotto} associato alla richiesta
     */
    public String getModifiche() {
        return this.modifiche;
    }

    /**
     * Imposta l'{@link Ordinazione} alla quale sar&agrave; associata la richiesta
     *
     * @param ordinazione {@link Ordinazione} alla quale associare la richiesta
     */
    public void setOrdinazione(Ordinazione ordinazione) {
        this.ordinazione = ordinazione;
    }

    /**
     * Imposta il prezzo della richiesta
     *
     * @param prezzo prezzo della richiesta
     */
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, della richiesta
     *
     * @return una rappresentazione, sotto forma di {@link String}, della richiesta
     */
    public String toString() {

        return "\nRICHIESTA" +
                "\t\nId: " + this.getId() +
                "\t\nProdotto: " + this.getProdotto().getOggetto() +
                "\t\nQuantita': " + this.getQuantita() +
                "\t\nModifiche: " + ((this.getModifiche().isEmpty()) ? "nessuna" : this.getModifiche()) +
                "\t\nPrezzo: " + this.getPrezzo();
    }
}