package it.unicam.cs.ids2122.Casotto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestoreOrdinazione {

    @Autowired
    OrdinazioneRepository ordinazioneRepository;

    @Autowired
    RichiestaRepository richiestaRepository;

    public List<Richiesta> getRichiesteOf(Ordinazione ordinazione){
        this.checkIsNull(ordinazione);
        if(!ordinazioneRepository.existsById(ordinazione.getId())){
            throw new IllegalArgumentException("L'ordinazione passata non esiste");
        }
        return richiestaRepository.findByOrdinazioneId(ordinazione.getId());
    }

    public Ordinazione creaOrdinazione(List<Richiesta> richieste, Ombrellone ombrellone){
        this.checkIsNull(richieste, ombrellone);
        Ordinazione ordinazione = new Ordinazione(ombrellone);
        if(!this.needImpostaPrezzo(richieste)){
            ordinazione.setPrezzoTot(this.getPrezzoTotaleRichieste(richieste));
        }
        ordinazioneRepository.save(ordinazione);
        for(Richiesta richiesta: richieste){
            richiesta.setOrdinazione(ordinazione);
        }
        richiestaRepository.saveAll(richieste);
        return ordinazione;
    }

    public boolean annullaOrdinazione(Ordinazione ordinazione){
        this.checkIsNull(ordinazione);
        if(!ordinazioneRepository.existsById(ordinazione.getId())){
            return false;
        }
        ordinazioneRepository.deleteById(ordinazione.getId());
        return true;
    }

    public boolean impostaPrezzoRichiesta(Richiesta richiesta, double prezzo){
        this.checkIsNull(richiesta, prezzo);
        if(!richiestaRepository.existsById(richiesta.getId())){
            throw new IllegalArgumentException("I parametri passati non esistono");
        }
        richiesta.setPrezzo(prezzo);
        richiestaRepository.save(richiesta);
        return true;
    }

    public double ricalcolaPrezzoFinale(Ordinazione ordinazione){
        this.checkIsNull(ordinazione);
        if(ordinazione.getPrezzoTot() != 0){
            return ordinazione.getPrezzoTot();
        }
        return this.getPrezzoTotaleRichieste(richiestaRepository.findByOrdinazioneId(ordinazione.getId()));
    }

    public List<Richiesta> listaRichiesteConModifiche(Ordinazione ordinazione){
        List<Richiesta> richieste = new ArrayList<>();
        for(Richiesta richiesta: richiestaRepository.findByOrdinazioneId(ordinazione.getId())){
            if(!richiesta.getModifiche().isEmpty()){
                richieste.add(richiesta);
            }
        }
        return richieste;
    }

    public void setStato(Ordinazione ordinazione, Stato stato){
        this.checkIsNull(ordinazione, stato);
        if(!ordinazioneRepository.existsById(ordinazione.getId())){
            throw new IllegalArgumentException("L'ordinazione passata non esiste");
        }
        ordinazione.setStato(stato);
        ordinazioneRepository.save(ordinazione);
    }

    public boolean needImpostaPrezzo(List<Richiesta> richieste){
        this.checkIsNull(richieste);
        for(Richiesta richiesta: richieste){
            if(richiesta.getPrezzo() == 0){
                return true;
            }
        }
        return false;
    }

    private double getPrezzoTotaleRichieste(List<Richiesta> richieste){
        double prezzoFinale = 0;
        for(Richiesta richiesta: richieste){
            prezzoFinale+=richiesta.getPrezzo();
        }
        return prezzoFinale;
    }

    private void checkIsNull(Object ... objects){
        for(Object obj: objects){
            if(obj == null){
                throw new NullPointerException("I paramentri passati sono nulli");
            }
        }
    }

}
