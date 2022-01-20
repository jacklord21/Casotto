package it.unicam.cs.ids2122.Casotto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Ordinazione {

    @Id
    @GeneratedValue
    private long id;
    private double prezzoTot;
    private Stato stato;

    @OneToMany(mappedBy = "ordinazione", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Richiesta> prodotti = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ombrellone_id", nullable = false)
    private Ombrellone ombrellone;

    public Ordinazione() {
    }

    public Ordinazione(Ombrellone ombrellone) {
        this.ombrellone = ombrellone;
        this.stato = Stato.DA_PAGARE;
    }

    public long getId() {
        return id;
    }

    public Set<Richiesta> getProdotti() {
        return prodotti;
    }

    public double getPrezzoTot() {
        return prezzoTot;
    }

    public Stato getStato() {
        return stato;
    }

    public Ombrellone getOmbrellone() {
        return ombrellone;
    }

    public void setPrezzoTot(double prezzoTot) {
        this.prezzoTot = prezzoTot;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }
}
