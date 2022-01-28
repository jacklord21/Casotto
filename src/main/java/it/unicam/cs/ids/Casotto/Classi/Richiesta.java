package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;

@Entity
public class Richiesta {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordinazione_id", nullable = false)
    private Ordinazione ordinazione;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    private int quantita;
    private double prezzo;
    private String modifiche;

    public Richiesta() {
    }

    public Richiesta(Prodotto prodotto, int quantita){
        this(prodotto, quantita, null);
    }


    public Richiesta(Prodotto prodotto, int quantita, String modifiche) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.modifiche = modifiche;
    }

    public long getId() {
        return id;
    }

    public Ordinazione getOrdinazione() {
        return ordinazione;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public String getModifiche() {
        return modifiche;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void setOrdinazione(Ordinazione ordinazione) {
        this.ordinazione = ordinazione;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public void setModifiche(String modifiche) {
        this.modifiche = modifiche;
    }


    @Override
    public String toString() {

        return "\nRICHIESTA" +
                "\t\nId: " + this.getId() +
                "\t\n" + this.getProdotto().toString() +
                "\t\nQuantita': " + this.getQuantita() +
                "\t\nModifiche: " + ((this.getModifiche().isEmpty()) ? "nessuna" : this.getModifiche());
    }
}
