package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta l'entit&agrave; Prodotto, associabile a pi&ugrave; {@link Richiesta}
 *
 */
@Entity
@SuppressWarnings("unused")
public class Prodotto {

    @Id
    @GeneratedValue
    private long id;
    private String oggetto;
    private int quantita;
    private double prezzo;
    private Tipo tipo;

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Richiesta> richieste = new HashSet<>();

    /**
     * Costruttore di default che inizializza un prodotto per la creazione della corrispondente tabella nel database
     *
     */
    public Prodotto() {
    }

    /**
     * Costruttore che crea un prodotto con i parametri passati
     *
     * @param oggetto descrizione del prodotto
     * @param prezzo prezzo unitario del prodotto
     * @param quantita quantit&agrave; disponibile del prodotto
     * @param tipo {@link Tipo} del prodotto
     */
    public Prodotto(String oggetto, double prezzo, int quantita, Tipo tipo) {
        this.oggetto = oggetto;
        this.quantita = quantita;
        this.prezzo = prezzo;
        this.tipo = tipo;
    }

    /**
     * Restituisce l'identificativo del prodotto
     *
     * @return l'identificativo del prodotto
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce la descrizione del prodotto
     *
     * @return la descrizione del prodotto
     */
    public String getOggetto() {
        return this.oggetto;
    }

    /**
     * Imposta la descrizione del prodotto
     *
     * @param oggetto descrizione del prodotto
     */
    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    /**
     * Restituisce la quantit&agrave; disponibile del prodotto
     *
     * @return la quantit&agrave; disponibile del prodotto
     */
    public int getQuantita() {
        return this.quantita;
    }

    /**
     * Imposta la quantit&agrave; disponibile del prodotto
     *
     * @param quantita quantit&agrave; disponibile del prodotto
     */
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    /**
     * Restituisce il prezzo unitario del prodotto
     *
     * @return il prezzo unitario del prodotto
     */
    public double getPrezzo() {
        return this.prezzo;
    }

    /**
     * Imposta il prezzo unitario del prodotto
     *
     * @param prezzo prezzo unitario del prodotto
     */
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    /**
     * Restituisce il {@link Tipo} del prodotto
     *
     * @return il {@link Tipo} del prodotto
     */
    public Tipo getTipo() {
        return this.tipo;
    }

    /**
     * Imposta il {@link Tipo} del prodotto
     *
     * @param tipo {@link Tipo} del prodotto
     */
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, del prodotto
     *
     * @return una rappresentazione, sotto forma di {@link String}, del prodotto
     */
    @Override
    public String toString() {
        return "PRODOTTO[Id: " + this.id + " -- Descrizione: " + this.oggetto +" -- Quantita' disponibile: " + this.getQuantita() +
                " -- Prezzo unitario: " + this.getPrezzo() + " euro]";
    }
}