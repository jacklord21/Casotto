package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    public Account(){
    }

    public Account(String email, String password, double saldo, Livello livello, Utente utente) {
        this.email = email;
        this.password = password.hashCode();
        this.saldo = saldo;
        this.livello = livello;
        this.utente = utente;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.hashCode();
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Livello getLivello() {
        return livello;
    }

    public void setLivello(Livello livello) {
        this.livello = livello;
    }

    public Set<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }


    public String toString() {
        return "ACCOUNT (Id " + this.getId() + ")" +
                "\nNome: " + this.utente.getNome() +
                "\nCognome: " + this.utente.getCognome() +
                "\nLivello: " + this.getLivello() + "\n";
    }
}