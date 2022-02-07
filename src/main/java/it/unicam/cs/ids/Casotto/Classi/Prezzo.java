package it.unicam.cs.ids.Casotto.Classi;

import java.util.Set;
import java.util.HashSet;
import javax.persistence.*;
import java.time.LocalDate;

/**
 * Classe che rappresenta l'entit&agrave; 'Prezzo'
 *
 */
@Entity
public class Prezzo {

    @Id
    @GeneratedValue
    private long id;
    private double prezzo;
    private int meseInizio;
    private int meseFine;
    private LocalDate DataInizio;
    private LocalDate DataFine;
    private Durata durata;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "prezzi")
    private Set<Ombrellone> ombrelloni = new HashSet<>();

    /**
     * Costruttore di default che inizializza un prezzo per la creazione della corrispondente tabella nel database
     *
     */
    Prezzo() {
    }

    /**
     * Costruttore che inizializza un prezzo con i parametri passati: questi NON possono essere nulli
     *
     * @param prezzo prezzo della prenotazione
     * @param meseInizio mese d'inizio validit&agrave; del prezzo
     * @param meseFine mese di fine validit&agrave; del prezzo
     * @param dataInizio data d'inizio validit&agrave; del prezzo per periodi speciali
     * @param dataFine data di fine validit&agrave; del prezzo per periodi speciali
     * @param durata {@link Durata} temporale del prezzo
     */
    public Prezzo(double prezzo, Integer meseInizio, Integer meseFine, LocalDate dataInizio, LocalDate dataFine, Durata durata) {
        this.prezzo = prezzo;
        this.meseInizio = (meseInizio==null) ? 0 : meseInizio;
        this.meseFine = (meseFine==null) ? 0 : meseFine;
        DataInizio = dataInizio;
        DataFine = dataFine;
        this.durata = durata;
    }

    /**
     * Restituisce l'identificativo del prezzo
     *
     * @return l'identificativo del prezzo
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce il prezzo
     *
     * @return il prezzo
     */
    public double getPrezzo() {
        return this.prezzo;
    }

    /**
     * Restituisce il mese d'inizio validit&agrave; del prezzo
     *
     * @return il mese d'inizio validit&agrave; del prezzo
     */
    public int getMeseInizio() {
        return this.meseInizio;
    }

    /**
     * Restituisce il mese di fine validit&agrave; del prezzo
     *
     * @return il mese di fine validit&agrave; del prezzo
     */
    public int getMeseFine() {
        return this.meseFine;
    }

    /**
     * Restituisce la data d'inizio validit&agrave; del prezzo per periodi speciali
     *
     * @return la data d'inizio validit&agrave; del prezzo per periodi speciali
     */
    public LocalDate getDataInizio() {
        return this.DataInizio;
    }

    /**
     * Restituisce la data di fine validit&agrave; del prezzo per periodi speciali
     *
     * @return la data di fine validit&agrave; del prezzo per periodi speciali
     */
    public LocalDate getDataFine() {
        return this.DataFine;
    }

    /**
     * Restituisce la {@link Durata} temporale associata al prezzo
     *
     * @return la {@link Durata} temporale associata al prezzo
     */
    public Durata getDurata() {
        return this.durata;
    }


    public String toString() {
        return "PREZZO" +
                "\n\tId: " + this.getId() +
                "\n\tData inizio: " + this.getDataInizio() + "\n\tData fine: " + this.getDataFine() +
                "\n\tMese inizio: " + this.getMeseInizio() + "\n\tMese fine: " + this.getMeseFine() +
                "\n\tDurata temporale: " + this.getDurata() + "\n\tPrezzo: " + this.getPrezzo();
    }
}