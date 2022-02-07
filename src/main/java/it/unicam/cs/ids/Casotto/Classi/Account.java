package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta l'entit&agrave; Account, il quale pu&ograve; essere associato a: 1 {@link Utente},
 * pi&ugrave; {@link Prenotazione} e pi&ugrave; iscrizioni, attraverso l'entit&agrave; {@link Partecipa},
 * ad {@link Attivita}
 *
 */
@Entity
@SuppressWarnings("unused")
public class Account {

    @Id
    @GeneratedValue
    private long id;
    private String email;
    private int password;
    private double saldo;
    private Livello livello;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Prenotazione> prenotazioni = new HashSet<>();

    @OneToMany(mappedBy = "partecipante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Partecipa> iscrizioni = new HashSet<>();

    /**
     * Costruttore di default che inizializza un account per la creazione della corrispondente tabella nel database
     *
     */
    public Account() {
    }

    /**
     * Costruttore che crea un account con i parametri passati
     *
     * @param email email associata all'account
     * @param password password dell'account
     * @param saldo saldo dell'account
     * @param livello {@link Livello} dell'account
     * @param utente {@link Utente} proprietario dell'account
     */
    public Account(String email, String password, double saldo, Livello livello, Utente utente) {
        this.email = email;
        this.password = password.hashCode();
        this.saldo = saldo;
        this.livello = livello;
        this.utente = utente;
    }

    /**
     * Restituisce l'identificativo dell'account
     *
     * @return l'identificativo dell'account
     */
    public long getId() {
        return this.id;
    }

    /**
     * Restituisce l'email associata all'account
     *
     * @return l'email associata all'account
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Cambia l'email associata all'account con l'email passata come parametro
     *
     * @param email nuova email da associare all'account
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Cambia la password dell'account con la password passata come parametro
     *
     * @param password nuova password dell'account
     */
    public void setPassword(String password) {
        this.password = password.hashCode();
    }

    /**
     * Restituisce il saldo dell'account
     *
     * @return il saldo dell'account
     */
    public double getSaldo() {
        return this.saldo;
    }

    /**
     * Aggiorna il saldo dell'account con il saldo passato come parametro
     *
     * @param saldo nuovo saldo dell'account
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    /**
     * Restituisce il {@link Livello} dell'account
     *
     * @return il {@link Livello} dell'account
     */
    public Livello getLivello() {
        return this.livello;
    }

    /**
     * Cambia il {@link Livello} dell'account con il {@link Livello} passato come parametro
     *
     * @param livello nuovo {@link Livello} dell'account
     */
    public void setLivello(Livello livello) {
        this.livello = livello;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di {@link String}, dell'account
     *
     * @return una rappresentazione, sotto forma di {@link String}, dell'account
     */
    public String toString() {
        return "ACCOUNT (Id " + this.getId() + ")" +
                "\nNome: " + this.utente.getNome() +
                "\nCognome: " + this.utente.getCognome() +
                "\nLivello: " + this.getLivello() + "\n";
    }
}