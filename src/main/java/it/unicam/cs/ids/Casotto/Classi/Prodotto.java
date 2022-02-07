package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Prodotto {

    @Id
    @GeneratedValue
    private long id;
    private String oggetto;
    private int quantita;
    private double prezzo;
    private Tipo tipo;

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Richiesta> richieste = new HashSet<>();

    public Prodotto() {
    }

    public Prodotto(String oggetto, double prezzo, int quantita, Tipo tipo) {
        this.oggetto = oggetto;
        this.quantita = quantita;
        this.prezzo = prezzo;
        this.tipo = tipo;
    }

    public long getId() {
        return this.id;
    }

    public String getOggetto() {
        return this.oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public int getQuantita() {
        return this.quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public double getPrezzo() {
        return this.prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public Tipo getTipo() {
        return this.tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "PRODOTTO[Id: " + this.id + " -- Descrizione: " + this.oggetto +" -- Quantita' disponibile: " + this.getQuantita() +
                " -- Prezzo unitario: " + this.getPrezzo() + " euro]";
    }
}