package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;

/**
 * Classe che rappresenta l'iscrizione a un'{@link Attivita}
 *
 */
@Entity
@SuppressWarnings("unused")
public class Partecipa {

    @Id
    @GeneratedValue
    private long id;
    private int numPartecipanti;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partecipante_id", nullable = false)
    private Account partecipante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attività_id", nullable = false)
    private Attivita attivita;

    /**
     * Costruttore di default che inizializza un'iscrizione per la creazione della corrispondente tabella nel database
     *
     */
    public Partecipa() {
    }

    /**
     * Costruttore che inizializza un'iscrizione con i parametri passati
     *
     * @param numPartecipanti numero di persone che si iscrivono
     * @param partecipante {@link Account} che effettua l'iscrizione
     * @param attivita {@link Attivita} alla quale ci si iscrive
     */
    public Partecipa(int numPartecipanti, Account partecipante, Attivita attivita) {
        this.numPartecipanti = numPartecipanti;
        this.partecipante = partecipante;
        this.attivita = attivita;
    }

    /**
     * Restituisce l'identificativo dell'iscrizione
     *
     * @return l'identificativo dell'iscrizione
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce il numero di partecipanti dell'iscrizione
     *
     * @return il numero di partecipanti dell'iscrizione
     */
    public int getNumPartecipanti() {
        return this.numPartecipanti;
    }
}