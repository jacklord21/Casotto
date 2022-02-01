package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Classe che permette di effettuare le operazioni del barista, eccetto la notifica dei problemi (effettuata
 * nell'{@link InteractionManager})
 */
@Service
public class InteractionManagerBarista
{
    @Autowired private GestoreOrdinazione gestoreOrdinazione;

    /**
     * Metodo che permette di cominciare la preparazione di un'{@link Ordinazione}
     *
     * Il barista seleziona l'{@link Ordinazione} da preparare, della quale poi vedr&agrave; tutti i prodotti.
     * Al termine, l'{@link Ordinazione} avr&agrave; lo stato "IN_PREPARAZIONE"
     *
     * Se non risultano presenti {@link Ordinazione} da poter preparare, quest'operazione non &egrave; possibile
     */
    public void inizioPreparazione() {
        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.PAGATO);

        if(ordinazioni.isEmpty()) {
            System.out.println("AVVISO] Non ci sono ordinazioni da far pagare.");
            return;
        }

        Ordinazione ordinazione = Acquisizione.scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "\nSeleziona un'ordinazione da preparare", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        System.out.println("\n\nPRODOTTI ORDINAZIONE: " + ordinazione.toString());
        this.gestoreOrdinazione.setStato(ordinazione, Stato.IN_PREPARAZIONE);
        System.out.println("La modifica dello stato dell'ordinazione con id '" + ordinazione.getId() + " e' stata eseguita correttamente.");
    }

    /**
     * Metodo che permette di terminare la preparazione di un'{@link Ordinazione}
     *
     * Il barista seleziona l'{@link Ordinazione} da contrassegnare come pronta. Al termine,
     * l'{@link Ordinazione} avr&agrave; lo stato "IN_CONSEGNA"
     *
     * Se non risultano presenti {@link Ordinazione} da poter contrassegnare comne pronte, quest'operazione
     * non &egrave; possibile
     */
    public void finePreparazione() {
        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.IN_PREPARAZIONE);
        if(ordinazioni.isEmpty()) {
            System.out.println("AVVISO] Nessuna ordinazione in preparazione.");
            return;
        }

        Ordinazione ordinazione = Acquisizione.scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "\nSeleziona un'ordinazione da contrassegnare come pronta", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        this.gestoreOrdinazione.setStato(ordinazione, Stato.IN_CONSEGNA);
        System.out.println("La modifica dello stato dell'ordinazione con id '" + ordinazione.getId() + " e' stata eseguita correttamente.");
    }
}