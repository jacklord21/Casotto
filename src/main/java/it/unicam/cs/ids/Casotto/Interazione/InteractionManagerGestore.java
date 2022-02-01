package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Classe che permette di effettuare le operazioni del gestore
 */
@Service
public class InteractionManagerGestore
{
    @Autowired private GestoreAccount gestoreAccount;
    @Autowired private GestoreAttivita gestoreAttivita;
    @Autowired private GestoreProdotti gestoreProdotti;
    @Autowired private GestoreNotifiche gestoreNotifiche;
    @Autowired private Spiaggia spiaggia;


    /**
     * Metodo che permette di modificare il {@link Livello} di un'{@link Account}
     *
     * Il gestore seleziona l'{@link Account} del quale modificare il {@link Livello}, inserendo poi il nuovo {@link Livello}
     *
     * Se non risultano presenti {@link Account} dei quali modificare il {@link Livello},
     * quest'operazione non &egrave; possibile
     */
    public void modificaLivello() {
        List<Account> accounts = this.gestoreAccount.getAllAccount();

        if(accounts.isEmpty()) {
            System.out.println("AVVISO] Nessun account presente nel sistema.");
            return;
        }

        Account a = Acquisizione.scelta(accounts, ac->String.valueOf(ac.getId()), ac-> System.out.println(ac.toString()),
                "Seleziona l'account di cui modificare il livello", "Errore: scelta NON valida. Riprova");

        this.gestoreAccount.updateLivelloAccount(a, Acquisizione.acqLivello("dell'account"));
        System.out.println("Il livello dell'account con id '" + a.getId() + "' e' stato modificato correttamente.");
    }

    /**
     * Metodo che permette di gestire la struttura
     *
     * Il gestore seleziona cosa vuole gestire, se i {@link Prodotto} o le {@link Attivita}
     */
    public void gestioneStruttura() {

        List<String> opzioniGestione = List.of("prodotti", "attivita");
        String sceltaGestione = Acquisizione.scelta(opzioniGestione, s->String.valueOf(s.charAt(0)), System.out::println,
                "\nSeleziona cosa desideri gestire (p per prodotti, a per attivita')", "Errore: opzione NON valida. Riprova.");

        if(Objects.equals(sceltaGestione, "prodotti")) this.gestioneProdotti();
        else this.gestioneAttivita();
    }

    private void gestioneAttivita() {
        this.gestione(()->Acquisizione.acqParamAttivita(this.gestoreAttivita),
                this::selezioneAttivitaDaRimuovere,
                this::selezionaAttivitaDaModificare,
                this.gestoreAttivita::modificheAttivita);
    }

    private void gestioneProdotti() {
        this.gestione(()->Acquisizione.acqParamProdotto(this.gestoreProdotti),
                this::selezioneProdottoDaRimuovere,
                this::selezionaProdottoDaModificare,
                this.gestoreProdotti::modificheProdotti);
    }

    /**
     * Metodo che permette di effettuare la gestione di {@link Prodotto} o {@link Attivita}
     *
     * Il gestore seleziona cosa desidera effettuare, cio&egrave; se aggiungere, rimuovere o modificare. Una
     * volta compiuta l'operazione richiesta ne viene chiesta conferma all'utente, per poter modificare i dati nel
     * database
     *
     * Ci&ograve; avviene finch&egrave; il gestore desidera farlo
     */
    private <T> void gestione(Supplier<Map<T, Boolean>> aggiunta, Supplier<Map<T, Boolean>> rimozione, Supplier<Map<T, Boolean>> modifica, BiConsumer<T, Boolean> funzioneModificaOggetto) {
        boolean flagContinuare = true;

        Map<String, Supplier<Map<T, Boolean>>> mappaGestione = Map.of(
                "aggiungere", aggiunta,
                "rimuovere", rimozione,
                "modificare", modifica
        );

        while(flagContinuare) {
            String sceltaGestione = Acquisizione.scelta(mappaGestione.keySet(), m->m, System.out::println,
                    "Digita cosa desideri fare", "Errore. Opzione non valida. Riprova.");

            Map<T, Boolean> m = mappaGestione.get(sceltaGestione).get();

            if(m!=null) {
                boolean confermaModifiche = Acquisizione.scelta(List.of(true, false), v -> String.valueOf(v.toString().charAt(0)), v -> System.out.println(v.toString()),
                        "\nConfermi di voler " + sceltaGestione + " (t per si'/f per no)?", "Scelta non possibile. Riprova.");

                if (confermaModifiche) {
                    funzioneModificaOggetto.accept(m.entrySet().iterator().next().getKey(), m.entrySet().iterator().next().getValue());
                    System.out.println("\nL'azione di " + sceltaGestione + " e' stata correttamente effettuata.");
                } else System.out.println("\nLe modifiche NON sono state apportate.");
            }
            else System.out.println("\nNon c'e' nulla da " + sceltaGestione);

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la gestione (t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }
    }

    private Map<Attivita, Boolean> selezioneAttivitaDaRimuovere() {
        List<Attivita> attivita = this.gestoreAttivita.getAllAttivita();
        return this.selezionaOggettoDaRimuovere(attivita, attivita.stream().map(Attivita::getId).collect(Collectors.toList()));
    }

    private Map<Prodotto, Boolean> selezioneProdottoDaRimuovere() {
        List<Prodotto> prodotti = this.gestoreProdotti.getAll();
        return this.selezionaOggettoDaRimuovere(prodotti, prodotti.stream().sequential().map(Prodotto::getId).collect(Collectors.toList()));
    }

    private <T> Map<T, Boolean> selezionaOggettoDaRimuovere(List<T> oggetti, List<Long> indici) {
        if(oggetti.isEmpty()) return null;

        return Map.of(Acquisizione.scelta(oggetti, a->String.valueOf(indici.remove(0)), a-> System.out.println(a.toString()), "Seleziona l'attivita' da rimuovere: ",
                "Errore: scelta NON valida. Riprova."), true);
    }

    /**
     * Metodo che permette di modificare un'{@link Attivita}
     *
     * Il gestore seleziona il dato dell'{@link Attivita} che desidera modificare, inserendo il nuovo valore. Al termine,
     * i cambiamenti vengono scritti sul database
     *
     * Ci&ograve; avviene finch&egrave; il gestore desidera farlo
     */
    private Map<Attivita, Boolean> selezionaAttivitaDaModificare() {
        boolean flagContinuare = true;
        List<Attivita> attivita = this.gestoreAttivita.getAllAttivita();

        if(attivita.isEmpty()) return null;

        Attivita daModificare = Acquisizione.scelta(attivita, a->String.valueOf(a.getId()), a-> System.out.println(a.toString()), "Seleziona cio' che vuoi rimuovere: ",
                "Errore: scelta NON valida. Riprova.");

        Object[] dati = new Object[3];

        while(flagContinuare) {
            Map<String, Supplier<Object>> datiAttivita = Map.of(
                    "nome", ()->dati[0]=Acquisizione.acqStringa("il nome dell'attivita'", false),
                    "data",  ()->dati[1]=Acquisizione.acqData("la data dell'attivita'", false),
                    "numero_posti", ()->dati[2]=Acquisizione.acqIntero("il numero di posti dell'attivita'", false));

            String sceltaDatoDaModificare = Acquisizione.scelta(datiAttivita.keySet(), k->k, System.out::println,
                    "\nSeleziona il dato da modificare: ", "Attenzione: scelta NON prevista. Riprovare.");

            datiAttivita.get(sceltaDatoDaModificare).get();

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la modifica dei dati dell'attivita'(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
        }

        daModificare.setNome( (dati[0]==null) ? daModificare.getNome() : (String)dati[0] );
        daModificare.setData( (dati[1]==null) ? daModificare.getData() : (LocalDate) dati[1] );
        daModificare.setNumeroposti( (dati[2]==null) ? daModificare.getNumeroposti() : (Integer)dati[2] );

        return Map.of(daModificare, false);
    }

    /**
     * Metodo che permette di modificare un {@link Prodotto}
     *
     * Il gestore seleziona il dato del {@link Prodotto} che desidera modificare, inserendo il nuovo valore. Al termine,
     * i cambiamenti vengono scritti sul database
     *
     * Ci&ograve; avviene finch&egrave; il gestore desidera farlo
     */
    private Map<Prodotto, Boolean> selezionaProdottoDaModificare() {
        boolean flagContinuare = true;
        List<Prodotto> prodotti = this.gestoreProdotti.getAll();

        if(prodotti.isEmpty()) return null;

        Prodotto daModificare = Acquisizione.scelta(prodotti, a->String.valueOf(a.getId()), a-> System.out.println(a.toString()),
                "Seleziona cio' che vuoi rimuovere", "Errore: scelta NON valida. Riprova.");

        Object[] dati = new Object[4];

        while(flagContinuare) {
            Map<String, Supplier<Object>> datiProdotto = Map.of(
                    "nome", ()->dati[0]=Acquisizione.acqStringa("il nome del prodotto", false),
                    "quantita",  ()->dati[1]=Acquisizione.acqIntero("la quantita' del prodotto", false),
                    "prezzo", ()->dati[2]=Acquisizione.acqDouble("il prezzo del prodotto", false),
                    "tipo", ()->dati[3]=Acquisizione.acqTipo("del prodotto"));

            String sceltaDatoDaModificare = Acquisizione.scelta(datiProdotto.keySet(), k->k, System.out::println,
                    "\nSeleziona il dato da modificare: ", "Attenzione: scelta NON prevista. Riprovare.");

            datiProdotto.get(sceltaDatoDaModificare).get();

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la modifica dei dati del prodotto(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
        }

        daModificare.setOggetto( (dati[0]==null) ? daModificare.getOggetto() : (String)dati[0] );
        daModificare.setQuantita( (dati[1]==null) ? daModificare.getQuantita() : (int) dati[1] );
        daModificare.setPrezzo( (dati[2]==null) ? daModificare.getPrezzo() : (double)dati[2] );
        daModificare.setTipo( (dati[3]==null) ? daModificare.getTipo() : (Tipo) dati[3] );

        return Map.of(daModificare, false);
    }

    /**
     * Metodo che permette di inviare una {@link Notifica}
     *
     * Il gestore scrive il testo della {@link Notifica} e la {@link LocalDate} di fine validit&agrave;, per poi selezionare
     * (finch&egrave; desidera farlo) i {@link Livello} a cui associare la notifica. Al termine della selezione,
     * se il gestore conferma, la notifica viene inviata agli {@link Account} con {@link Livello} pari a quelli indicati
     */
    public void invioNotifiche() {
        String testoNotifica = Acquisizione.acqStringa("il testo della notifica", false);
        LocalDate dataFine = Acquisizione.acqData("di fine validita' della notifica", false);


        boolean flagContinuare = true;
        List<Livello> gruppi = new ArrayList<>();

        while(flagContinuare) {
            gruppi.add(Acquisizione.acqLivello("della notifica"));

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la selezione dei gruppi ai quali inviare la notifica(t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }

        boolean confermaInvio = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                "\nConfermi l'invio (t per si'/f per no)? : ", "Scelta non possibile. Riprova.");

        if(confermaInvio)  this.gestoreNotifiche.invioNotifica(testoNotifica, gruppi, dataFine);
        else System.out.println("La notifica NON e' stata inviata.");
    }

    /**
     * Metodo che permette di inserire un {@link Prezzo}
     *
     * Il gestore inserisce tutti i parametri del {@link Prezzo}, per poi selezionare un {@link Ombrellone}
     * al quale associarlo (ci&ograve; finch&egrave; il gestore desidera farlo)
     */
    public void inserisciPrezzo() {
        boolean flagContinuare = true;

        List<Ombrellone> ombrelloni = this.spiaggia.getAllOmbrelloni();
        Prezzo prezzo = Acquisizione.acqParamPrezzo(this.spiaggia);

        while(flagContinuare) {

            Ombrellone ombrellone = Acquisizione.scelta(ombrelloni, o->String.valueOf(o.getId()), o-> System.out.println(o.toString()),
                    "Seleziona un'ombrellone al quale associare il prezzo", "Errore: scelta NON valida. Riprova.");

            this.spiaggia.associaPrezzo(prezzo, ombrellone);

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v->{},
                    "\nDesideri continuare con la selezione degli ombrelloni ai quali associare il prezzo(t per si'/f per no)?", "Scelta non possibile. Riprova.");

        }
    }
}