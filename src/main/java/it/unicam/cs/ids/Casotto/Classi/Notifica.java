package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Notifica {

    @Id
    @GeneratedValue
    private long id;
    private LocalDate datavalidita;
    private String testo;
    private Livello gruppo;

    public Notifica() {
    }

    public Notifica(String testo, Livello gruppo, LocalDate datavalidita) {
        this.datavalidita = datavalidita;
        this.testo = testo;
        this.gruppo = gruppo;
    }

    public Notifica(String testo, Livello gruppo) {
        this(testo, gruppo, null);
    }

    public long getId() {
        return this.id;
    }

    public LocalDate getDatavalidita() {
        return this.datavalidita;
    }

    public String getTesto() {
        return this.testo;
    }

    public Livello getGruppo() {
        return this.gruppo;
    }

    public String toString() {
        return "\nNOTIFICA\n" + this.getTesto() +
                ( (this.getDatavalidita()!=null) ? "\nData fine validita': " + this.getDatavalidita() : "" );
    }


}
