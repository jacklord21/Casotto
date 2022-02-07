package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Classe che permette di effettuare le operazioni del cliente (anche partecipante) e l'operazione di notifica dei
 * problemi da parte del barista
 */
@Controller
public class InteractionManager
{
    @Autowired private GestoreAccount gestoreAccount;
    @Autowired private GestoreAcquisto gestoreAcquisto;
    @Autowired private GestoreAttivita gestoreAttivita;
    @Autowired private GestoreNotifiche gestoreNotifiche;
    @Autowired private GestorePagamenti gestorePagamenti;
    @Autowired private GestorePrenotazioni gestorePrenotazioni;
    @Autowired private GestoreProdotti gestoreProdotti;
    @Autowired private Spiaggia spiaggia;

    private Account account;
    private final Scanner sc;

    public InteractionManager() {
        this.sc = new Scanner(System.in);
    }

    /**
     * Metodo che permette di effettuare la registrazione
     */
    public void registrazione() {
        String nome = Acquisizione.acqStringa("il nome", false),
                cognome = Acquisizione.acqStringa("il cognome", false);
        LocalDate dataNas = Acquisizione.acqData("di nascita", false);

        Utente u = new Utente(nome, cognome, dataNas);
        if(this.gestoreAccount.checkIfUserExists(u)) {
            System.out.println("Impossibile proseguire con la registrazione: l'utente e' gia' presente nel sistema.");
            return;
        }

        String email = Acquisizione.acqStringa("l'email", false),
                psw = Acquisizione.acqStringa("la password", false);

        if(this.gestoreAccount.registration(u, Livello.CLIENTE, email, psw))
            System.out.println("La registrazione e' andata a buon fine. Ora e' possibile eseguire il login.");
    }

    /**
     * Metodo che permette di effettuare il login a un {@link Account}
     */
    public void login() {
        while( (this.account = this.gestoreAccount.login(Acquisizione.acqStringa("email", false),
                Acquisizione.acqStringa("password", false))) == null )
            System.out.println("\nErrore: NON esiste alcun account con le credenziali associate");

        System.out.print("\nBenvenuto, " + this.gestoreAccount.getUtenteOf(this.account).getNome().toUpperCase(Locale.ROOT) + " " + this.gestoreAccount.getUtenteOf(this.account).getCognome().toUpperCase() + "\n");

        this.gestoreNotifiche.getNotifiche(this.account.getLivello()).forEach(n-> System.out.println(n.toString()));
    }

    /**
     * Metodo che permette di effettuare il logout
     */
    public void logout() {
        this.account = null;
        System.out.println("Logout effettuato con successo!");
    }


    /**
     * Metodo che permette di modificare i dati dell'{@link Account}
     *
     * L'utente seleziona il dato che vuole modificare e inserisce il nuovo valore. Successivamente, l'utente decide se
     * continuare con la modifica o meno.
     *
     */
    public void modificaDati() {
        boolean flagContinuare = true;

        List<Map<String, Supplier<Boolean>>> listaDatiAccountDaModificare = List.of(
                Map.of("nome", ()->this.gestoreAccount.changeUserName(this.account, Acquisizione.acqStringa("il nome", false))),
                Map.of("cognome", ()->this.gestoreAccount.changeUserSurname(this.account, Acquisizione.acqStringa("il cognome", false))),
                Map.of("datanascita", ()->this.gestoreAccount.changeUserBirthdayDate(this.account, Acquisizione.acqData("di nascita", false))),
                Map.of("email", ()->this.gestoreAccount.changeAccountEmail(this.account, Acquisizione.acqStringa("l'email", false))),
                Map.of("password", ()->this.gestoreAccount.changePasswordAccount(this.account, Acquisizione.acqStringa("la password", false)))
                );

        while(flagContinuare) {
            Acquisizione.scelta(listaDatiAccountDaModificare, m->m.entrySet().iterator().next().getKey(), m-> System.out.println("" + m.entrySet().iterator().next().getKey()),
                    "\nSeleziona il dato da modificare", "Attenzione: dato inesistente. Riprova.").entrySet().iterator().next().getValue().get();

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v->{},
                    "\nDesideri continuare con la modifica dei dati(t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }
    }

    /**
     * Metodo che permette di effettuare la {@link Prenotazione} della spiaggia
     *
     * L'utente digita il numero di persone, la data per la quale desidera prenotare e la {@link Durata}. Successivamente,
     * sulla base di questi parametri, vengono estratti gli ombrelloni disponibili: se questi NON ci sono (o i parametri
     * inseriti NON sono corretti) la prenotazione NON pu&ograve; essere effettuata.
     * Successivamente l'utente seleziona gli ombrelloni, il numero di sdraie e lettini desiderati e riceve conferma della
     * prenotazione.
     *
     */
    public void prenotaSpiaggia() {
        Set<Ombrellone> ombrelloniScelti = new HashSet<>();

        int numPersone = Acquisizione.acqIntero("il numero di persone", false);
        LocalDate data = Acquisizione.acqData("per la quale desideri prenotare", false);
        Durata durata = Acquisizione.acqDurata("prenotazione");

        if(data.isBefore(LocalDate.now()) || this.gestorePrenotazioni.haPrenotazioni(this.account, data, durata)) {
            System.out.println("AVVISO] Impossibile effettuare una prenotazione.");
            return;
        }

        List<Ombrellone> listaOmbrelloniLiberi = spiaggia.getOmbrelloniLiberi(data, durata);

        if(listaOmbrelloniLiberi.isEmpty() || listaOmbrelloniLiberi.stream().mapToInt(Ombrellone::getNumero).sum()<numPersone) {
            System.out.println("AVVISO] Gli ombrelloni NON possono contenere le persone indicate o non ci sono ombrelloni liberi in base ai dati indicati.");
            return;
        }

        Map<String, Ombrellone> mappaOmbrelloniLiberi = listaOmbrelloniLiberi.stream().collect(Collectors.toMap(om -> String.valueOf(om.getId()), om -> om));

        if(listaOmbrelloniLiberi.stream().anyMatch(o -> o.getNumero() == numPersone))
            mappaOmbrelloniLiberi = listaOmbrelloniLiberi.stream().filter(o -> o.getNumero()==numPersone).collect(Collectors.toMap(om -> String.valueOf(om.getId()), om -> om));

        Map<String, List<Ombrellone>> mappaOmbrelloniLiberiPerFila = mappaOmbrelloniLiberi.values().stream().collect(Collectors.groupingBy(Ombrellone::getFila));

        mappaOmbrelloniLiberiPerFila.keySet().forEach(k->{
            System.out.println("\nFila " + k + "\n");
            mappaOmbrelloniLiberiPerFila.get(k).forEach(o-> System.out.println(o.toString()));
        });

        while(ombrelloniScelti.stream().mapToInt(Ombrellone::getNumero).sum()<numPersone)
            ombrelloniScelti.add(Acquisizione.scelta(listaOmbrelloniLiberi, o->String.valueOf(o.getId()), o->{},
                    "Seleziona un ombrellone (digita l'id)", "Errore: scelta NON valida. Riprovare."));

        int lettiniScelti = Acquisizione.acqLettiniSdraie(spiaggia.lettiniDisponibili(data, durata), "lettini", (scelta, disp) -> scelta<0 || scelta>disp),
                sdraieScelte = Acquisizione.acqLettiniSdraie(spiaggia.sdraieDisponibili(data, durata), "sdraie", (scelta, disp) -> scelta<0 || scelta>disp);

        Prenotazione pren = new Prenotazione(data, durata, lettiniScelti, sdraieScelte, this.account);
        pren.addOmbrelloni(ombrelloniScelti);

        this.gestorePagamenti.pagamentoPrenotazione(pren, this.account);
        System.out.println("Prenotazione effettuata con successo!");
    }

    /**
     * Metodo che permette di cancellare una {@link Prenotazione}
     *
     * Se non risulta presente nessuna {@link Prenotazione} da poter cancellare, quest'operazione non &egrave; possibile.
     */
    public void cancellaPrenotazione() {
        List<Prenotazione> prenotazioni = this.gestorePrenotazioni.getCurrentPrenotazioni(this.account);

        if(prenotazioni.isEmpty()) {
            System.out.println("AVVISO] nessuna prenotazione da cancellare.");
            return;
        }

        Prenotazione prenotazione = Acquisizione.scelta(prenotazioni, p->String.valueOf(p.getId()),
                p-> System.out.println(p.toString()), "Seleziona una prenotazione", "Errore: la prenotazione scelta NON esiste. Riprovare.");

        if(!this.gestorePrenotazioni.cancellazionePrenotazione(prenotazione)) System.out.println("\nAVVISO] Impossibile cancellare la prenotazione.");
        else System.out.println("\nLa prenotazione con id '" + prenotazione.getId() + "' e' stata eliminata correttamente. Il suo saldo e' stato aggiornato.");
    }

    /**
     * Metodo che permette di visualizzare le {@link Prenotazione} passate, cio&egrave; quelle con data
     * precedente rispetto alla data corrente
     */
    public void visualizzaStoricoPrenotazioni() {visualizzaPrenotazioni(this.gestorePrenotazioni::getPrenotazioniHistory);}

    /**
     * Metodo che permette di visualizzare le {@link Prenotazione} valide, cio&egrave; quelle con data
     * successiva rispetto alla data corrente
     */
    public void visualizzaPrenotazioniCorrenti() {visualizzaPrenotazioni(this.gestorePrenotazioni::getCurrentPrenotazioni);}

    private void visualizzaPrenotazioni(Function<Account, List<Prenotazione>> f) {
        f.apply(this.account).forEach(p-> System.out.println(p.toString()));
    }


    /**
     * Metodo che permette di effettuare un'{@link Ordinazione}.
     *
     * Al termine della selezione dei {@link Prodotto}, l'utente pu&ograve; decidere se confermare, modificare
     * (cio&egrave; aggiungere o togliere prodotti) o annullare l'acquisto.
     *
     * Se non si ha una {@link Prenotazione} per il giorno corrente, quest'operazione non &egrave; possibile.
     */
    public void acquistaProdotto() {
        if(this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now())==null) {
            System.err.println("Errore: NON hai nessuna prenotazione per la giornata odierna.");
            return;
        }

        if(selezionaProdotti().equals("e")) {
            System.err.println("AVVISO] Non ci sono prodotti da poter selezionare.");
            return;
        }

        Map<String, Supplier<String>> mappaModificheOrdinazione = Map.of("a", this::selezionaProdotti, "t", this::rimuoviRichieste);

        String sceltaSuOrdinazione;
        List<String> opzioniSuOrdinazione = List.of("confermare", "modificare", "annullare");

        do {
            sceltaSuOrdinazione = Acquisizione.scelta(opzioniSuOrdinazione.stream().map(op->String.valueOf(op.charAt(0))).collect(Collectors.toList()),
                    op->op, op-> {}, "Desidera confermare (c), modificare (m) o annullare (a) l'acquisto (c/m/a)", "Errore: scelta NON è prevista. Riprova: ");

            if (sceltaSuOrdinazione.equals("c")) confermaOrdinazione();
            else if (sceltaSuOrdinazione.equals("a")) annullaAcquisto();
            else {
                System.out.print("Desideri aggiungere o togliere prodotti (a/t): ");
                while (!mappaModificheOrdinazione.containsKey(sceltaSuOrdinazione = sc.next()))
                    System.out.print("Errore: la scelta fatta NON è prevista. Riprova: ");

                sceltaSuOrdinazione = mappaModificheOrdinazione.get(sceltaSuOrdinazione).get();
            }
        } while(sceltaSuOrdinazione.equals("m"));
    }

    /**
     * Metodo che permette di selezionare i {@link Prodotto}
     *
     * L'utente seleziona i {@link Prodotto}, con associata la quantit&agrave; desiderata ed eventuali
     * modifiche, che andranno a comporre un'{@link Ordinazione}.
     * Ci&ograve; viene fatto finch&egrave; l'utente desidera farlo.
     *
     * Se non risultano presenti n&egrave; cibi, n&egrave; bevande, quest'operazione non &egrave; possibile.
     *
     */
    private String selezionaProdotti() {
        boolean flagModifiche, flagContinuare = true;

        while(flagContinuare) {
            List<Prodotto> prodotti = this.gestoreProdotti.getAllAlimenti();

            if(prodotti.stream().allMatch(p->p.getQuantita()==0)) return "e";

            Prodotto prodotto = Acquisizione.scelta(prodotti, p->String.valueOf(p.getId()), p->System.out.println(p.toString()),
                    "\nScegli un prodotto", "Errore: l'id digitato NON corrisponde ad alcun prodotto.");

            int quantita = Acquisizione.acqIntero("la quantita' del prodotto: (MAX. " + prodotto.getQuantita() + ")", false);

            if(!this.gestoreProdotti.isPresent(prodotto, quantita))
                System.out.println("AVVISO] La quantita' digitata NON e' disponibile.");
            else {
                System.out.println("\nIl prodotto da lei selezionato e' disponibile.");
                flagModifiche = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> {},
                        "Vuoi modificare gli ingredienti del prodotto(t per si'/f per no)?", "Scelta non possibile. Riprova.");

                if(flagModifiche) {
                    sc.nextLine();
                    System.out.print("\nDigita le modifiche desiderate: ");
                    this.gestoreAcquisto.addRichiesta(prodotto, quantita, sc.nextLine());
                }
                else this.gestoreAcquisto.addRichiesta(prodotto, quantita, "");

                System.out.println("\nLa sua richiesta e' stata memorizzata correttamente.");
            }

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> {},
                    "Desideri continuare con la selezione dei prodotti(t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }

        double prezzo =  this.gestoreAcquisto.getPrezzoTotale();

        System.out.println("\nGentile cliente, il prezzo totale della sua ordinazione e': " + prezzo +
                " euro (questo potra' variare se sono state richieste delle modifiche ai prodotti.");

        return "m";
    }


    /**
     * Metodo che permette di annullare l'acquisto
     */
    private void annullaAcquisto() {
        this.gestoreAcquisto.cancellaAcquisto();
        System.out.println("\nIl suo acquisto e' stato annullato.");
    }

    /**
     * Metodo che permette di confermare un acquisto
     *
     * L'utente seleziona l'{@link Ombrellone} a cui associarlo. Al termine, verr&agrave; creata un'{@link Ordinazione},
     * la quale avr&agrave; lo stato "DA_PAGARE"
     */
    private void confermaOrdinazione() {
        Set<Ombrellone> ombrelloniPrenotazione = this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now()).getOmbrelloni();
        Ombrellone ombrellone = Acquisizione.scelta(ombrelloniPrenotazione, o->String.valueOf(o.getId()), o-> System.out.println(o.toString()),
                "\nSeleziona un ombrellone da associare alla prenotazione", "L'id selezionato NON e' valido. Riprova.");

        this.gestoreAcquisto.confirmOrdinazione(ombrellone);

        System.out.println("\nIl suo ordine e' stato correttamente registrato.");
    }

    /**
     * Metodo che permette di rimuovere le {@link Richiesta} dall'acquisto.
     * Ci&ograve; viene fatto finch&egrave; l'utente vuole o finch&egrave; non ci sono pi&ugrave; richieste da eliminare.
     *
     */
    private String rimuoviRichieste() {
        boolean flagContinuare = true;

        while(flagContinuare) {
            List<Richiesta> richieste = this.gestoreAcquisto.getAllRichieste();
            List<Integer> listaIndici = IntStream.range(1, richieste.size()+1).boxed().collect(Collectors.toList());

            int indice;
            listaIndici.forEach(i-> System.out.println(richieste.get(i-1) + "\nID per la rimozione della richiesta: " + i));

            while(!listaIndici.contains(indice =  Acquisizione.acqIntero("l'id della richiesta da rimuovere", false)))
                System.out.println("Errore: scelta NON valida. Riprova.");

            this.gestoreAcquisto.cancellaRichiesta(richieste.get(indice-1));

            if(this.gestoreAcquisto.getAllRichieste().isEmpty()) {
                System.out.println("Sono state rimosse tutte le richieste .");
                return "a";
            }
            else System.out.println("La richiesta e' stata correttamente rimossa. " + "Il prezzo attuale e' di " + this.gestoreAcquisto.getPrezzoTotale() + " euro");

            flagContinuare = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v->{},
                    "\nDesideri continuare con la rimozione dei prodotti(t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }
        return "m";
    }

    /**
     * Metodo che permette di prenotare un'{@link Attivita}
     *
     * L'utente seleziona l'{@link Attivita} a cui vuole partecipare, inserendo poi il numero di persone che vogliono partecipare
     *
     * Se non si ha una {@link Prenotazione} per il giorno corrente o non ci sono {@link Attivita} disponibili,
     * quest'operazione non &egrave; possibile.
     *
     */
    public void prenotazioneAttivita() {
        if(this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now())==null || !this.gestoreAttivita.thereIsAttivitaForToday()) {
            System.out.println("AVVISO] Impossibile prenotare un'attivita'.");
            return;
        }

        Attivita attivitaScelta;
        boolean booked = true;

        do{
            if(!booked) System.out.println("Impossibile partecipare all'attività selezionata con il numero di partecipanti digitato.");

            attivitaScelta = Acquisizione.scelta(this.gestoreAttivita.getAllAttivitaForToday(), a->String.valueOf(a.getId()), a-> System.out.println(a.toString()),
                    "Scegli un attivita' a cui desideri partecipare", "Errore: scelta NON valida. Riprova");

        }while( !(booked=this.gestoreAttivita.prenotazione(this.account, Acquisizione.acqIntero("il numero di partecipanti(MAX. " + this.gestoreAttivita.postiRimanenti(attivitaScelta) + ")", false), attivitaScelta)) );

        System.out.println("L'iscrizione all'attivita' '" + attivitaScelta.getNome() + " e' stata correttamente registrata.");
    }

    /**
     * Metodo che permette di cancellare un'{@link Attivita}
     *
     * L'utente seleziona l'{@link Attivita} che desidera cancellare
     *
     * Se non si hanno prenotate {@link Attivita} per il giorno corrente, quest'operazione non &egrave; possibile.
     *
     */
    public void cancellazionePrenotazioneAttivita() {
        List<Attivita> attivita = this.gestoreAttivita.getAllAttivitaForTodayOf(this.account);

        if(attivita.isEmpty()) {
            System.out.println("AVVISO] nessuna prenotazione per attivita' da cancellare.");
            return;
        }

        Attivita attivitaDaCancellare = Acquisizione.scelta(attivita, a->String.valueOf(a.getId()), null,
                "Seleziona l'attivita' da cancellare", "Errore: l'id digitato NON e' associato ad alcuna attivita'.");

        String descrizione = attivitaDaCancellare.getNome();

        this.gestoreAttivita.cancellaPrenotazione(this.account, attivitaDaCancellare);
        System.out.println("La prenotazione all'attivita' '" + descrizione + "e' stata cancellata.");
    }


    /**
     * Metodo che permette, a un cliente, di notificare un reclamo al gestore
     *
     */
    public void notificaReclami() { notifica("reclamo"); }

    /**
     * Metodo che permette, a un barista, di notificare un problema al gestore
     *
     */
    public void notificaProblemi() { notifica("problema"); }

    private void notifica(String cosaNotificare) {
        String testo = Acquisizione.acqStringa("il testo del " + cosaNotificare, false);

        boolean confermaInvio = Acquisizione.scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v->{},
                "\nConfermi l'invio del " + cosaNotificare + " (t per si'/f per no)?", "Scelta non possibile. Riprova.");

        if(confermaInvio)  this.gestoreNotifiche.invioProblema(cosaNotificare.toUpperCase(Locale.ROOT) + ": " + testo);
        else System.out.println("Il " + cosaNotificare + " NON e' stato notificato.");
    }

    public Account getAccount() {
        return this.account;
    }
}