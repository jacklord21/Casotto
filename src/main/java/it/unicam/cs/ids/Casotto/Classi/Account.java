package it.unicam.cs.ids.Casotto.Classi;

import com.sun.istack.NotNull;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;

/**
 * Classe che rappresenta l'entit&agrave; 'Account'
 *
 */
@Entity
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Account")
    private long id;

    @NotNull
    @Column(name = "Email")
    private String email;

    @NotNull
    @Column(name = "Password")
    private int password;

    @Column(name = "Saldo")
    private double saldo;

    @Column(name = "Livello")
    private Livello livello;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.REMOVE
    })
    private List<Prenotazione> prenotazioni;

    /**
     * Costruttore di default che inizializza un account per la creazione della corrispondente tabella nel database
     *
     */
    Account() {
    }

    /**
     * Costruttore che inizializza un account con i parametri passati: questi NON possono essere nulli
     *
     * @param utente {@link Utente} da associare all'account
     * @param livello {@link Livello} da associare all'account
     * @param email email da associare all'account
     * @param password password dell'account
     */
    public Account(Utente utente, Livello livello, String email, String password) {
        this.utente = Objects.requireNonNull(utente);
        this.saldo = 0;
        this.livello = Objects.requireNonNull(livello);
        this.email = email;
        this.password = password.hashCode();
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
     * Restituisce la password associata all'account
     *
     * @return la password associata all'account
     */
    public int getPassword() {
        return this.password;
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
     * Restituisce il {@link Livello} dell'account
     *
     * @return il {@link Livello} dell'account
     */
    public Livello getLivello() {
        return this.livello;
    }

    /**
     * Restituisce l'{@link Utente} proprietario dell'account
     *
     * @return l'{@link Utente} proprietario dell'account
     */
    public Utente getUtente() {
        return this.utente;
    }

    /**
     * Imposta il {@link Livello} dell'account con il {@link Livello} passato come parametro: se quest'ultimo
     * &egrave; nullo, il livello corrente rimane inalterato
     *
     * @param livello nuovo {@link Livello} da associare all'account
     */
    public void setLivello(Livello livello) {
        this.livello = (livello==null) ? this.livello : livello;
    }

    /**
     * Imposta il saldo dell'account con il saldo passato come parametro: se quest'ultimo &egrave; negativo,
     * il saldo corrente rimane inalterato
     *
     * @param saldo nuovo saldo dell'account
     */
    public void setSaldo(double saldo) {
        this.saldo = (saldo<0.0) ? this.saldo : saldo;
    }

    /**
     * Imposta la password dell'account con la password passata come parametro: se quest'ultima &egrave; nulla,
     * la password corrente rimane inalterata
     *
     * @param password nuova password da associare all'account
     */
    public void setPassword(String password) {
        this.password = (password==null) ? this.password : password.hashCode();
    }
}