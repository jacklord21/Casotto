package it.unicam.cs.ids.Casotto.Classi;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Classe che rappresenta l'entit&agrave; 'Utente'
 *
 */
@Entity(name = "utente")
public class Utente
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_Utente")
    private long id;

    @NotNull
    @Column(name = "Nome")
    private String nome;

    @NotNull
    @Column(name = "Cognome")
    private String cognome;

    @NotNull
    @Column(name = "Data_Nascita")
    private LocalDate dataNascita;

    @OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "utente")
    private Account account;

    /**
     * Costruttore di default che inizializza un utente per la creazione della corrispondente tabella nel database
     *
     */
    Utente() {
    }

    /**
     * Costruttore che inizializza un utente con i parametri passati: questi NON possono essere nulli
     *
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param dataNascita data di nascita dell'utente
     */
    public Utente(String nome, String cognome, LocalDate dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
    }

    /**
     * Restituisce l'identificativo dell'utente
     *
     * @return l'identificativo dell'utente
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce il nome dell'utente
     *
     * @return il nome dell'utente
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce il cognome dell'utente
     *
     * @return il cognome dell'utente
     */
    public String getCognome() {
        return this.cognome;
    }

    /**
     * Restituisce la data di nascita dell'utente
     *
     * @return la data di nascita dell'utente
     */
    public LocalDate getDataNascita() {
        return this.dataNascita;
    }

    /**
     * Restituisce l'{@link Account} associato all'utente
     *
     * @return l'{@link Account}s associato all'utente
     */
    public Account getAccount() {
        return this.account;
    }

}