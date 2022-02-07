package it.unicam.cs.ids.Casotto.Classi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreAcquisto {

    @Autowired
    private GestoreProdotti gestoreProdotti;

    private final List<Richiesta> richieste;

    @Autowired
    private GestoreOrdinazione gestoreOrdinazione;

    public GestoreAcquisto() {
        this.richieste = new ArrayList<>();
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

    public List<Richiesta> getAllRichieste(){
        return this.richieste;
    }

    public boolean cancellaRichiesta(Richiesta richiesta) {
        return this.richieste.remove(richiesta);
    }

    public void cancellaAcquisto(){
        this.richieste.clear();
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
}