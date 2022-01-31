package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;
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
    @Autowired private GestorePrenotazioni gestorePrenotazioni;

    @Autowired private GestoreAccount gestoreAccount;

    @Autowired private GestorePagamenti gestorePagamenti;

    @Autowired private GestoreOrdinazione gestoreOrdinazione;

    @Autowired private GestoreAcquisto gestoreAcquisto;

    @Autowired private GestoreProdotti gestoreProdotti;

    @Autowired private GestoreAttivita gestoreAttivita;

    @Autowired private GestoreNotifiche gestoreNotifiche;

    @Autowired private Spiaggia spiaggia;

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
        String nome = Acquisizione.acqStringa("il nome"), cognome = Acquisizione.acqStringa("il cognome");
        LocalDate dataNas = Acquisizione.acqData("di nascita");

        Utente u = new Utente(nome, cognome, dataNas);
        if(this.gestoreAccount.checkIfUserExists(u)) {
            System.out.println("Impossibile proseguire con la registrazione: l'utente e' gia' presente nel sistema.");
            return;
        }

        String email = Acquisizione.acqStringa("l'email"), psw = Acquisizione.acqStringa("la password");

        if(this.gestoreAccount.registration(u, Livello.CLIENTE, email, psw))
            System.out.println("La registrazione e' andata a buon fine. Ora e' possibile eseguire il login.");
    }

    /**
     * Metodo per permettere di effettuare il login
     *
     */
    public void login() {
        while( (this.account = this.gestoreAccount.login(Acquisizione.acqStringa("email"), Acquisizione.acqStringa("password"))) == null )
            System.out.println("\nErrore: NON esiste alcun account con le credenziali associate");

        System.out.print("\nBenvenuto, " + this.gestoreAccount.getUtenteOf(this.account).getNome().toUpperCase(Locale.ROOT) + " " + this.gestoreAccount.getUtenteOf(this.account).getCognome().toUpperCase() + "\n");

        this.gestoreNotifiche.getNotifiche(this.account.getLivello()).forEach(n-> System.out.println(n.toString()));
    }

    /**
     * Metodo per permettere di effettuare il logout
     *
     */
    public void logout() {
        this.account = null;
        System.out.println("Logout effettuato con successo!");
    }

    /**
     * Metodo che permette di effettuare la prenotazione della spiaggia
     *
     */
    public void prenotaSpiaggia() {
        Set<Ombrellone> ombrelloniScelti = new HashSet<>();

        int numPersone = Acquisizione.acqIntero("il numero di persone");
        LocalDate data = Acquisizione.acqData("per la quale desideri prenotare");
        Durata durata = Acquisizione.acqDurata("prenotazione");

        if(data.isBefore(LocalDate.now()) || this.gestorePrenotazioni.haPrenotazioni(this.account, data, durata)) {
            System.out.println("AVVISO] Impossibile effettuare una prenotazione.");
            return;
        }

        List<Ombrellone> listaOmbrelloniLiberi = spiaggia.getOmbrelloniLiberi(data, durata, numPersone);

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
            ombrelloniScelti.add(scelta(listaOmbrelloniLiberi, o->String.valueOf(o.getId()), o->{},
                    "Seleziona un ombrellone (digita l'id): ", "Errore: la scelta fatta NON è prevista. Riprovare."));

        int lettiniScelti = Acquisizione.acqLettiniSdraie(spiaggia.lettiniDisponibili(data, durata), "lettini", (scelta, disp) -> scelta<0 || scelta>disp),
        sdraieScelte = Acquisizione.acqLettiniSdraie(spiaggia.sdraieDisponibili(data, durata), "sdraie", (scelta, disp) -> scelta<0 || scelta>disp);

        Prenotazione pren = new Prenotazione(data, durata, lettiniScelti, sdraieScelte, this.account);
        pren.addOmbrelloni(ombrelloniScelti);

        this.gestorePagamenti.pagamentoPrenotazione(pren, this.account);
        System.out.println("Prenotazione effettuata con successo!");
    }

    public void cancellaPrenotazione() {
        List<Prenotazione> prenotazioni = this.gestorePrenotazioni.getCurrentPrenotazioni(this.account);

        if(prenotazioni.isEmpty()) {
            System.out.println("AVVISO] nessuna prenotazione da cancellare.");
            return;
        }

        Prenotazione prenotazione = scelta(prenotazioni, p->String.valueOf(p.getId()),
                    p-> System.out.println(p.toString()), "Seleziona una prenotazione", "Errore: la prenotazione scelta NON esiste. Riprovare.");

        if(!this.gestorePrenotazioni.cancellazionePrenotazione(prenotazione)) System.out.println("\nAVVISO] Impossibile cancellare la prenotazione.");
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
            System.err.println("AVVISO] Non ci sono prodotti da poter selezionare.");
            return;
        }

        Map<String, Supplier<String>> mappaModificheOrdinazione = Map.of("a", this::selezionaProdotti, "t", this::rimuoviRichieste);

        String sceltaSuOrdinazione;
        List<String> opzioniSuOrdinazione = List.of("confermare", "modificare", "annullare");

         do {
             sceltaSuOrdinazione = scelta(opzioniSuOrdinazione.stream().map(op->String.valueOf(op.charAt(0))).collect(Collectors.toList()),
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

    private String selezionaProdotti() {
        boolean flagModifiche, flagContinuare = true;

        while(flagContinuare) {
            List<Prodotto> prodotti = this.gestoreProdotti.getAllAlimenti();

            if(prodotti.stream().allMatch(p->p.getQuantita()==0)) return "e";

            Prodotto prodotto = scelta(prodotti, p->String.valueOf(p.getId()), p->System.out.println(p.toString()),
                    "\nScegli un prodotto: ", "Errore: l'id digitato NON corrisponde ad alcun prodotto.");

            int quantita = Acquisizione.acqIntero("la quantita' del prodotto: (MAX. " + prodotto.getQuantita() + "): ");

            if(!this.gestoreProdotti.isPresent(prodotto, quantita))
                System.out.println("AVVISO] La quantita' digitata NON e' disponibile.");
            else {
                System.out.println("\nIl prodotto da lei selezionato e' disponibile.");
                flagModifiche = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> {},
                        "Vuoi modificare gli ingredienti del prodotto(t per si'/f per no)?", "Scelta non possibile. Riprova.");

                if(flagModifiche) {
                    sc.nextLine();
                    System.out.print("\nDigita le modifiche desiderate: ");
                    this.gestoreAcquisto.addRichiesta(prodotto, quantita, sc.nextLine());
                }
                else this.gestoreAcquisto.addRichiesta(prodotto, quantita, "");

                System.out.println("\nLa sua richiesta e' stata memorizzata correttamente.");
            }

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> {},
                    "Desideri continuare con la selezione dei prodotti(t per si'/f per no)?", "Scelta non possibile. Riprova.");
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
                "\nSeleziona un ombrellone da associare alla prenotazione", "L'id selezionato NON e' valido. Riprova.");

        this.gestoreAcquisto.confirmOrdinazione(ombrellone);

        System.out.println("\nIl suo ordine e' stato correttamente registrato.");
    }

    private String rimuoviRichieste() {
        boolean flagContinuare = true;

        while(flagContinuare) {
            List<Richiesta> richieste = this.gestoreAcquisto.getAllRichieste();
            List<Integer> listaIndici = IntStream.range(1, richieste.size()+1).boxed().collect(Collectors.toList());

            int indice;
            listaIndici.forEach(i-> System.out.println(richieste.get(i-1) + "\nID per la rimozione della richiesta: " + i));

            while(!listaIndici.contains(indice =  Acquisizione.acqIntero("l'id della richiesta da rimuovere: ")))
                System.out.println("Errore: scelta NON valida. Riprova.");

            this.gestoreAcquisto.cancellaRichiesta(richieste.get(indice-1));

            if(this.gestoreAcquisto.getAllRichieste().isEmpty()) {
                System.out.println("Sono state rimosse tutte le richieste .");
                return "a";
            }
            else System.out.println("La richiesta e' stata correttamente rimossa. " + "Il prezzo attuale e' di " + this.gestoreAcquisto.getPrezzoTotale() + " euro");

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                        "\nDesideri continuare con la rimozione dei prodotti(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
        }
        return "m";
    }

    public void pagamentoOrdinazione() {
        String sceltaPagamento;
        List<String> opzioni = List.of("c", "e");

        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.DA_PAGARE);

        if(ordinazioni.isEmpty()) {
            System.out.println("Nessuna ordinazione da far pagare. ");
            return;
        }

        Ordinazione ordinazione = scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "\nSeleziona un'ordinazione da far pagare: ", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        generazioneContoOrdinazione(ordinazione);
        System.out.print("Il prezzo dell'ordinazione e' di " + ordinazione.getPrezzoTot() + " euro. " +
                "\nDesidera pagare in contanti (c) o con pagamento elettronico (e) (c/e)?: ");

        while(!opzioni.contains((sceltaPagamento=sc.nextLine())))
            System.out.print("Errore: la scelta fatta NON è prevista. Riprova: ");

        if(Objects.equals(sceltaPagamento, "c")) {
            double resto;
            do { resto = this.gestorePagamenti.pagamentoContanti(ordinazione, Acquisizione.acqDouble("i contanti")); }
            while (resto==-1.0);

            System.out.println("\nIl resto e' di: " + resto + " euro.");
        }
        else this.gestorePagamenti.pagamentoElettronico(ordinazione);
        // rappresenta la stampa dello scontrino con la stampante, con i relativi problemi
        System.out.println(this.gestorePagamenti.creazioneScontrino(ordinazione));
    }

    private void generazioneContoOrdinazione(Ordinazione ordinazione) {
        if(ordinazione.getPrezzoTot()>0) return;

        for(Richiesta richiesta : this.gestoreOrdinazione.listaRichiesteConModifiche(ordinazione)) {
            System.out.print(richiesta.toString() + "\n\nPrezzo originale: " + this.gestoreProdotti.getProdottoOf(richiesta).getPrezzo() + "\n");
            this.gestoreOrdinazione.impostaPrezzoRichiesta(richiesta, Acquisizione.acqDouble("il prezzo comprendente le modifiche"));
        }
        this.gestoreOrdinazione.ricalcolaPrezzoFinale(ordinazione);
    }


    public void inizioPreparazione() {
        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.PAGATO);

        if(ordinazioni.isEmpty()) {
            System.out.println("AVVISO] Non ci sono ordinazioni da far pagare.");
            return;
        }

        Ordinazione ordinazione = scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "\nSeleziona un'ordinazione da preparare", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        System.out.println("\n\nPRODOTTI ORDINAZIONE: " + ordinazione.toString());
        this.gestoreOrdinazione.setStato(ordinazione, Stato.IN_PREPARAZIONE);
        System.out.println("La modifica dello stato dell'ordinazione con id '" + ordinazione.getId() + " e' stata eseguita correttamente.");
    }


// **3a iterazione************************************************************************************************************* //

    public void modificaLivello() {
        List<Account> accounts = this.gestoreAccount.getAllAccount();

        Account a = scelta(accounts, ac->String.valueOf(ac.getId()), ac-> System.out.println(ac.toString()),
                "Seleziona l'account di cui modificare il livello", "Errore: scelta NON valida. Riprova");

        this.gestoreAccount.updateLivelloAccount(a, Acquisizione.acqLivello("dell'account"));
        System.out.println("Il livello dell'account con id '" + a.getId() + "' e' stato modificato correttamente.");
    }

    public void modificaDati() {
        boolean flagContinuare = true;

        List<Map<String, Supplier<Boolean>>> listaDatiAccountDaModificare = List.of(
                Map.of("nome", ()->this.gestoreAccount.changeUserName(this.account, Acquisizione.acqStringa("il nome"))),
                Map.of("cognome", ()->this.gestoreAccount.changeUserSurname(this.account, Acquisizione.acqStringa("il cognome"))),
                Map.of("datanascita", ()->this.gestoreAccount.changeUserBirthdayDate(this.account, Acquisizione.acqData("di nascita"))),
                Map.of("email", ()->this.gestoreAccount.changeAccountEmail(this.account, Acquisizione.acqStringa("l'email"))),
                Map.of("password", ()->this.gestoreAccount.changePasswordAccount(this.account, Acquisizione.acqStringa("la password")))
                );

        while(flagContinuare) {
            scelta(listaDatiAccountDaModificare, m->m.entrySet().iterator().next().getKey(), m-> System.out.println("" + m.entrySet().iterator().next().getKey()),
                    "\nSeleziona il dato da modificare", "Attenzione: scelta NON prevista. Riprovare.").entrySet().iterator().next().getValue().get();

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la modifica dei dati(t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }
    }

    public void finePreparazione() {
        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.IN_PREPARAZIONE);
        if(ordinazioni.isEmpty()) {
            System.out.println("AVVISO] Nessuna ordinazione in preparazione.");
            return;
        }

        Ordinazione ordinazione = scelta(ordinazioni, ord->String.valueOf(ord.getId()),  ord-> System.out.println(ord.toString()),
                "\nSeleziona un'ordinazione da contrassegnare come pronta", "Errore: l'id digitato NON e' associato ad alcun ordinazione");

        this.gestoreOrdinazione.setStato(ordinazione, Stato.IN_CONSEGNA);
        System.out.println("La modifica dello stato dell'ordinazione con id '" + ordinazione.getId() + " e' stata eseguita correttamente.");
    }

    public void consegnaComandaConScontrino() {

        List<Ordinazione> ordinazioni = this.gestoreOrdinazione.getOrdinazioneWith(Stato.IN_CONSEGNA);
        if(ordinazioni.isEmpty()) {
            System.out.println("\nAVVISO] Nessuna ordinazione da consegnare.");
            return;
        }

        Ordinazione ordinazione = scelta(ordinazioni, o->String.valueOf(o.getId()), o-> System.out.println(o.toString()),
                "Seleziona l'ordinazione da consegnare", "L'id selezionato NON e' associato ad alcun ordinazione da consegnare");

        // nella realtà l'addetto spiaggia prende lo scontrino stampato e lo "allega" all'ordinazione che consegnerà

        this.gestoreOrdinazione.setStato(ordinazione, Stato.CONSEGNATO);
        System.out.println("L'ordinazione e' stata consegnata. Lo stato dell'ordine e' stato aggiornato in 'CONSEGNATO'");
    }

    public void prenotazioneAttivita() {
        if(this.gestorePrenotazioni.getPrenotazioneOf(this.account, LocalDate.now())==null || this.gestoreAttivita.thereIsAttivitaForToday()) {
            System.out.println("AVVISO] Impossibile prenotare un'attivita'.");
            return;
        }

        Attivita attivitaScelta;
        boolean booked = true;

         do{
            if(!booked) System.out.println("Impossibile partecipare all'attività selezionata con il numero di partecipanti digitato.");

            attivitaScelta = scelta(this.gestoreAttivita.getAllAttivitaForToday(), a->String.valueOf(a.getId()), a-> System.out.println(a.toString()),
                    "Scegli un attivita' a cui desideri partecipare", "Errore: attivita' NON valida. Riprova");

            System.out.println("Inserisci il numero di partecipanti(MAX. " + this.gestoreAttivita.postiRimanenti(attivitaScelta) + "): ");
        }while( !(booked=this.gestoreAttivita.prenotazione(this.account, Acquisizione.acqIntero("il numero di partecipanti"), attivitaScelta)) );

        System.out.println("L'iscrizione all'attivita' '" + attivitaScelta.getNome() + " e' stata correttamente registrata.");
    }

    public void cancellazionePrenotazioneAttivita() {
        List<Attivita> attivita = this.gestoreAttivita.getAllAttivitaForTodayOf(this.account);
        if(attivita.isEmpty()) {
            System.out.println("AVVISO] nessuna prenotazione da cancellare.");
            return;
        }

        Attivita attivitaDaCancellare = scelta(attivita, a->String.valueOf(a.getId()), null,
                "Seleziona l'attivita' da cancellare", "Errore: l'id digitato NON e' associato ad alcuna attivita'.");

        String descrizione = attivitaDaCancellare.getNome();

        this.gestoreAttivita.cancellaPrenotazione(this.account, attivitaDaCancellare);
        System.out.println("La prenotazione all'attivita' '" + descrizione + "e' stata cancellata.");
    }

// **4a iterazione************************************************************************************************************* //

    public void gestioneStruttura() {

        List<String> opzioniGestione = List.of("prodotti", "attivita");
        String sceltaGestione = scelta(opzioniGestione, s->String.valueOf(s.charAt(0)), System.out::println,
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

    private <T> void gestione(Supplier<Map<T, Boolean>> aggiunta, Supplier<Map<T, Boolean>> rimozione, Supplier<Map<T, Boolean>> modifica, BiConsumer<T, Boolean> funzioneModificaOggetto) {
        boolean flagContinuare = true;

        Map<String, Supplier<Map<T, Boolean>>> mappaGestione = Map.of(
                "aggiungere", aggiunta,
                "rimuovere", rimozione,
                "modificare", modifica
        );

        while(flagContinuare) {
            String sceltaGestione = scelta(mappaGestione.keySet(), m->m, System.out::println, "Digita cosa desideri fare", "Errore. Opzione non valida. Riprova.");

            Map<T, Boolean> m = mappaGestione.get(sceltaGestione).get();

            if(m!=null) {
                boolean confermaModifiche = scelta(List.of(true, false), v -> String.valueOf(v.toString().charAt(0)), v -> System.out.println(v.toString()),
                        "\nConfermi di voler " + sceltaGestione + " (t per si'/f per no)?", "Scelta non possibile. Riprova.");

                if (confermaModifiche) {
                    funzioneModificaOggetto.accept(m.entrySet().iterator().next().getKey(), m.entrySet().iterator().next().getValue());
                    System.out.println("\nL'azione di " + sceltaGestione + " e' stata correttamente effettuata.");
                } else System.out.println("\nLe modifiche NON sono state apportate.");
            }
            else System.out.println("\nNon c'e' nulla da " + sceltaGestione);

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la gestione (t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
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

        return Map.of(scelta(oggetti, a->String.valueOf(indici.remove(0)), a-> System.out.println(a.toString()), "Seleziona l'attivita' da rimuovere: ",
                "Errore: scelta NON valida. Riprova."), true);
    }

    private Map<Attivita, Boolean> selezionaAttivitaDaModificare() {
        boolean flagContinuare = true;
        List<Attivita> attivita = this.gestoreAttivita.getAllAttivita();

        if(attivita.isEmpty()) return null;

        Attivita daModificare = scelta(attivita, a->String.valueOf(a.getId()), a-> System.out.println(a.toString()), "Seleziona cio' che vuoi rimuovere: ",
                "Errore: scelta NON valida. Riprova.");

        Object[] dati = new Object[3];

        while(flagContinuare) {
            Map<String, Supplier<Object>> datiAttivita = Map.of(
                    "nome", ()->dati[0]=Acquisizione.acqStringa("il nome dell'attivita'"),
                    "data",  ()->dati[1]=Acquisizione.acqData("la data dell'attivita'"),
                    "numero_posti", ()->dati[2]=Acquisizione.acqIntero("il numero di posti dell'attivita'"));

            String sceltaDatoDaModificare = scelta(datiAttivita.keySet(), k->k, System.out::println,
                    "\nSeleziona il dato da modificare: ", "Attenzione: scelta NON prevista. Riprovare.");

            datiAttivita.get(sceltaDatoDaModificare).get();

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la modifica dei dati dell'attivita'(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
        }

        daModificare.setNome( (dati[0]==null) ? daModificare.getNome() : (String)dati[0] );
        daModificare.setData( (dati[1]==null) ? daModificare.getData() : (LocalDate) dati[1] );
        daModificare.setNumeroposti( (dati[2]==null) ? daModificare.getNumeroposti() : (Integer)dati[2] );

        return Map.of(daModificare, false);
    }

    private Map<Prodotto, Boolean> selezionaProdottoDaModificare() {
        boolean flagContinuare = true;
        List<Prodotto> prodotti = this.gestoreProdotti.getAll();

        if(prodotti.isEmpty()) return null;

        Prodotto daModificare = scelta(prodotti, a->String.valueOf(a.getId()), a-> System.out.println(a.toString()),
                "Seleziona cio' che vuoi rimuovere", "Errore: scelta NON valida. Riprova.");

        Object[] dati = new Object[4];

        while(flagContinuare) {
            Map<String, Supplier<Object>> datiProdotto = Map.of(
                    "nome", ()->dati[0]=Acquisizione.acqStringa("il nome del prodotto"),
                    "quantita",  ()->dati[1]=Acquisizione.acqIntero("la quantita' del prodotto"),
                    "prezzo", ()->dati[2]=Acquisizione.acqDouble("il prezzo del prodotto"),
                    "tipo", ()->dati[3]=Acquisizione.acqTipo("del prodotto"));

            String sceltaDatoDaModificare = scelta(datiProdotto.keySet(), k->k, System.out::println,
                    "\nSeleziona il dato da modificare: ", "Attenzione: scelta NON prevista. Riprovare.");

            datiProdotto.get(sceltaDatoDaModificare).get();

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la modifica dei dati del prodotto(t per si'/f per no)? : ", "Scelta non possibile. Riprova.");
        }

        daModificare.setOggetto( (dati[0]==null) ? daModificare.getOggetto() : (String)dati[0] );
        daModificare.setQuantita( (dati[1]==null) ? daModificare.getQuantita() : (int) dati[1] );
        daModificare.setPrezzo( (dati[2]==null) ? daModificare.getPrezzo() : (double)dati[2] );
        daModificare.setTipo( (dati[3]==null) ? daModificare.getTipo() : (Tipo) dati[3] );

        return Map.of(daModificare, false);
    }

    public void notificaProblemi() { notifica("problema"); }

    public void notificaReclami() { notifica("reclamo"); }

    private void notifica(String cosaNotificare) {
        String testo = Acquisizione.acqStringa("il testo del " + cosaNotificare);

        boolean confermaInvio = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                "\nConfermi l'invio del " + cosaNotificare + " (t per si'/f per no)? : ", "Scelta non possibile. Riprova.");

        if(confermaInvio)  this.gestoreNotifiche.invioProblema(cosaNotificare.toUpperCase(Locale.ROOT) + ": " + testo);
        else System.out.println("Il " + cosaNotificare + " NON e' stato notificato.");
    }

    public void invioNotifiche() {
        String testoNotifica = Acquisizione.acqStringa("il testo della notifica");
        LocalDate dataFine = Acquisizione.acqData("di fine validita' della notifica");


        boolean flagContinuare = true;
        List<Livello> gruppi = new ArrayList<>();

        while(flagContinuare) {
            gruppi.add(Acquisizione.acqLivello("della notifica"));

            flagContinuare = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                    "\nDesideri continuare con la selezione dei gruppi ai quali inviare la notifica(t per si'/f per no)?", "Scelta non possibile. Riprova.");
        }

        boolean confermaInvio = scelta(List.of(true, false), v->String.valueOf(v.toString().charAt(0)), v-> System.out.println(v.toString()),
                "\nConfermi l'invio (t per si'/f per no)? : ", "Scelta non possibile. Riprova.");

        if(confermaInvio)  this.gestoreNotifiche.invioNotifica(testoNotifica, gruppi, dataFine);
        else System.out.println("La notifica NON e' stata inviata.");
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

            System.out.print("\n" + fraseSelezione + ": "); scelta = "" + sc.next();
        }while(!mappaOggetti.containsKey(scelta));

        return mappaOggetti.get(scelta);
    }
}