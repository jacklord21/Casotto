package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@SuppressWarnings("unused")
public class Ordinazione {

    @Id
    @GeneratedValue
    private long id;
    private double prezzoTot;
    private Stato stato;
    private LocalDate data;

    @OneToMany(mappedBy = "ordinazione", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Richiesta> prodotti = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ombrellone_id", nullable = false)
    private Ombrellone ombrellone;

    public Ordinazione() {
    }

    public Ordinazione(Ombrellone ombrellone) {
        this.ombrellone = ombrellone;
        this.stato = Stato.DA_PAGARE;
        this.data = LocalDate.now();
    }

    public long getId() {
        return this.id;
    }

    public Set<Richiesta> getRichieste() {
        return this.prodotti;
    }

    public double getPrezzoTot() {
        return this.prezzoTot;
    }

    public Stato getStato() {
        return this.stato;
    }

    public LocalDate getData() {return this.data;}

    public Ombrellone getOmbrellone() {
        return this.ombrellone;
    }

    public void setPrezzoTot(double prezzoTot) {
        this.prezzoTot = prezzoTot;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {

        String richieste = "";

        for (Richiesta r : this.getRichieste())
            richieste = richieste.concat(r.toString() + "\n");

        return "\n\n\nORDINAZIONE" +
                "\t\nId: " + this.getId() +
                "\t\n" + richieste +
                "\t\nStato: " + this.getStato() +
                "\t\nOmbrellone: " + this.getOmbrellone().toString() +
                "\t\nPrezzo totale: " + this.getPrezzoTot();
    }
}