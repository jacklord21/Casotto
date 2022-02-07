package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta l'entit&agrave; Attivit&agrave;, la quale pu&ograve; essere associata a
 * pi&ugrave; {@link Partecipa}
 *
 */
@Entity
@SuppressWarnings("unused")
public class Attivita {

    @Id
    @GeneratedValue
    private long id;

    private String nome;
    private LocalDate data;
    private int numeroposti;

    @OneToMany(mappedBy = "attivita", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Partecipa> partecipanti = new HashSet<>();

    /**
     * Costruttore di default che inizializza un'attivit&agrave; per la creazione della corrispondente tabella nel database
     *
     */
    public Attivita() {
    }

    /**
     * Costruttore che crea un'attivit&agrave; con i parametri passati
     *
     * @param nome descrizione dell'attivit&agrave;
     * @param data {@link LocalDate} di svolgimento dell'attivit&agrave;
     * @param numeroPosti capienza dell'attivit&agrave;
     */
    public Attivita(String nome, LocalDate data, int numeroPosti) {
        this.nome = nome;
        this.data = data;
        this.numeroposti = numeroPosti;
    }

    /**
     * Restituisce l'identificativo dell'attivit&agrave;
     *
     * @return l'identificativo dell'attivit&agrave;
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce la descrizione dell'attivit&agrave;
     *
     * @return la descrizione dell'attivit&agrave;
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Cambia la descrizione dell'attivit&agrave; con la descrizione passata come parametro
     *
     * @param nome nuova descrizione dell'attivit&agrave;
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce la {@link LocalDate} di svolgimento dell'attivit&agrave;
     *
     * @return la {@link LocalDate} di svolgimento dell'attivit&agrave;
     */
    public LocalDate getData() {
        return this.data;
    }

    /**
     * Cambia la {@link LocalDate} di svolgimento dell'attivit&agrave; con la {@link LocalDate} passata come parametro
     *
     * @param data nuova {@link LocalDate} di svolgimento dell'attivit&agrave;
     */
    public void setData(LocalDate data) {
        this.data = data;
    }

    /**
     * Restituisce il numero di posti dell'attivit&agrave;
     *
     * @return il numero di posti dell'attivit&agrave;
     */
    public int getNumeroposti() {
        return this.numeroposti;
    }

    /**
     * Aggiorna il numero di posti dell'attivit&agrave; con la quantit&agrave; passata come parametro
     *
     * @param numeroposti nuovo numero di posti dell'attivit&agrave;
     */
    public void setNumeroposti(int numeroposti) {
        this.numeroposti = numeroposti;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, dell'attivit&agrave;
     *
     * @return una rappresentazione, sotto forma di {@link String}, dell'attivit&agrave;
     */
    @Override
    public String toString() {
        return "\nATTIVITA' " +
                "\n\tId: " + this.getId() +
                "\n\tNome: " + this.getNome() +
                "\n\tData: " + this.getData() +
                "\n\tNumero posti: " + this.getNumeroposti() + "\n";
    }
}