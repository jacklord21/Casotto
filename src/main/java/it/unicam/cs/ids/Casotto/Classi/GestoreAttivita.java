package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.AccountRepository;
import it.unicam.cs.ids.Casotto.Repository.AttivitaRepository;
import it.unicam.cs.ids.Casotto.Repository.PartecipaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreAttivita {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AttivitaRepository attivitaRepository;

    @Autowired
    PartecipaRepository partecipaRepository;

    @Autowired
    GestoreAccount gestoreAccount;

    public List<Attivita> getAllAttivitaOf(Account account){
        return partecipaRepository.findByPartecipanteId(account.getId())
                .stream().map(p -> attivitaRepository.findByPartecipantiId(p.getId()))
                .collect(Collectors.toList());
    }

    public List<Attivita> getAllAttivitaForTodayOf(Account account){
        return this.getAllAttivitaOf(account).stream().filter(a -> a.getData().isEqual(LocalDate.now())).collect(Collectors.toList());
    }

    public List<Attivita> getAllAttivita(){
        return StreamSupport.stream(attivitaRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean thereIsAttivitaForToday() {
        return !this.getAllAttivitaForToday().isEmpty();
    }

    public List<Attivita> getAllAttivitaForToday(){
        return this.getAllAttivitaOf(LocalDate.now());
    }

    public List<Attivita> getAllAttivitaOf(LocalDate data){
        return attivitaRepository.findByData(data);
    }

    public int postiRimanenti(Attivita attivita){
        int postiRimanenti = attivita.getNumeroposti();
        for(Partecipa iscrizione: partecipaRepository.findByAttivitaId(attivita.getId())){
            postiRimanenti -= iscrizione.getNumPartecipanti();
        }
        return postiRimanenti;
    }

    public boolean canPrenotate (int numPartecipanti, Attivita attivita){
        return this.postiRimanenti(attivita) >= numPartecipanti;
    }

    public boolean prenotazione(Account account, int numPartecipanti, Attivita attivita){
        if(!this.canPrenotate(numPartecipanti, attivita)){
            return false;
        }
        Partecipa iscrizione = new Partecipa(numPartecipanti, account, attivita);
        partecipaRepository.save(iscrizione);
        this.gestoreAccount.updateLivelloAccount(account, Livello.PARTECIPANTE);

        return true;
    }

    public boolean cancellaPrenotazione(Account account, Attivita attivita){
        return this.cancellaPrenotazione(partecipaRepository.findByPartecipanteIdAndAttivitaId(account.getId(), attivita.getId()));
    }

    private boolean cancellaPrenotazione(Partecipa iscrizione) {
        Account account = accountRepository.findByIscrizioniId(iscrizione.getId());
        if(!partecipaRepository.existsById(iscrizione.getId())){
            throw new IllegalArgumentException("L'iscrizione passata non esiste");
        }
        if(!this.haveOtherIscrizioniForToday(account)){
            account.setLivello(Livello.CLIENTE);
            accountRepository.save(account);
        }
        partecipaRepository.deleteById(iscrizione.getId());
        return true;
    }

    private boolean haveOtherIscrizioniForToday(Account account) {
        return !this.getAllAttivitaForTodayOf(account).isEmpty();
    }

    public Attivita createAttivita(String nome, LocalDate data, int numPosti) {
        return new Attivita(nome, data, numPosti);
    }

    public boolean modificheAttivita(Attivita attivita, boolean cancella) {
        if(cancella) {
            this.cancellaAttivita(attivita);
            return true;
        }

        this.attivitaRepository.save(attivita);
        return true;
    }

    private void cancellaAttivita(Attivita attivita) {
        if(attivitaRepository.existsById(attivita.getId()))
            attivitaRepository.deleteById(attivita.getId());
    }
}
