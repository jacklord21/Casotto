package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta l'entit&agrave; Ordinazione, la quale pu&ograve; essere associata a pi&ugrave; {@link Richiesta}
 * e
 *
 */
@Entity
@SuppressWarnings("unused")
public class Ordinazione {

    @Id
    @GeneratedValue
    private long id;
    private double prezzoTot;
    private Stato stato;
    private LocalDate data;

    @OneToMany(mappedBy = "ordinazione", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Richiesta> prodotti = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ombrellone_id", nullable = false)
    private Ombrellone ombrellone;

    /**
     * Costruttore di default che inizializza un'ordinazione per la creazione della corrispondente tabella nel database
     *
     */
    public Ordinazione() {
    }

    /**
     * Costruttore che crea un'ordinazione con l'{@link Ombrellone} passato
     *
     * @param ombrellone {@link Ombrellone} da associare all'ordinazione
     *
     */
    public Ordinazione(Ombrellone ombrellone) {
        this.ombrellone = ombrellone;
        this.stato = Stato.DA_PAGARE;
        this.data = LocalDate.now();
    }

    /**
     * Restituisce l'identificativo dell'ordinazione
     *
     * @return l'identificativo dell'ordinazione
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce le {@link Richiesta} presenti nell'ordinazione
     *
     * @return un {@link Set} contenente le {@link Richiesta} presenti nell'ordinazione
     */
    public Set<Richiesta> getRichieste() {
        return this.prodotti;
    }

    /**
     * Restituisce il prezzo totale dell'ordinazione
     *
     * @return il prezzo totale dell'ordinazione
     */
    public double getPrezzoTot() {
        return this.prezzoTot;
    }

    /**
     * Restituisce lo {@link Stato} dell'ordinazione
     *
     * @return lo {@link Stato} dell'ordinazione
     */
    public Stato getStato() {
        return this.stato;
    }

    /**
     * Restituisce la {@link LocalDate} dell'ordinazione
     *
     * @return la {@link LocalDate} dell'ordinazione
     */
    public LocalDate getData() {return this.data;}

    /**
     * Restituisce l' {@link Ombrellone} associato all'ordinazione
     *
     * @return l' {@link Ombrellone} associato all'ordinazione
     */
    public Ombrellone getOmbrellone() {
        return this.ombrellone;
    }

    /**
     * Aggiorna il prezzo totale dell'ordinazione
     *
     * @param prezzoTot nuovo prezzo totale dell'ordinazione
     */
    public void setPrezzoTot(double prezzoTot) {
        this.prezzoTot = prezzoTot;
    }

    /**
     * Aggiorna lo {@link Stato} dell'ordinazione
     *
     * @param stato nuovo {@link Stato} dell'ordinazione
     */
    public void setStato(Stato stato) {
        this.stato = stato;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, dell'ordinazione
     *
     * @return una rappresentazione, sotto forma di {@link String}, dell'ordinazione
     */
    @Override
    public String toString() {

        String richieste = "";

        for (Richiesta r : this.getRichieste())
            richieste = richieste.concat(r.toString() + "\n");

        return "\n\n\nORDINAZIONE" +
                "\t\nId: " + this.getId() +
                "\t\n" + richieste +
                "\t\nStato: " + this.getStato() +
                "\t\nOmbrellone: " + this.getOmbrellone().toString() +
                "\t\nPrezzo totale: " + this.getPrezzoTot();
    }
}