package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@SuppressWarnings( {"UnusedReturnValue", "BooleanMethodIsAlwaysInverted"} )
public class GestoreProdotti {

    @Autowired
    private ProdottoRepository prodottoRepository;

    public int getQuantitaOf(String oggetto) {
        return prodottoRepository.findByOggetto(oggetto).getQuantita();
    }

    public double getPrezzoOf(String oggetto) {
        return prodottoRepository.findByOggetto(oggetto).getPrezzo();
    }

    public Prodotto getProdottoOf(Richiesta richiesta){
        return prodottoRepository.findByRichiesteId(richiesta.getId());
    }

    public void decrementoQuantitaProdotto(Prodotto prodotto, int quantita){
        this.modificaQuantitaProdotto(prodotto, quantita * -1);
    }

    private void modificaQuantitaProdotto(Prodotto prodotto, int quantita){
        this.checkIsNull(prodotto, quantita);
        if(!prodottoRepository.existsById(prodotto.getId())){
            throw new IllegalArgumentException("Il prodotto passato non esiste");
        }
        if(!this.isPresent(prodotto, quantita)){
            return;
        }
        prodotto.setQuantita(prodotto.getQuantita()+quantita);
        prodottoRepository.save(prodotto);
    }

    public Prodotto creazioneProdotto(String oggetto, double prezzo, int quantita, Tipo tipo){
        this.checkIsNull(oggetto, prezzo, quantita);
        return new Prodotto(oggetto, prezzo, quantita, tipo);
    }

    private void cancellaProdotto(Prodotto prodotto){
        this.checkIsNull(prodotto);
        if(!prodottoRepository.existsById(prodotto.getId())){
            return;
        }
        prodottoRepository.deleteById(prodotto.getId());
    }

    public boolean modificheProdotti(Prodotto prodotto, boolean cancella) {
        if(cancella) {
            this.cancellaProdotto(prodotto);
            return true;
        }

        this.prodottoRepository.save(prodotto);
        return true;
    }

    public List<Prodotto> getBevande(){
        return prodottoRepository.findByTipo(Tipo.BEVANDE);
    }

    public List<Prodotto> getCibo(){
        return prodottoRepository.findByTipo(Tipo.CIBO);
    }

    public List<Prodotto> getAllAlimenti(){
        List<Prodotto> lista = this.getBevande();
        lista.addAll(this.getCibo());
        return lista;
    }

    public List<Prodotto> getAll(){
        return StreamSupport.stream(prodottoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean isPresent(Prodotto prodotto, int quantita){
        this.checkIsNull(prodotto, quantita);
        if(!prodottoRepository.existsById(prodotto.getId()) || quantita < 0){
            return false;
        }
        return prodottoRepository.findById(prodotto.getId()).getQuantita() >= quantita;
    }

    private void checkIsNull(Object ... objects) {
        for(Object obj: objects){
            if(obj == null){
                throw new NullPointerException("I parametri passati sono nulli");
            }
        }
    }
}
