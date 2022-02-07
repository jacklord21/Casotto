package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.NotificaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe che rappresenta un gestore di {@link Notifica}
 *
 */
@Service
@SuppressWarnings("UnusedReturnValue")
public class GestoreNotifiche {

    @Autowired
    NotificaRepository notificaRepository;

    /**
     * Restituisce tutte le {@link Notifica} destinate al {@link Livello} indicato. All'inizio elimina automaticamente
     * le {@link Notifica}, associate al {@link Livello} indicato, la cui data di fine validit&agrave; &egrave; stata
     * raggiunta
     *
     * @param gruppo {@link Livello} del quale estrarre le {@link Notifica}
     * @return una {@link List} contenente tutte le {@link Notifica} destinate al {@link Livello} indicato, o vuota se
     *         non sono presenti {@link Notifica} per il {@link Livello} indicato
     */
    public List<Notifica> getNotifiche(Livello gruppo) {
        for (Notifica notifica : notificaRepository.findByGruppo(gruppo))
            if (notifica.getDatavalidita()!=null && notifica.getDatavalidita().isBefore(LocalDate.now()))
                this.removeNotifica(notifica);

        return notificaRepository.findByGruppo(gruppo);
    }

    /**
     * Elimina la {@link Notifica} indicata
     *
     * @param notifica {@link Notifica} da eliminare
     * @return se la {@link Notifica} &egrave; stata eliminata, false se si tenta di eliminare una {@link Notifica}
     *         che non esiste
     */
    public boolean removeNotifica(Notifica notifica) {
        if(!notificaRepository.existsById(notifica.getId()))
            return false;

        notificaRepository.deleteById(notifica.getId());
        return true;
    }

    /**
     * Invia una {@link Notifica} ai {@link Livello} indicati
     *
     * @param testo testo della {@link Notifica}
     * @param gruppiInvio {@link Livello} ai quali inviare la notifica
     * @param dataFineValidita data di fine validit&agrave; della notifica
     */
    public void invioNotifica(String testo, List<Livello> gruppiInvio, LocalDate dataFineValidita){
        for(Livello gruppo: gruppiInvio)
            notificaRepository.save(new Notifica(testo, gruppo, dataFineValidita));
    }

    /**
     * Invia un {@link Notifica}, in questo caso un problema, ai {@link Livello} indicati
     *
     * @param testo testo del problema
     */
    public void invioProblema(String testo) {
        notificaRepository.save(new Notifica(testo, Livello.GESTORE));
    }
}
