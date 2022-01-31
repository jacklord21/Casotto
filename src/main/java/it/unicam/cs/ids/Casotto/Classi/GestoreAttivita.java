package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.AccountRepository;
import it.unicam.cs.ids.Casotto.Repository.AttivitaRepository;
import it.unicam.cs.ids.Casotto.Repository.PartecipaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GestoreAttivita {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AttivitaRepository attivitaRepository;

    @Autowired
    PartecipaRepository partecipaRepository;

    @Autowired
    GestoreAccount gestoreAccount;


    public boolean cambiaNomeAttivita(Attivita attivita, String nome) {
        Objects.requireNonNull(attivita); Objects.requireNonNull(nome);
        if(!this.attivitaRepository.existsById(attivita.getId()))
            return false;

        attivita.setNome(nome);
        this.attivitaRepository.save(attivita);
        return true;
    }

    public boolean cambiaDataAttivita(Attivita attivita, LocalDate data) {
        Objects.requireNonNull(attivita); Objects.requireNonNull(data);
        if(!this.attivitaRepository.existsById(attivita.getId()))
            return false;

        attivita.setData(data);
        this.attivitaRepository.save(attivita);
        return true;
    }

    public boolean cambiaNumeroPartecipantiAttivita(Attivita attivita, int partecipanti) {
        Objects.requireNonNull(attivita);
        if(!this.attivitaRepository.existsById(attivita.getId()))
            return false;

        attivita.setNumeroposti(partecipanti);
        this.attivitaRepository.save(attivita);
        return true;
    }

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

    public List<Account> getAccountIscrittiTo(Attivita attivita){
        return partecipaRepository.findByAttivitaId(attivita.getId())
                .stream().map(iscr -> accountRepository.findByIscrizioniId(iscr.getId()))
                .collect(Collectors.toList());
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

    private boolean haveOtherIscrizioniForToday(Account account){
        return !this.getAllAttivitaForTodayOf(account).isEmpty();
    }

    public boolean decrementaNumPartecipanti(Partecipa iscrizione, int partecipantiDaTogliere){
        if(!partecipaRepository.existsById(iscrizione.getId())){
            throw new IllegalArgumentException("L'iscrizone passata non esiste");
        }
        if(iscrizione.getNumPartecipanti() <= partecipantiDaTogliere){
            return this.cancellaPrenotazione(iscrizione);
        }
        iscrizione.setNumPartecipanti(iscrizione.getNumPartecipanti() - partecipantiDaTogliere);
        partecipaRepository.save(iscrizione);
        return true;
    }

    public boolean incrementaNumPartecipanti(Partecipa iscrizione, int partecipantiDaAggiungere){
        if(!partecipaRepository.existsById(iscrizione.getId())){
            throw new IllegalArgumentException("L'iscrizone passata non esiste");
        }
        if(!this.canPrenotate(partecipantiDaAggiungere, attivitaRepository.findByPartecipantiId(iscrizione.getId()))){
            return false;
        }
        iscrizione.setNumPartecipanti(iscrizione.getNumPartecipanti() + partecipantiDaAggiungere);
        partecipaRepository.save(iscrizione);
        return true;
    }

    public Attivita createAttivita(String nome, LocalDate data, int numPosti){
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

    private void cancellaAttivita(Attivita attivita){
        if(attivitaRepository.existsById(attivita.getId())){
            attivitaRepository.deleteById(attivita.getId());
        }
    }
}
