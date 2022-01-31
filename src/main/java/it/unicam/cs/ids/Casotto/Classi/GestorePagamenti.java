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
    private Spiaggia spiaggia;

    @Autowired
    private GestoreOrdinazione gestoreOrdinazione;

    @Autowired
    private GestoreAccount gestoreAccount;

    @Autowired
    private GestorePrenotazioni gestorePrenotazioni;

    public GestorePagamenti() {
    }

    public boolean pagamentoPrenotazione(Prenotazione pren, Account account) {
        pren.setPrezzo(this.spiaggia.getPrezzoTotale(pren));

        if(pren.getPrezzo() >= account.getSaldo()) this.gestoreAccount.updateSaldoAccount(account, 0);
        else if(pren.getPrezzo() < account.getSaldo()) this.gestoreAccount.updateSaldoAccount(account, account.getSaldo() - pren.getPrezzo());

        //Pagamento "prezzoFinale"

        return this.gestorePrenotazioni.registrazionePrenotazione(pren);
    }

    public boolean pagamentoElettronico(Ordinazione ordinazione){
        //TODO: implementazione comunicazione con il POS ed il sistema sta in attesa della risposta positiva del POS
        gestoreOrdinazione.setStato(ordinazione, Stato.PAGATO);
        return true;
    }

    public double pagamentoContanti(Ordinazione ordinazione, double denaro) {
        if(denaro < ordinazione.getPrezzoTot()) return -1.0;

        gestoreOrdinazione.setStato(ordinazione, Stato.PAGATO);
        return denaro - ordinazione.getPrezzoTot();
    }

    public String creazioneScontrino(Ordinazione ordinazione) {

        if(ordinazione == null) throw new NullPointerException("L'ordinazione passata è nulla");
        if(ordinazione.getStato() == Stato.DA_PAGARE || ordinazione.getStato() == Stato.CONSEGNATO) return null;

       List<String> scontrino = new ArrayList<>();

        scontrino.add("\n\nSCONTRINO: \n");

        scontrino.add("Ordinazione numero: " + ordinazione.getId() + "\nIdentificativo ombrellone: " + ordinazione.getOmbrellone().getId());
        for(Richiesta richiesta: this.gestoreOrdinazione.getRichiesteOf(ordinazione))
            scontrino.add(richiesta.toString());

        scontrino.add("Totale:  " + ordinazione.getPrezzoTot());
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
