package it.unicam.cs.ids.Casotto.Classi;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        if(!gestoreProdotti.isPresent(prodotto, quantita)){
            return false;
        }
        Richiesta richiesta = new Richiesta(prodotto, quantita, modifiche);
        richiesta.setPrezzo(prodotto.getPrezzo()*quantita);
        richieste.add(richiesta);
        return true;
    }

    public Prodotto getProdottoOf(Richiesta richiesta){
        return this.gestoreProdotti.getProdottoOf(richiesta);
    }

    public List<Richiesta> getAllRichieste(){
        return this.richieste;
    }

    public boolean cancellaRichiesta(Richiesta richiesta){
        return this.richieste.remove(richiesta);
    }

    public void cancellaAcquisto(){
        this.richieste.clear();
    }

    public boolean decrementaProdotto(Richiesta richiesta, int quantita){
        boolean remove = false;
        for(Richiesta richiestaList: richieste){
            if(richiestaList.equals(richiesta)){
                if(richiestaList.getQuantita() <= quantita){
                    remove = true;
                    break;
                }
                if(richiestaList.getQuantita() > quantita){
                    richiestaList.setQuantita(richiestaList.getQuantita() - quantita);
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

    public double getPrezzoTotale(){
        double prezzoTotale = 0;
        for(Richiesta richiesta: this.richieste){
            prezzoTotale += richiesta.getPrezzo();
        }

        return prezzoTotale;
    }

    private double ricalcoloPrezzoRichiesta(Richiesta richiesta){
        return gestoreProdotti.getProdottoOf(richiesta).getPrezzo() * richiesta.getQuantita();
    }

}
