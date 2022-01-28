package it.unicam.cs.ids.Casotto.Classi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestorePagamenti {

    @Autowired
    private GestoreProdotti gestoreProdotti;

    @Autowired
    private GestoreOrdinazione gestoreOrdinazione;

    @Autowired
    private GestoreAccount gestoreAccount;

    @Autowired
    private GestorePrenotazioni gestorePrenotazioni;

    public GestorePagamenti() {
    }

    public boolean pagamentoPrenotazione(Prenotazione prenotazione, Account account) {
        if(prenotazione.getPrezzo() >= account.getSaldo()) this.gestoreAccount.updateSaldoAccount(account, 0);
        else if(prenotazione.getPrezzo() < account.getSaldo()) this.gestoreAccount.updateSaldoAccount(account, account.getSaldo() - prenotazione.getPrezzo());

        //Pagamento "prezzoFinale"

        return this.gestorePrenotazioni.registrazionePrenotazione(prenotazione);
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

        scontrino.add("Ordinazione: " + ordinazione.getId() + " Ombrellone: " + ordinazione.getOmbrellone().getId());
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
