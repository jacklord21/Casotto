package it.unicam.cs.ids.Casotto.Classi;

import com.sun.istack.NotNull;

import java.util.Set;
import java.util.HashSet;
import javax.persistence.*;
import java.time.LocalDate;

/**
 * Classe che rappresenta l'entit&agrave; 'Prenotazione', che pu&ograve; essere associata a: 1 {@link Account}
 * e a pi&ugrave; {@link Ombrellone}
 *
 */
@Entity(name = "prenotazione")
@SuppressWarnings( {"unused", "FieldMayBeFinal"} )
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Prenotazione")
    private long id;

    @NotNull
    @Column(name = "Data_Prenotazione")
    private LocalDate dataPrenotazione;

    @NotNull
    @Column(name = "Lettini")
    private int lettini;

    @NotNull
    @Column(name = "Sdraie")
    private int sdraie;

    @NotNull
    @Column(name = "Durata")
    private Durata durata;

    @NotNull
    @Column(name = "Prezzo")
    private double prezzo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_Account", nullable = false)
    private Account account;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE
            })
    @JoinTable(name = "occupa",
            joinColumns = { @JoinColumn(name = "ID_Prenotazione") },
            inverseJoinColumns = { @JoinColumn(name = "ID_Ombrellone") }
    )
    private Set<Ombrellone> ombrelloni;

    /**
     * Costruttore di default che inizializza una prenotazione per la creazione della corrispondente tabella nel database
     *
     */
    public Prenotazione() {
        this.ombrelloni = new HashSet<>();
    }

    /**
     * Costruttore che inizializza una prenotazione con i parametri passati: questi NON possono essere nulli
     *
     * @param dataPrenotazione data della prenotazione
     * @param durata {@link Durata} temporale della prenotazione
     * @param lettini numero di lettini da associare alla prenotazione
     * @param sdraie numero di sdraie da associare alla prenotazione
     * @param account {@link Account} che effettua la prenotazione
     */
    public Prenotazione(LocalDate dataPrenotazione, Durata durata, int lettini, int sdraie, Account account) {
        this.dataPrenotazione = dataPrenotazione;
        this.durata = durata;
        this.lettini = lettini;
        this.sdraie = sdraie;
        this.prezzo = 0;
        this.account = account;
        this.ombrelloni = new HashSet<>();
    }

    /**
     * Restituisce l'identificativo della prenotazione
     *
     * @return l'identificativo della prenotazione
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce il {@link Set} di ombrelloni associati alla prenotazione
     *
     * @return il {@link Set} di ombrelloni associati alla prenotazione
     */
    public Set<Ombrellone> getOmbrelloni() {
        return this.ombrelloni;
    }

    /**
     * Restituisce la data associata alla prenotazione
     *
     * @return la data associata alla prenotazione
     */
    public LocalDate getDataPrenotazione() {
        return this.dataPrenotazione;
    }

    /**
     * Restituisce il prezzo della prenotazione
     *
     * @return il prezzo della prenotazione
     */
    public double getPrezzo() {
        return this.prezzo;
    }

    /**
     * Restituisce il numero di lettini associato alla prenotazione
     *
     * @return il numero di lettini associato alla prenotazione
     */
    public int getLettini() {
        return this.lettini;
    }

    /**
     * Restituisce il numero di sdraie associato alla prenotazione
     *
     * @return il numero di sdraie associato alla prenotazione
     */
    public int getSdraie() {
        return this.sdraie;
    }

    /**
     * Restituisce l'{@link Account} che ha effettuato la prenotazione
     *
     * @return l'{@link Account} che ha effettuato la prenotazione
     */
    public Account getAccount() {
        return this.account;
    }

    /**
     * Restituisce la {@link Durata} temporale della prenotazione
     *
     * @return la {@link Durata} temporale della prenotazione
     */
    public Durata getDurata() {
        return this.durata;
    }


    public void addOmbrelloni(Set<Ombrellone> ombrelloni){
        this.ombrelloni.addAll(ombrelloni);
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, della prenotazione
     *
     * @return una rappresentazione, sotto forma di {@link String}, della prenotazione;
     */
    @Override
    public String toString() {

        return "\nPRENOTAZIONE"+
                "\n\tId: " + this.getId() +
                "\n\tOmbrelloni: " + getStringaOmbrelloni() +
                "\n\tData prenotazione: " + this.getDataPrenotazione() +
                "\n\tPrezzo: " + this.getPrezzo() +
                "\n\tLettini: " + this.getLettini() +
                "\n\tSdraie: " + this.getSdraie() + "\n";
    }

    /**
     * Restituisce una rappresentazione personalizzata, sotto forma di {@link String}, degli {@link Ombrellone}
     * associati alla prenotazione
     *
     * @return una rappresentazione personalizzata, sotto forma di {@link String}, degli {@link Ombrellone}
     *         associati alla prenotazione
     */
    private String getStringaOmbrelloni() {
        String ris = "";

        for(Ombrellone o : this.getOmbrelloni())
            ris = ris.concat(o.toString() + ", ");

        return ris.substring(0, ris.length()-2);
    }
}