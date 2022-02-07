package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * Classe che rappresenta l'entit&agrave; Notifica
 *
 */
@Entity
@SuppressWarnings( {"unused", "FieldCanBeLocal"} )
public class Notifica {

    @Id
    @GeneratedValue
    private long id;
    private LocalDate datavalidita;
    private String testo;
    private Livello gruppo;

    /**
     * Costruttore di default che inizializza una notifica per la creazione della corrispondente tabella nel database
     *
     */
    public Notifica() {
    }

    /**
     * Costruttore che crea una notifica con una data di fine validit&agrave;
     *
     * @param testo testo della notifica
     * @param gruppo {@link Livello} al quale associare la notifica
     * @param datavalidita data di fine validit&agrave; della notifica
     *
     */
    public Notifica(String testo, Livello gruppo, LocalDate datavalidita) {
        this.datavalidita = datavalidita;
        this.testo = testo;
        this.gruppo = gruppo;
    }

    /**
     * Costruttore che crea una notifica senza data di fine validit&agrave;
     *
     * @param testo testo della notifica
     * @param gruppo {@link Livello} al quale associare la notifica
     *
     */
    public Notifica(String testo, Livello gruppo) {
        this(testo, gruppo, null);
    }

    /**
     * Restituisce l'identificativo della notifica
     *
     * @return l'identificativo della notifica
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce la data di fine validit&agrave; della notifica
     *
     * @return la data di fine validit&agrave; della notifica
     */
    public LocalDate getDatavalidita() {
        return this.datavalidita;
    }

    /**
     * Restituisce il testo della notifica
     *
     * @return il testo della notifica
     */
    public String getTesto() {
        return this.testo;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, della notifica
     *
     * @return una rappresentazione, sotto forma di {@link String}, della notifica
     */
    public String toString() {
        return "\nNOTIFICA\n" + this.getTesto() +
                ( (this.getDatavalidita()!=null) ? "\nData fine validita': " + this.getDatavalidita() : "" );
    }
}