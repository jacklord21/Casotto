package it.unicam.cs.ids.Casotto.Classi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un gestore di pagamenti
 *
 */
@Service
@SuppressWarnings("UnusedReturnValue")
public class GestorePagamenti {

    @Autowired
    private Spiaggia spiaggia;

    @Autowired
    private GestoreOrdinazione gestoreOrdinazione;

    @Autowired
    private GestoreAccount gestoreAccount;

    @Autowired
    private GestorePrenotazioni gestorePrenotazioni;

    /**
     * Costruttore di default che inizializza un gestore di pagamenti
     *
     */
    public GestorePagamenti() {
    }

    /**
     * Rappresenta il pagamento di una {@link Prenotazione}
     *
     * @param pren {@link Prenotazione} da pagare
     * @param account {@link Account} che effettua la {@link Prenotazione}
     * @return se il pagamento della {@link Prenotazione} &egrave; andato a buon fine
     */
    public boolean pagamentoPrenotazione(Prenotazione pren, Account account) {
        pren.setPrezzo(this.spiaggia.getPrezzoTotale(pren));

        if(pren.getPrezzo() >= account.getSaldo()) this.gestoreAccount.updateSaldoAccount(account, 0);
        else if(pren.getPrezzo() < account.getSaldo()) this.gestoreAccount.updateSaldoAccount(account, account.getSaldo() - pren.getPrezzo());

        //Pagamento "prezzoFinale"

        return this.gestorePrenotazioni.registrazionePrenotazione(pren);
    }

    /**
     * Rappresenta il pagamento elettronico di un'{@link Ordinazione}
     *
     * @param ordinazione {@link Ordinazione} da pagare
     * @return se il pagamento dell'{@link Ordinazione} &egrave; andato a buon fine
     */
    public boolean pagamentoElettronico(Ordinazione ordinazione) {
        // implementazione comunicazione con il POS ed il sistema sta in attesa della risposta positiva del POS
        gestoreOrdinazione.setStato(ordinazione, Stato.PAGATO);
        return true;
    }

    /**
     * Rappresenta il pagamento in contanti di un'{@link Ordinazione}
     *
     * @param ordinazione {@link Ordinazione} da pagare
     * @param denaro quantit&agrave; di denaro che il cliente vuole utilizzare per pagare l'{@link Ordinazione}
     * @return il resto, o -1.0 se il denaro passato non &egrave; sufficiente a pagare l'{@link Ordinazione}
     */
    public double pagamentoContanti(Ordinazione ordinazione, double denaro) {
        if(denaro < ordinazione.getPrezzoTot()) return -1.0;

        gestoreOrdinazione.setStato(ordinazione, Stato.PAGATO);
        return denaro - ordinazione.getPrezzoTot();
    }

    /**
     * Genera lo scontrino relativo all'{@link Ordinazione} pagata
     *
     * @param ordinazione {@link Ordinazione} della quale generare lo scontrino
     * @return una {@link String} che rappresenta lo scontrino dell'{@link Ordinazione}, o vuota se l'{@link Ordinazione}
     *         passata &egrave; nulla, oppure se lo stato dell'{@link Ordinazione} non &egrave; corretto
     */
    public String creazioneScontrino(Ordinazione ordinazione) {

        if(ordinazione == null) return null;
        if(ordinazione.getStato() == Stato.DA_PAGARE || ordinazione.getStato() == Stato.CONSEGNATO) return null;

       List<String> scontrino = new ArrayList<>();

        scontrino.add("\n\nSCONTRINO: \n");

        scontrino.add("Ordinazione numero: " + ordinazione.getId() + "\nIdentificativo ombrellone: " + ordinazione.getOmbrellone().getId());
        for(Richiesta richiesta: this.gestoreOrdinazione.getRichiesteOf(ordinazione))
            scontrino.add(richiesta.toString());

        scontrino.add("Totale:  " + ordinazione.getPrezzoTot());
        return this.generateStringOfScontrino(scontrino);
    }

    /**
     * Genera uno scontrino a partire da una lista di {@link String}
     *
     * @param righe {@link List} di {@link String} che comporranno lo scontrino
     * @return una {@link String} che rappresenta uno scontrino
     */
    private String generateStringOfScontrino(List<String> righe) {
        StringBuilder scontrinoCompleto = new StringBuilder("Prodotto    Quantità    Prezzo\n");
        for(String riga: righe){
            scontrinoCompleto.append(riga);
            scontrinoCompleto.append("\n");
        }
        return scontrinoCompleto.toString();
    }
}
