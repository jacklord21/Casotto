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
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getData() {
        return data;
    }

    public int getNumeroposti() {
        return numeroposti;
    }
}
