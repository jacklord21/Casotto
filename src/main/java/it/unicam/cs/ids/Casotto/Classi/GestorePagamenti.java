package it.unicam.cs.ids.Casotto.Classi;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestorePagamenti {

    private final GestoreProdotti gestoreProdotti;
    private final GestoreOrdinazione gestoreOrdinazione;

    public GestorePagamenti() {
        this.gestoreProdotti = new GestoreProdotti();
        this.gestoreOrdinazione = new GestoreOrdinazione();
    }

    /**
     * Metodo che calcola il prezzo finale di una {@link Prenotazione} in base al saldo dell'{@link Account}
     * passato come parametro. Se il saldo &egrave; maggiore del prezzo della prenotazione il prezzo finale &egrave;
     * ZERO, altrimenti al prezzo della prenotazione viene sottratto il saldo dell'account
     *
     * @param prenotazione {@link Prenotazione} della quale calcolare il prezzo finale
     * @param account {@link Account} del quale conoscere il saldo
     * @return il prezzo finale della {@link Prenotazione} in base al saldo dell'{@link Account}
     */
    public double prezzoFinale(Prenotazione prenotazione, Account account){
        return (account.getSaldo()>prenotazione.getPrezzo()) ? 0 : prenotazione.getPrezzo()-account.getSaldo();
    }

    public boolean pagamentoElettronico(Ordinazione ordinazione){
        //TODO: implementazione comunicazione con il POS ed il sistema sta in attesa della risposta positiva del POS
        gestoreOrdinazione.setStato(ordinazione, Stato.PAGATO);
        return true;
    }

    public double pagamentoContanti(Ordinazione ordinazione, double denaro){
        if(denaro < ordinazione.getPrezzoTot()){
            throw new IllegalArgumentException("Denaro insufficente");
        }
        gestoreOrdinazione.setStato(ordinazione, Stato.PAGATO);
        return denaro - ordinazione.getPrezzoTot();
    }

    public String creazioneScontrino(Ordinazione ordinazione){
        if(ordinazione == null){
            throw new NullPointerException("L'ordinazione passata è nulla");
        }
        if(ordinazione.getStato() == Stato.DA_PAGARE || ordinazione.getStato() == Stato.CONSEGNATO){
            return null;
        }
        GestoreOrdinazione gestoreOrdinazione = new GestoreOrdinazione();
        List<String> scontrino = new ArrayList<>();
        for(Richiesta richiesta: gestoreOrdinazione.getRichiesteOf(ordinazione)){
            scontrino.add(gestoreProdotti.getProdottoOf(richiesta).getOggetto() +
                    "  " + richiesta.getQuantita() + "  " + richiesta.getPrezzo());

        }
        scontrino.add("Totale:  "+ordinazione.getPrezzoTot());
        return this.generateStringOfScontrino(scontrino);
    }

    private String generateStringOfScontrino(List<String> righe){
        StringBuilder scontrinoCompleto = new StringBuilder("Prodotto    Quantità    Prezzo\n");
        for(String riga: righe){
            scontrinoCompleto.append(riga);
            scontrinoCompleto.append("\n");
        }
        return scontrinoCompleto.toString();
    }
}
