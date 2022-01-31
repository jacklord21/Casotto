package it.unicam.cs.ids.Casotto.Classi;

import com.sun.istack.NotNull;

import java.util.Set;
import java.util.HashSet;
import javax.persistence.*;

/**
 * Classe che rappresenta l'entit&agrave; 'Ombrellone'
 *
 */
@Entity
@Table(name = "ombrellone")
public class Ombrellone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Ombrellone")
    private long id;

    @NotNull
    @Column(name = "Numero")
    private int numero;

    @NotNull
    @Column(name = "Fila")
    private String fila;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "costa",
            joinColumns = { @JoinColumn(name = "ombrellone_id") },
            inverseJoinColumns = { @JoinColumn(name = "prezzo_id") })
    private Set<Prezzo> prezzi = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE
            }, mappedBy = "ombrelloni"
    )
    public Set<Prenotazione> prenotazioni = new HashSet<>();

    /**
     * Costruttore di default che inizializza un ombrellone per la creazione della corrispondente tabella nel database
     *
     */
    public Ombrellone() {
    }

    /**
     * Costruttore che inizializza un ombrellone con i parametri passati: questi NON possono essere nulli
     *
     * @param numero numero dell'ombrellone
     * @param fila fila nella quale &egrave; posizionato l'ombrellone
     */
    public Ombrellone(int numero, String fila) {
        this.numero = numero;
        this.fila = fila;
    }

    /**
     * Restituisce l'identificativo dell'ombrellone
     *
     * @return l'identificativo dell'ombrellone
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce il numero dell'ombrellone
     *
     * @return il numero dell'ombrellone
     */
    public int getNumero() {
        return this.numero;
    }

    /**
     * Restituisce la fila nella quale &egrave; posizionato l'ombrellone
     *
     * @return la fila nella quale &egrave; posizionato l'ombrellone
     */
    public String getFila() {
        return this.fila;
    }

    /**
     * Aggiunge ai prezzi dell'ombrellone i prezzi contenuti nel {@link Set} passato come parametro: se quest'ultimo
     * &egrave; nullo, i prezzi correnti rimangono invariati
     *
     * @param prezzi nuovi prezzi da aggiungere
     */
    public void addPrezzi(Set<Prezzo> prezzi) {
        if(prezzi==null) return;
        this.prezzi.addAll(prezzi);
    }


    @Override
    public String toString() {
        return "OMBRELLONE[Id: " + this.getId() + " -- Fila: " + this.getFila() + " -- Capienza: " + this.getNumero() + "]";
    }

}