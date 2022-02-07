package it.unicam.cs.ids.Casotto.Classi;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Attivita {

    @Id
    @GeneratedValue
    private long id;

    private String nome;
    private LocalDate data;
    private int numeroposti;

    @OneToMany(mappedBy = "attivita", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Partecipa> partecipanti = new HashSet<>();

    public Attivita() {
    }

    public Attivita(String nome, LocalDate data, int numeroPosti) {
        this.nome = nome;
        this.data = data;
        this.numeroposti = numeroPosti;
    }

    public long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return this.data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getNumeroposti() {
        return this.numeroposti;
    }

    public void setNumeroposti(int numeroposti) {
        this.numeroposti = numeroposti;
    }

    @Override
    public String toString() {

        return "\nATTIVITA' " +
                "\n\tId: " + this.getId() +
                "\n\tNome: " + this.getNome() +
                "\n\tData: " + this.getData() +
                "\n\tNumero posti: " + this.getNumeroposti() + "\n";
    }
}