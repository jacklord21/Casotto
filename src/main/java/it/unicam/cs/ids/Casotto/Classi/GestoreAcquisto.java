package it.unicam.cs.ids.Casotto.Classi;

import java.util.ArrayList;
import java.util.List;

public class GestoreAcquisto {

    private final GestoreProdotti gestoreProdotti;
    private final List<Richiesta> richieste;
    private final GestoreOrdinazione gestoreOrdinazione;

    public GestoreAcquisto() {
        this.gestoreProdotti = new GestoreProdotti();
        this.richieste = new ArrayList<>();
        this.gestoreOrdinazione = new GestoreOrdinazione();
    }

    public boolean addRichiesta(Prodotto prodotto, int quantita, String modifiche){
        if(!gestoreProdotti.decrementoQuantitaProdotto(prodotto, quantita)){
            return false;
        }
        if(modifiche.isEmpty()) {
            Richiesta richiesta = new Richiesta(prodotto, quantita);
            richiesta.setPrezzo(prodotto.getPrezzo()*quantita);
            richieste.add(richiesta);
        }
        richieste.add(new Richiesta(prodotto, quantita, modifiche));
        return true;
    }

    public boolean decrementaProdotto(Richiesta richiesta, int quantita){
        boolean remove = false;
        for(Richiesta richiestaList: richieste){
            if(richiestaList.equals(richiesta)){
                if(richiestaList.getQuantita() == quantita){
                    remove = true;
                    break;
                }
                if(richiestaList.getQuantita() > quantita){
                    richiestaList.setQuantita(richiestaList.getQuantita()-quantita);
                    richiestaList.setPrezzo(this.ricalcoloPrezzoRichiesta(richiestaList));
                }
                this.gestoreProdotti.incrementoQuantitaProdotto(richiestaList.getProdotto(), quantita);
                return true;
            }
            break;
        }
        if(remove) this.richieste.remove(richiesta);
        return false;
    }

    public Ordinazione confirmOrdinazione(Ombrellone ombrellone){
        return this.gestoreOrdinazione.creaOrdinazione(richieste, ombrellone);
    }

    private double ricalcoloPrezzoRichiesta(Richiesta richiesta){
        return gestoreProdotti.getProdottoOf(richiesta).getPrezzo() * richiesta.getQuantita();
    }


}
