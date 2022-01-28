package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;
import it.unicam.cs.ids.Casotto.Repository.OmbrelloneRepository;
import it.unicam.cs.ids.Casotto.Repository.PrezzoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class InteractionManager
{
    @Autowired
    private GestorePrenotazioni gestorePrenotazioni;

    @Autowired
    private GestoreAccount gestoreAccount;

    @Autowired
    private GestorePagamenti gestorePagamenti;

    @Autowired
    private GestoreOrdinazione gestoreOrdinazione;

    @Autowired
    private GestoreAcquisto gestoreAcquisto;

    @Autowired
    private PrezzoRepository prezzoRepository;

    @Autowired
    private OmbrelloneRepository ombrelloneRepository;

    @Autowired
    private GestoreProdotti gestoreProdotti;

    @Autowired
    private GestoreAttivita gestoreAttivita;

    @Autowired
    private Spiaggia spiaggia;

    private Account account;

    private final Scanner sc;

    public InteractionManager() {
        this.sc = new Scanner(System.in);
    }

// **1a iterazione************************************************************************************************************* //

    /**
     * Metodo che permettere di effettuare la registrazione
     *
     */
    public void registrazione() {
        String nome = Acquisizione.acquisizioneCredenziali("il nome"), cognome = Acquisizione.acquisizioneCredenziali("il cognome");
        LocalDate dataNas = Acquisizione.acquisizioneData("di nascita");

        Utente u = new Utente(nome, cognome, dataNas);
        if(this.gestoreAccount.checkIfUserExists(u)) {
            System.out.println("Errore: l'utente e' gia' presente nel sistema.");
            return;
        }

        Livello level = Acquisizione.acquisizioneLivello();
        String email = Acquisizione.acquisizioneCredenziali("l'email"), psw = Acquisizione.acquisizioneCredenziali("la password");

        if(this.gestoreAccount.registration(u, level, email, psw))
            System.out.println("La registrazione e' andata a buon fine. Ora e' possibile eseguire il login.");
    }

    /**
     * Metodo per permettere di effettuare il login
     *
     */
    public void login() {
        while( (this.account = this.gestoreAccount.login(Acquisizione.acquisizioneCredenziali("email"), Acquisizione. acquisizioneCredenziali("password"))) == null )
            System.out.println("\nErrore: NON esiste alcun account con le credenziali associate");

        System.out.println("\nBenvenuto, " + this.gestoreAccount.getUtenteOf(this.account).getNome().toUpperCase(Locale.ROOT) + " " + this.gestoreAccount.getUtenteOf(this.account).getCognome().toUpperCase());
    }

    /**
     * Metodo per permettere di effettuare il logout
     *
     */
    public void logout() {
        this.account = null;
        System.out.println("Successfully logout!");
    }

    /**
     * Metodo che permette di effettuare la prenotazione della spiaggia
     *
     */
    public void prenotaSpiaggia() {
        Ombrellone ombrelloneScelto;
        Set<Ombrellone> ombrelloniScelti = new HashSet<>();

        System.out.print("Inserisci il numero di persone: ");
        int numPersone = sc.nextInt();

        LocalDate dataPrenotazione = Acquisizione.acquisizioneData("per la quale desideri prenotare (YYYY-mm-dd)");
        Durata durataPrenotazione = Acquisizione.acquisizioneDurata("prenotazione");

        List<Ombrellone> listaOmbrelloniLiberi = spiaggia.getOmbrelloniLiberi(dataPrenotazione, durataPrenotazione, numPersone);
        Map<String, Ombrellone> mappaOmbrelloniLiberi = listaOmbrelloniLiberi.stream().collect(Collectors.toMap(om -> String.valueOf(om.getId()), om -> om));

        if(listaOmbrelloniLiberi.stream().anyMatch(o -> o.getNumero() == numPersone))
            mappaOmbrelloniLiberi = listaOmbrelloniLiberi.stream().filter(o -> o.getNumero()==numPersone).collect(Collectors.toMap(om -> String.valueOf(om.getId()), om -> om));

        Map<String, List<Ombrellone>> mappaOmbrelloniLiberiPerFila = mappaOmbrelloniLiberi.values().stream().collect(Collectors.groupingBy(Ombrellone::getFila));

        for (String k : mappaOmbrelloniLiberiPerFila.keySet()) {
            System.out.println("\nFila " + k);
            for (Ombrellone o : mappaOmbrelloniLiberiPerFila.get(k))
                System.out.println("ID: " + o.getId() + " -- Capienza: " + o.getNumero());
        }

        while(ombrelloniScelti.stream().mapToInt(Ombrellone::getNumero).sum()<numPersone) {
            ombrelloneScelto = scelta(listaOmbrelloniLiberi, o->String.valueOf(o.getId()), o->{},
                    "Seleziona un ombrellone: ", "Errore: la scelta fatta NON è prevista. Riprovare.");
            ombrelloniScelti.add(ombrelloneScelto);
        }

        BiPredicate<Integer, Integer> predicatoLettiniSdraie = (scelta, disp) -> scelta<0 || scelta>disp;

        int lettiniScelti = Acquisizione.acquisizioneLettiniSdraie(spiaggia.lettiniDisponibili(dataPrenotazione, durataPrenotazione), "lettini", predicatoLettiniSdraie),
        sdraieScelte = Acquisizione.acquisizioneLettiniSdraie(spiaggia.sdraieDisponibili(dataPrenotazione, durataPrenotazione), "sdraie", predicatoLettiniSdraie);

        Prenotazione pren = new Prenotazione(dataPrenotazione, durataPrenotazione, lettiniScelti, sdraieScelte, this.account);
        pren.addOmbrelloni(ombrelloniScelti);
        pren.setPrezzo(this.spiaggia.getPrezzoTotale(pren));

        this.gestorePagamenti.pagamentoPrenotazione(pren, this.account);

        System.out.println("SALDO ACCOUNT DA JAVA: " + this.account.getSaldo());

        System.out.println("Prenotazione effettuata con successo!");
    }

    public void cancellaPrenotazione() {
        List<Prenotazione> prenotazioni = this.gestorePrenotazioni.getCurrentPrenotazioni(this.account);

        if(prenotazioni.isEmpty()) {
            System.out.println("ATTENZIONE: nessuna prenotazione da cancellare.");
            return;
        }

        Prenotazione prenotazione = scelta(prenotazioni, p->String.valueOf(p.getId()),
                    p-> System.out.println(p.toString()), "Seleziona una prenotazione: ", "Errore: la prenotazione scelta NON esiste. Riprovare.");

        if(!this.gestorePrenotazioni.cancellazionePrenotazione(prenotazione))
            System.out.println("\nErrore: impossibile cancellare la prenotazione.");
        else System.out.println("\nLa prenotazione con id '" + prenotazione.getId() + "' e' stata eliminata correttamente. Il suo saldo e' stato aggiornato.");
    }

    public void visualizzaStoricoPrenotazioni() {visualizzaPrenotazioni(this.gestorePrenotazioni::getPrenotazioniHistory);}

    public void visualizzaPrenotazioniCorrenti() {visualizzaPrenotazioni(this.gestorePrenotazioni::getCurrentPrenotazioni);}

    private void visualizzaPrenotazioni(Function<Account, List<Prenotazione>> f) {
        f.apply(this.account).forEach(p-> System.out.println(p.toString()));
    }

// **2a iterazione************************************************************************************************************* //

    public void acquistaProdotto() {
        if(this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now())==null) {
            System.err.println("Errore: NON hai nessuna prenotazione per la giornata odierna.");
            return;
        }

        if(selezionaProdotti().equals("e")) {
            System.err.println("Errore: NON ci sono prodotti da selezionare.");
            return;
        }

        Map<String, Supplier<String>> mappaModificheOrdinazione = Map.of("a", this::selezionaProdotti, "t", this::rimuoviRichieste);

        String sceltaSuOrdinazione;
        List<String> opzioniSuOrdinazione = List.of("c", "m", "a");

         do {
            System.out.print("\nDesidera confermare (c), modificare (m) o annullare (a) l'ordinazione (c/m/a): ");
            while (!opzioniSuOrdinazione.contains((sceltaSuOrdinazione = sc.next())))
                System.out.print("Errore: la scelta fatta NON è prevista. Riprovare: ");

            if (sceltaSuOrdinazione.equals("c")) confermaOrdinazione();
            else if (sceltaSuOrdinazione.equals("a")) annullaAcquisto();
            else {
                System.out.print("Desideri aggiungere o togliere prodotti (a/t): ");
                while (!mappaModificheOrdinazione.containsKey(sceltaSuOrdinazione = sc.next()))
                    System.out.print("Errore: la scelta fatta NON è prevista. Riprovare: ");

                sceltaSuOrdinazione = mappaModificheOrdinazione.get(sceltaSuOrdinazione).get();
            }

        } while(sceltaSuOrdinazione.equals("m"));
    }

    private String selezionaProdotti() {
        boolean flagModifiche, flagContinuare = true;

        while(flagContinuare) {
            List<Prodotto> prodotti = this.gestoreProdotti.getAllAlimenti();

            if(prodotti.stream().allMatch(p->p.getQuantita()==0)) return "e";

            Prodotto prodotto = scelta(prodotti, p->String.valueOf(p.getId()), p->System.out.println(p.toString()),
                    "\nScegli un prodotto: ", "Errore: l'id digitato NON corrisponde ad alcun prodotto.");

            System.out.print("Digita la quantita' del prodotto: ");
            int quantita = sc.nextInt();

            if(!this.gestoreProdotti.isPresent(prodotto, quantita)) System.out.println("Errore: la quantita' digitata NON e' disponibile.");
            else {
                System.out.println("\nIl prodotto da lei selezionato e' disponibile.");
                flagModifiche = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> {},
                        "Desideri modificare gli ingredienti del prodotto(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");

                if(flagModifiche) {
                    System.out.print("\nDigita modifiche: ");
                    this.gestoreAcquisto.addRichiesta(prodotto, quantita, sc.next());
                }
                else this.gestoreAcquisto.addRichiesta(prodotto, quantita, "");

                System.out.println("\nLa sua richiesta e' stata memorizzata correttamente.");
            }

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> {},
                    "\nDesideri continuare con la selezione(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
        }

        double prezzo =  this.gestoreAcquisto.getPrezzoTotale();

        System.out.println("\nGentile cliente, il prezzo totale della sua ordinazione e': " + prezzo +
                " euro (questo potra' variare se sono state richieste delle modifiche ai prodotti.");

        return "m";
    }

    private void annullaAcquisto() {
        this.gestoreAcquisto.cancellaAcquisto();
        System.out.println("\nIl suo acquisto e' stato annullato.");
    }

    private void confermaOrdinazione() {
        Set<Ombrellone> ombrelloniPrenotazione = this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now()).getOmbrelloni();
        Ombrellone ombrellone = scelta(ombrelloniPrenotazione, o->String.valueOf(o.getId()), o-> System.out.println(o.toString()),
                "\nSeleziona un ombrellone da associare alla prenotazione: ", "L'id selezionato NON e' valido. Riprova.");

        this.gestoreAcquisto.confirmOrdinazione(ombrellone);

        System.out.println("\nIl suo ordine e' stato correttamente registrato.");
    }

    private String rimuoviRichieste() {
        boolean flagContinuare = true;

        while(flagContinuare) {
            List<Richiesta> richieste = this.gestoreAcquisto.getAllRichieste();

            //TODO aggiustare l'associazione dell'id
            LinkedList<Integer> indiciRichieste = IntStream.range(1, richieste.size()+1).boxed().collect(Collectors.toCollection(LinkedList::new));
            Richiesta richiesta = scelta(richieste, r-> String.valueOf(indiciRichieste.poll()), r -> System.out.println(r.toString() + "\nID per la RIMOZIONE: " + indiciRichieste.getFirst()),
                    "\nSeleziona una richiesta da rimuovere", "Errore: l'id digitato NON e' associato ad alcuna richiesta. Riprova.");

            this.gestoreAcquisto.cancellaRichiesta(richiesta);

            if(this.gestoreAcquisto.getAllRichieste().isEmpty()) return "a";
            else System.out.println("La richiesta e' stata correttamente rimossa. " + "Il prezzo attuale e' di " + this.gestoreAcquisto.getPrezzoTotale() + " euro");

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                        "\nDesideri continuare con la rimozione dei prodotti(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");

        }
        return "m";
    }

    public void pagamentoOrdinazione() {
        String scelta;
        List<String> opzioni = List.of("c", "e");

        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.DA_PAGARE);
        Ordinazione ordinazione = scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "Seleziona un'ordinazione da preparare", "Errore: l'id digitato NON e' associato ad alcun ordinazione");
        generazioneContoOrdinazione(ordinazione);

        System.out.print("Desidera pagare in contanti (c) o in modo elettronico (e) (c/e): ");
        while(!opzioni.contains((scelta=sc.next())))
            System.out.print("Errore: la scelta fatta NON è prevista. Riprovare: ");

        if(Objects.equals(scelta, "c")) {
            System.out.println("Inserisci denaro: ");
            this.gestorePagamenti.pagamentoContanti(ordinazione, sc.nextDouble());
        }
        else  this.gestorePagamenti.pagamentoElettronico(ordinazione);

        // rappresenta la stampa dello scontrino con la stampante, con i relativi problemi
        System.out.println("SCONTRINO: \n" + this.gestorePagamenti.creazioneScontrino(ordinazione));
    }

    private void generazioneContoOrdinazione(Ordinazione ordinazione) {

        if(ordinazione.getPrezzoTot()>0) return;

        List<Richiesta> richieste = this.gestoreOrdinazione.listaRichiesteConModifiche(ordinazione);
        for(Richiesta richiesta : richieste) {
            System.out.println(richiesta.toString() + "Prezzo originale: "
                    + this.gestoreProdotti.getProdottoOf(richiesta).getPrezzo() + "\n\nInserisci il prezzo comprendente le modifiche: ");
            this.gestoreOrdinazione.impostaPrezzoRichiesta(richiesta, sc.nextDouble());
        }

        this.gestoreOrdinazione.ricalcolaPrezzoFinale(ordinazione);
    }


    public void inizioPreparazione() {
        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.PAGATO);
        Ordinazione ordinazione = scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "Seleziona un'ordinazione da preparare", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        System.out.println("PRODOTTI ORDINAZIONE: ");//quantita aggiungere
        ordinazione.getProdotti().forEach(r-> System.out.println(r.toString()));
        this.gestoreOrdinazione.setStato(ordinazione, Stato.IN_PREPARAZIONE);
        System.out.println("La modifica dello stato dell'ordinazione con id '" + ordinazione.getId() + " e' stata eseguita correttamente.");
    }


// **3a iterazione************************************************************************************************************* //

    public void modificaDati() {
        //TODO controllare coerenza tra CU e SSD per quanto riguarda cosa fare dopo la notifica che il valore inserito no è corretto

        boolean flagContinuare = true;

        List<Map<String, Supplier<Boolean>>> listaDatiDaModificare = List.of(
                Map.of("nome", ()->this.gestoreAccount.changeUserName(this.account, Acquisizione.acquisizioneCredenziali("il nome"))),
                Map.of("cognome", ()->this.gestoreAccount.changeUserSurname(this.account, Acquisizione.acquisizioneCredenziali("il cognome"))),
                Map.of("datanascita", ()->this.gestoreAccount.changeUserBirthdayDate(this.account, Acquisizione.acquisizioneData("di nascita"))),
                Map.of("email", ()->this.gestoreAccount.changeAccountEmail(this.account, Acquisizione.acquisizioneCredenziali("l'email"))),
                Map.of("password", ()->this.gestoreAccount.changePasswordAccount(this.account, Acquisizione.acquisizioneCredenziali("la password")))
                );

        while(flagContinuare) {

            Map<String, Supplier<Boolean>> sceltaDatoDaModificare = scelta(listaDatiDaModificare, m->m.entrySet().iterator().next().getKey(), m-> System.out.println("" + m.entrySet().iterator().next().getKey()),
                    "\nSeleziona il dato da modificare: ", "Attenzione: scelta NON prevista. Riprovare.");

            sceltaDatoDaModificare.entrySet().iterator().next().getValue().get();

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la modifica dei dati(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");

        }
      }

    public void finePreparazione() {
        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.IN_PREPARAZIONE);
        Ordinazione ordinazione = scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "Seleziona un'ordinazione da contrassegnare come pronta", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        this.gestoreOrdinazione.setStato(ordinazione, Stato.IN_CONSEGNA);
        System.out.println("La modifica dello stato dell'ordinazione con id '" + ordinazione.getId() + " e' stata eseguita correttamente.");
    }

    public void consegnaComandaConScontrino() {

        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.IN_CONSEGNA);
        if(ordinazioni.isEmpty()) {
            System.out.println("\nNessuna ordinazione da consegnare.");
            return;
        }

        Ordinazione ordinazione = scelta(ordinazioni, o->String.valueOf(o.getId()), o-> System.out.println(o.toString()),
                "Seleziona l'ordinazione da consegnare: ", "L'id selezionato NON e' associato ad alcun ordinazione da consegnare");

        // nella realtà l'addetto spiaggia prende lo scontrino stampato e lo "allega" all'ordinazione che consegnerà

        this.gestoreOrdinazione.setStato(ordinazione, Stato.CONSEGNATO);
        System.out.println("L'ordinazione e' stata consegnata. Lo stato dell'ordine e' stato aggiornato in 'CONSEGNATO'");
    }

    public void prenotazioneAttivita() {
        if(this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now())==null) {
            System.out.println("Nessuna prenotazione per il giorno corrente.");
            return;
        }

        if(!this.gestoreAttivita.thereIsAttivitaForToday()) {
            System.err.println("Nessuna attività prevista per oggi.");
            return;
        }

        Attivita attivitaScelta;
    //    int partecipanti;
        boolean booked = true;

         do{
            if(!booked) System.out.println("Impossibile partecipare all'attività selezionata con il numero di partecipanti digitato.");

            List<Attivita> attivita = this.gestoreAttivita.getAllAttivitaForToday();
            attivitaScelta = scelta(attivita, a->String.valueOf(a.getId()), a-> System.out.println(a.toString()),
                    "Scegli un attivita' a cui desideri partecipare", "Errore: attivita' NON valida. Riprova");

            System.out.println("Inserisci il numero di partecipanti(MAX. " + this.gestoreAttivita.postiRimanenti(attivitaScelta) + "): ");
         //   partecipanti = sc.nextInt();

        }while( !(booked=this.gestoreAttivita.prenotazione(this.account, sc.nextInt(), attivitaScelta)) );

        System.out.println("L'iscrizione all'attivita' '" + attivitaScelta.getNome() + " e' stata correttamente registrata.");
    }

    public void cancellazionePrenotazioneAttivita() {
        List<Attivita> attivita = this.gestoreAttivita.getAllAttivitaForTodayOf(this.account);
        Attivita attivitaDaCancellare = scelta(attivita, a->String.valueOf(a.getId()), null,
                "Seleziona l'attivita' da cancellare", "Errore: l'id digitato NON e' associato ad alcuna attivita'.");

        String descrizione = attivitaDaCancellare.getNome();

        this.gestoreAttivita.cancellaPrenotazione(this.account, attivitaDaCancellare);
        System.out.println("La prenotazione all'attivita' '" + descrizione + "e' stata cancellata.");
    }

// **4a iterazione************************************************************************************************************* //

    public void gestioneStruttura() {

        List<String> opzioniGestione = List.of("prodotti", "struttura");
        String sceltaGestione = scelta(opzioniGestione, s->s, System.out::println, "Seleziona cosa vuoi gestire: ",
                "Errore: opzione NON valida. Riprova.");


    }

    private void gestioneAttivita() {

    }

    private void gestioneProdotti() {

    }

    public void invioNotifiche() {

    }

    public void notificaProblemi() {

    }

    public void notificaReclami() {

    }

































    public void inserisciPrezzoPerOmbrellone() {
        LocalDate dataInizio = Acquisizione.acquisizioneData("di inizio del prezzo"),
                dataFine = Acquisizione.acquisizioneData("di fine del prezzo");

        int meseInizio = Acquisizione.acquisizioneMese("inizio"), meseFine = Acquisizione.acquisizioneMese("fine");
        Durata durata = Acquisizione.acquisizioneDurata("del prezzo");

        double prezzo = Acquisizione.acquisizionePrezzo("per gli ombrelloni");

        this.prezzoRepository.save(new Prezzo(prezzo, meseInizio, meseFine, dataInizio, dataFine, durata));
        System.out.println("Prezzo salvato correttamente.");
    }

    public void impostaPrezzoOmbrellone() {
        Ombrellone ombrellone = scelta(spiaggia.getAllOmbrelloni(), o->String.valueOf(o.getId()), o->System.out.println(o.toString()),
                "Seleziona un ombrellone: ", "Errore: l'identificativo digitato non è associato ad alcun ombrellone.");
        Prezzo prezzo = scelta((List<Prezzo>)this.prezzoRepository.findAll(), p->String.valueOf(p.getId()), p-> System.out.println(p.toString()),
                "Seleziona un prezzo: ", "Errore: l'identificativo digitato non e' associato ad alcun prezzo.");

        ombrellone.addPrezzi(Set.of(prezzo));
        this.ombrelloneRepository.save(ombrellone);
        System.out.println("Il prezzo e' stato associato correttamente");
    }



// **per ora NESSUNA iterazione************************************************************************************************************* //


    public Account getAccount() {
        return this.account;
    }

    private <T> T scelta(Collection<T> listaOggetti, Function<T, String> funzioneChiaveMappa, Consumer<T> consumerStampaOggetti, String fraseSelezione, String fraseErrore) {
        String scelta = "";
        listaOggetti.forEach(consumerStampaOggetti);
        Map<String, T> mappaOggetti = listaOggetti.stream().collect(Collectors.toMap(funzioneChiaveMappa, o->o));

        do {
            if(!scelta.isEmpty()) System.out.println(fraseErrore);

            System.out.print(fraseSelezione); scelta = "" + sc.next();
        }while(!mappaOggetti.containsKey(scelta));

        return mappaOggetti.get(scelta);
    }
}