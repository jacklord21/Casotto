package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    public LocalDate getData() {return this.data;}

    public Ombrellone getOmbrellone() {
        return ombrellone;
    }

    public void setPrezzoTot(double prezzoTot) {
        this.prezzoTot = prezzoTot;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {

        return "\nORDINAZIONE" +
                "\t\nId: " + this.getId() +
                "\t\n" + this.getProdotti().stream().toString() +
                "\t\nStato: " + this.getStato() +
                "\t\n" + this.getOmbrellone().toString();
    }

}
