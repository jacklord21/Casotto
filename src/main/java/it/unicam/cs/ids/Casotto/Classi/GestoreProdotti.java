package it.unicam.cs.ids2122.Casotto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestoreProdotti {

    @Autowired
    ProdottoRepository prodottoRepository;

    public List<Prodotto> getProdottiOf(List<Richiesta> richieste){
        List<Prodotto> prodotti = new ArrayList<>();
        for(Richiesta richiesta: richieste){
            prodotti.add(prodottoRepository.findByRichiesteId(richiesta.getId()));
        }
        return prodotti;
    }

    public Prodotto getProdottoOf(Richiesta richiesta){
        return prodottoRepository.findByRichiesteId(richiesta.getId());
    }

    public void incrementoQuantitaProdotto(Prodotto prodotto, int quantita){
        this.modificaQuantitaProdotto(prodotto, quantita);
    }

    public boolean decrementoQuantitaProdotto(Prodotto prodotto, int quantita){
        return this.modificaQuantitaProdotto(prodotto, quantita * -1);
    }

    private boolean modificaQuantitaProdotto(Prodotto prodotto, int quantita){
        this.checkIsNull(prodotto, quantita);
        if(!prodottoRepository.existsById(prodotto.getId())){
            throw new IllegalArgumentException("Il prodotto passato non esiste");
        }
        if(!this.isPresent(prodotto, quantita)){
            return false;
        }
        prodotto.setQuantita(prodotto.getQuantita()+quantita);
        prodottoRepository.save(prodotto);
        return true;
    }

    public boolean aggiuntaProdotto(String oggetto, long prezzo, int quantita, Tipo tipo){
        this.checkIsNull(oggetto, prezzo, quantita);
        if(prodottoRepository.existsByOggetto(oggetto)){
            return false;
        }
        Prodotto prodotto = new Prodotto(oggetto, prezzo, quantita, tipo);
        prodottoRepository.save(prodotto);
        return true;
    }

    public boolean rimozioneProdotto(Prodotto prodotto){
        this.checkIsNull(prodotto);
        if(!prodottoRepository.existsById(prodotto.getId())){
            return false;
        }
        prodottoRepository.deleteById(prodotto.getId());
        return true;
    }

    public List<Prodotto> getBevande(){
        return prodottoRepository.findByTipo(Tipo.BEVANDE);
    }

    public List<Prodotto> getCibo(){
        return prodottoRepository.findByTipo(Tipo.CIBO);
    }

    public List<Prodotto> getAll(){
        List<Prodotto> lista = this.getBevande();
        lista.addAll(this.getCibo());
        return lista;
    }

    public boolean isPresent(Prodotto prodotto, int quantita){
        this.checkIsNull(prodotto, quantita);
        if(!prodottoRepository.existsById(prodotto.getId()) || quantita < 0){
            return false;
        }
        return prodottoRepository.findById(prodotto.getId()).getQuantita() >= quantita;
    }

    private void checkIsNull(Object ... objects){
        for(Object obj: objects){
            if(obj == null){
                throw new NullPointerException("I paramentri passati sono nulli");
            }
        }
    }
}
