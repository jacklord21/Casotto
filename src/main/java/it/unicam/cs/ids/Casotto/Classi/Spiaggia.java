package it.unicam.cs.ids.Casotto.Classi;

import it.unicam.cs.ids.Casotto.Repository.OmbrelloneRepository;
import it.unicam.cs.ids.Casotto.Repository.PrenotazioniRepository;
import it.unicam.cs.ids.Casotto.Repository.PrezzoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Classe che rappresenta un gestore spiaggia, che permette di effettuare le operazioni
 * connesse
 *
 */
@Service
public class Spiaggia {

    @Autowired
    OmbrelloneRepository ombrelloneRepository;

    @Autowired
    PrezzoRepository prezzoRepository;

    @Autowired
    PrenotazioniRepository prenotazioneRepository;

    @Autowired
    GestoreProdotti gestoreProdotti;


    /**
     * Metodo che calcola il prezzo totale di una prenotazione in base al prezzo dell'{@link Ombrellone} e alla
     * quantit&agrave; di sdraie e lettini presenti nella prenotazione
     *
     * @param prenotazione {@link Prenotazione} della quale calcolare il prezzo totale
     *
     * @throws NullPointerException se la {@link Prenotazione} passata &egrave; nulla
     *
     * @return il prezzo totale della prenotazione
     */
    public double getPrezzoTotale(Prenotazione prenotazione) {

        if(Objects.isNull(prenotazione)) throw new NullPointerException("La prenotazione passata è nulla");

        double prezzoFinale = 0.0;
        for(Ombrellone ombrellone: prenotazione.getOmbrelloni())
            prezzoFinale += this.getPrezzoOmbrellone(ombrellone, prenotazione.getDataPrenotazione(), prenotazione.getDurata()).getPrezzo();

        prezzoFinale += this.gestoreProdotti.getPrezzoOf("lettini") * prenotazione.getLettini();
        prezzoFinale += this.gestoreProdotti.getPrezzoOf("sdraie") * prenotazione.getSdraie();
        return prezzoFinale;
    }

    /**
     * Metodo che permette di ottenere il {@link Prezzo} di un {@link Ombrellone} in una determinata data (passata come
     * parametro). Il {@link Prezzo} viene cercato in base alla data intera e, se non viene trovato, viene cercato
     * solamente in base al mese
     *
     * @param ombrellone {@link Ombrellone} del quel cercare il {@link Prezzo}
     * @param dataPrenotazione data in cui si vuole effettuare la {@link Prenotazione}
     * @param durata {@link Durata} temporale della prenotazione
     * @return  il prezzo dell'{@link Ombrellone} passato
     */
    public Prezzo getPrezzoOmbrellone(Ombrellone ombrellone, LocalDate dataPrenotazione, Durata durata) {
        Optional<Prezzo> prezzo = this.checkDataCorrente(prezzoRepository.findByOmbrelloniId(ombrellone.getId()), dataPrenotazione, durata);
        if(prezzo.isPresent()) return prezzo.get();

        prezzo = this.checkMeseCorrente(prezzoRepository.findByOmbrelloniId(ombrellone.getId()), dataPrenotazione, durata);
        return prezzo.orElse(null);
    }

    /**
     * Metodo che permette di conoscere gli ombrelloni liberi nella data indicata e per una certa {@link Durata} temporale
     *
     * @param dataPrenotazione data nella quale si vogliono conoscere gli ombrelloni liberi
     * @param durata {@link Durata} temporale della prenotazione
     * @return una {@link List} contenente gli ombrelloni che risultano liberi in base ai parametri passati
     */
    public List<Ombrellone> getOmbrelloniLiberi(LocalDate dataPrenotazione, Durata durata, int numPersone){
        List<Ombrellone> ombrelloni = ombrelloneRepository.findAll();
        ombrelloni.removeIf(ombrellone -> this.notIsFree(ombrellone, dataPrenotazione, durata));
        return ombrelloni;
    }

    /**
     * Verifica se un {@link Ombrellone} &egrave; disponibile nella data indicata, in base alla {@link Durata} temporale
     *
     * @param ombrellone {@link Ombrellone} del quale verificare la disponibilit&agrave;
     * @param dataPrenotazione data nella quale verificare la disponibilit&agrave; dell'{@link Ombrellone}
     * @param durata durata temporale per la quale verificare la disponibilit&agrave; dell'{@link Ombrellone}
     *
     * @throws NullPointerException se almeno UNO dei parametri passati &egrave; nullo
     *
     * @return true se l'ombrellone &egrave; disponibile, false altrimenti
     */
    public boolean notIsFree(Ombrellone ombrellone, LocalDate dataPrenotazione, Durata durata){
        if(ombrellone == null || dataPrenotazione == null || durata == null){
            throw new NullPointerException("I parametri passati sono nulli");
        }
        for (Prenotazione prenotazione: prenotazioneRepository.findByOmbrelloniIdAndDataPrenotazione(ombrellone.getId(), dataPrenotazione)){
            if(prenotazione.getDurata() == Durata.INTERO || (durata==Durata.INTERO && (prenotazione.getDurata()==Durata.MATTINO || prenotazione.getDurata()==Durata.POMERIGGIO)))
                return true;
/*            else if (prenotazione.getDurata()==Durata.MATTINO && durata==Durata.INTERO)
                return true;
            else if (prenotazione.getDurata()==Durata.POMERIGGIO && durata==Durata.INTERO)
                return true;*/
        }
        return false;
    }

    /**
     * Metodo che calcola il numero di lettini ancora disponibili. Si estrae da ogni prenotazione (con data uguale a
     * quella passata come parametro e con {@link Durata} pari a 'INTERO' o alla durata passata come parametro) il
     * numero di lettini associati, che vengono sommati in un accumulatore. La quantit&agrave; ottenuta viene sottratta
     * dal totale delle lettini disponibili
     *
     * @param dataPrenotazione data della prenotazione
     * @param durata durata della prenotazione
     * @return il numero di lettini disponibili
     */
    public int lettiniDisponibili(LocalDate dataPrenotazione, Durata durata){
        int oggettiOccupati = 0;
        int quantitaTotale = this.gestoreProdotti.getQuantitaOf("lettini");

        for(Prenotazione prenotazione: prenotazioneRepository.findByDataPrenotazione(dataPrenotazione)){
            if(prenotazione.getDurata() == Durata.INTERO || prenotazione.getDurata() == durata){
                oggettiOccupati += prenotazione.getLettini();
            }
        }
        return quantitaTotale-oggettiOccupati;
    }

    /**
     * Metodo che calcola il numero di sdraie ancora disponibili. Si estrae da ogni prenotazione (con data uguale a
     * quella passata come parametro e con {@link Durata} pari a 'INTERO' o alla durata passata come parametro) il
     * numero di sdraie associate, che vengono sommate in un accumulatore. La quantit&agrave; ottenuta viene sottratta
     * dal totale delle sdraie disponibili
     *
     * @param dataPrenotazione data della prenotazione
     * @param durata durata della prenotazione
     * @return il numero di sdraie disponibili
     */
    public int sdraieDisponibili(LocalDate dataPrenotazione, Durata durata) {
        int oggettiOccupati = 0;
        int quantitaTotale = this.gestoreProdotti.getQuantitaOf("sdraie");

        for(Prenotazione prenotazione: prenotazioneRepository.findByDataPrenotazione(dataPrenotazione)){
            if(prenotazione.getDurata() == Durata.INTERO || prenotazione.getDurata() == durata){
                oggettiOccupati += prenotazione.getSdraie();
            }
        }
        return quantitaTotale-oggettiOccupati;
    }

    public List<Ombrellone> getAllOmbrelloni() {
        return this.ombrelloneRepository.findAll();
    }

    private Optional<Prezzo> checkDataCorrente(List<Prezzo> prezzi, LocalDate dataPrenotazione, Durata durata){
        return prezzi.stream()
                .filter(p -> p.getDataInizio() != null && p.getDataFine() != null)
                .filter(p -> p.getDataInizio().compareTo(dataPrenotazione) <= 0 &&
                        p.getDataFine().compareTo(dataPrenotazione) >= 0)
                .filter(p -> this.checkDurata(p, durata)).findFirst();
    }

    private Optional<Prezzo> checkMeseCorrente(List<Prezzo> prezzi, LocalDate dataPrenotazione, Durata durata){
        return prezzi.stream()
                .filter(p -> p.getMeseInizio() != 0 && p.getMeseFine() != 0)
                .filter(p -> p.getMeseInizio() <= dataPrenotazione.getMonthValue() &&
                        p.getMeseFine() >= dataPrenotazione.getMonthValue())
                .filter(p -> this.checkDurata(p, durata)).findFirst();
    }

    private boolean checkDurata(Prezzo prezzo, Durata durata){
        return (prezzo.getDurata().equals(Durata.INTERO) || prezzo.getDurata().equals(durata));
    }
}