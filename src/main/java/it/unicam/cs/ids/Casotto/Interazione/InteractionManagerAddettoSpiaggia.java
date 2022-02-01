package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Classe che permette di effettuare le operazioni dell'addetto spiaggia
 *
 */
@Service
public class InteractionManagerAddettoSpiaggia
{
    @Autowired private GestoreOrdinazione gestoreOrdinazione;

    @Autowired private GestorePagamenti gestorePagamenti;

    @Autowired private GestoreProdotti gestoreProdotti;

    private final Scanner sc = new Scanner(System.in);

    /**
     * Metodo che permette di far pagare un'{@link Ordinazione} al cliente
     *
     * Il cliente porta' scegliere se pagare l'{@link Ordinazione} in contanti o con metodo elettronico. Al termine,
     * l'{@link Ordinazione} avr&agrave; lo stato "PAGATO"
     *
     * Se non risulta presente nessuna {@link Ordinazione} da far pagare, quest'operazione non &egrave; possibile
     */
    public void pagamentoOrdinazione() {
        String sceltaPagamento;
        List<String> opzioni = List.of("c", "e");

        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.DA_PAGARE);

        if(ordinazioni.isEmpty()) {
            System.out.println("AVVISO] Nessuna ordinazione da far pagare. ");
            return;
        }

        Ordinazione ordinazione = Acquisizione.scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "\nSeleziona un'ordinazione da far pagare", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        generazioneContoOrdinazione(ordinazione);
        System.out.print("Il prezzo dell'ordinazione e' di " + ordinazione.getPrezzoTot() + " euro. " +
                "\nDesidera pagare in contanti (c) o con pagamento elettronico (e) (c/e)?: ");

        while(!opzioni.contains((sceltaPagamento=sc.nextLine())))
            System.out.print("Errore: la scelta fatta NON è prevista. Riprova: ");

        if(Objects.equals(sceltaPagamento, "c")) {
            double resto;
            do { resto = this.gestorePagamenti.pagamentoContanti(ordinazione, Acquisizione.acqDouble("i contanti", false)); }
            while (resto==-1.0);

            System.out.println("\nIl resto e' di: " + resto + " euro.");
        }
        else this.gestorePagamenti.pagamentoElettronico(ordinazione);
        // rappresenta la stampa dello scontrino con la stampante, con i relativi problemi
        System.out.println(this.gestorePagamenti.creazioneScontrino(ordinazione));
    }

    /**
     * Metodo che permette di generare il conto di un'{@link Ordinazione}
     *
     * Ogni {@link Richiesta} con modifiche, presente nell'{@link Ordinazione}, viene mostrata all'addetto spiaggia,
     * che dovr&agrave; inserire il prezzo comprendente le modifiche. Al termine, verr&agrave; ricalcolato il prezzo
     * totale dell'{@link Ordinazione}
     *
     * Se non risulta presente nessuna {@link Ordinazione} da far pagare, quest'operazione non &egrave; possibile.
     */
    private void generazioneContoOrdinazione(Ordinazione ordinazione) {
        if(ordinazione.getPrezzoTot()>0) return;

        for(Richiesta richiesta : this.gestoreOrdinazione.listaRichiesteConModifiche(ordinazione)) {
            System.out.print(richiesta.toString() + "\n\nPrezzo originale: " + this.gestoreProdotti.getProdottoOf(richiesta).getPrezzo() + "\n");
            this.gestoreOrdinazione.impostaPrezzoRichiesta(richiesta, Acquisizione.acqDouble("il prezzo comprendente le modifiche", false));
        }
        this.gestoreOrdinazione.ricalcolaPrezzoFinale(ordinazione);
    }

    /**
     * Metodo che permette di consegnare un'{@link Ordinazione}
     *
     * L'addetto spiaggia seleziona l'{@link Ordinazione} da consegnare, per poi consegnarla. Dopo la consegna,
     * l'{@link Ordinazione} avr&agrave; lo stato "CONSEGNATO"
     *
     * Se non risulta presente nessuna {@link Ordinazione} da consegnare, quest'operazione non &egrave; possibile
     */
    public void consegnaOrdinazioneConScontrino() {

        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.IN_CONSEGNA);
        if(ordinazioni.isEmpty()) {
            System.out.println("\nAVVISO] Nessuna ordinazione da consegnare.");
            return;
        }

        Ordinazione ordinazione = Acquisizione.scelta(ordinazioni, o->String.valueOf(o.getId()), o-> System.out.println(o.toString()),
                "Seleziona l'ordinazione da consegnare", "L'id selezionato NON e' associato ad alcun ordinazione da consegnare");

        // nella realtà l'addetto spiaggia prende lo scontrino stampato e lo "allega" all'ordinazione che consegnerà
        this.gestoreOrdinazione.setStato(ordinazione, Stato.CONSEGNATO);
        System.out.println("L'ordinazione e' stata consegnata. Lo stato dell'ordine e' stato aggiornato in 'CONSEGNATO'");
    }
}
