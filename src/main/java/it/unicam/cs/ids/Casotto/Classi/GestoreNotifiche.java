package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.NotificaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestoreNotifiche {

    @Autowired
    NotificaRepository notificaRepository;

    public List<Notifica> getNotifiche(Livello gruppo){
        if(gruppo == Livello.GESTORE)
            for (Notifica notifica : notificaRepository.findAll())
                if (notifica.getDatavalidita().isBefore(LocalDate.now()))
                    this.removeNotifica(notifica);

        return notificaRepository.findByGruppo(gruppo)
                .stream().filter(n -> n.getDatavalidita().compareTo(LocalDate.now()) >= 0)
                .collect(Collectors.toList());
    }

    public boolean removeNotifica(Notifica notifica){
        if(!notificaRepository.existsById(notifica.getId())){
            return false;
        }
        notificaRepository.deleteById(notifica.getId());
        return true;
    }

    public boolean invioNotificha(String testo, List<Livello> gruppiInvio, LocalDate dataFineValidita){
        for(Livello gruppo: gruppiInvio){
            notificaRepository.save(new Notifica(testo, gruppo, dataFineValidita));
        }
        return true;
    }

    public boolean invioProblema(String testo){
        notificaRepository.save(new Notifica(testo, Livello.GESTORE));
        return true;
    }
}
