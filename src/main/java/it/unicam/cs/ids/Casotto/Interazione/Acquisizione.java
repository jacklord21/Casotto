package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe che permette di effettuare l'acquisizione di valori di tipi diversi
 *
 */
public class Acquisizione {

    private static final Scanner sc = new Scanner(System.in);

    /**
     * Metodo per acquisire un numero con la virgola. Il metodo stampa la stringa "Inserisci ", seguita
     * dalla descrizione del numero, seguita dalla stringa ": ". (e.s.: "Inserisci un numero: ")
     *
     * @param descNumeroConVirgola descrizione del numero con la virgola da acquisire
     * @return il numero con la virgola che &egrave; stato inserito
     */
    public static Double acqDouble(String descNumeroConVirgola, boolean puoEssereNull) {
        return acqValore("Inserisci " + descNumeroConVirgola + ": ",
                "Errore: il numero digitato e' errato. Riprova: ", puoEssereNull, Double::parseDouble);
    }

    /**
     * Metodo per acquisire un numero intero. Il metodo stampa la stringa "Inserisci ", seguita
     * dalla descrizione del numero, seguita dalla stringa ": ". (e.s.: "Inserisci un numero: ")
     *
     * @param descIntero descrizione del numero intero da acquisire
     * @return il numero intero che &egrave; stato inserito
     */
    public static Integer acqIntero(String descIntero, boolean puoEssereNull) {
        return acqValore("Inserisci " + descIntero + ": ",
                "Errore: numero intero NON valido. Riprova: ", puoEssereNull, Integer::parseInt);
    }

    /**
     * Metodo per acquisire una {@link LocalDate}. Il metodo stampa la stringa "Inserisci la data ", seguita
     * dalla descrizione della data, seguita dalla stringa " (YYYY-mm-dd): ". (e.s.: "Inserisci la data di compleanno (YYYY-mm-dd): ")
     *
     * @param descData descrizione della data da acquisire
     * @return la {@link LocalDate} che &egrave; stata inserita
     */
    public static LocalDate acqData(String descData, boolean puoEssereNull) {
        return acqValore("Inserisci la data " + descData + " (YYYY-mm-dd): ",
                "Errore nel formato della data: deve essere del tipo YYYY-mm-dd. Riprova: ", puoEssereNull, LocalDate::parse);
    }

    /**
     * Metodo per acquisire una {@link String}. Il metodo stampa la stringa "Inserisci ", seguita
     * dalla descrizione della stringa, seguita dalla stringa ": ". (e.s.: "Inserisci il nome: ")
     *
     * @param descStringa descrizione della stringa da acquisire
     * @return la {@link String} che &egrave; stata inserita
     */
    public static String acqStringa(String descStringa, boolean puoEssereNull) {
        return acqValore("Inserisci " + descStringa + ": ", "", puoEssereNull, s->s);
    }

    private static <T> T acqValore(String fraseAcquisizione, String fraseErrore, boolean puoEssereNull, Function<String, T> funzioneConversioneValore) {
        T ris;

        String val;
        System.out.print(fraseAcquisizione);
        val = sc.nextLine();

        if(puoEssereNull && val.equals("")) return null;
            do {
                try {
                    ris = funzioneConversioneValore.apply(val);
                    break;
                }
                catch(Exception e) { System.out.print(fraseErrore); val = sc.nextLine(); }
            } while(true);

        return ris;
    }

    /**
     * Metodo per acquisire il numero di lettini e sdraie
     *
     * @param oggettiDisponibili numero massimo di lettini o sdraie disponibili
     * @param descrizione descrizione dell'oggetto del quale acquisire la quantit&agrave;
     * @param predicato {@link java.util.function.Predicate} per verificare se la quantit&agrave; inserita &egrave; valida
     * @return il numero di lettini o sdraie
     */
    public static int acqLettiniSdraie(int oggettiDisponibili, String descrizione, BiPredicate<Integer, Integer> predicato) {
        int oggettiScelti = acqIntero("il numero desiderato di " + descrizione + " (max. " + oggettiDisponibili + "): ", false);

        while(predicato.test(oggettiScelti, oggettiDisponibili)) {
            System.out.println("Errore: il numero di " + descrizione + " inserito non è valido. Riprova.");
            System.out.print("Inserisci il numero desiderato di " + descrizione + " (max. " + oggettiDisponibili + "): ");
            oggettiScelti = sc.nextInt();
        }

        return oggettiScelti;
    }

    /**
     * Metodo per acquisire la {@link Durata}. Il metodo stampa la stringa "\nDigita la durata ", seguita
     * dalla descrizione della durata, seguita dalla stringa ": ". (e.s.: "Inserisci la durata della prenotazione: ")
     *
     * @param descDurata descrizione della {@link Durata} da acquisire
     * @return la {@link Durata} che &egrave; stato selezionata
     */
    public static Durata acqDurata(String descDurata) {
        return acqEnum("la durata " + descDurata, "Errore: la durata selezionata non è valido. Riprova.",
                Arrays.stream(Durata.values()).map(Durata::name).collect(Collectors.toList()), Durata::valueOf);
    }

    /**
     * Metodo per acquisire il {@link Livello}. Il metodo stampa la stringa "\nDigita il livello ", seguita
     * dalla descrizione del livello, seguita dalla stringa ": ". (e.s.: "Digita il livello dell'account: ")
     *
     * @param descLivello descrizione del {@link Livello} da acquisire
     * @return il {@link Livello} che &egrave; stato selezionato
     */
    public static Livello acqLivello(String descLivello) {
        return acqEnum("il livello " + descLivello, "Errore: il livello selezionato non è valido. Riprova.",
                Arrays.stream(Livello.values()).map(Livello::name).collect(Collectors.toList()), Livello::valueOf);
    }

    /**
     * Metodo per acquisire il {@link Tipo}. Il metodo stampa la stringa "\nDigita il tipo ", seguita
     * dalla descrizione del tipo, seguita dalla stringa ": ". (e.s.: "Digita il tipo del prodotto: ")
     *
     * @param descTipo descrizione del {@link Tipo} da acquisire
     * @return il {@link Tipo} che &egrave; stato selezionato
     */
    public static Tipo acqTipo(String descTipo) {
       return acqEnum("il tipo " + descTipo, "Errore: il tipo selezionato non è valido. Riprova.",
               Arrays.stream(Tipo.values()).map(Tipo::name).collect(Collectors.toList()), Tipo::valueOf);
    }

    private static <T> T acqEnum(String fraseAcquisizione, String fraseErrore, List<String> possibiliValori, Function<String, T> funzioneValoreDiRitorno) {
        String ris = "";

        do {
            if(!ris.isEmpty())
                System.out.println(fraseErrore);

            System.out.println("\nValori: ");
            possibiliValori.forEach(System.out::println);

            System.out.print("\nDigita " + fraseAcquisizione + ": ");
            ris = sc.nextLine().toUpperCase(Locale.ROOT);
        }
        while(!possibiliValori.contains(ris));

        return funzioneValoreDiRitorno.apply(ris);
    }

    /**
     * Metodo per acquisire i parametri dell'{@link Attivita}
     *
     * @param ga {@link GestoreAttivita} per creare un oggetto di tipo {@link Attivita} con i parametri acquisiti
     * @return una {@link Map} contenente come 'chiave' l'{@link Attivita} creata, mentre come 'valore' un booleano che
     *         indica se quell'{@link Attivita} debba essere cancellata o meno
     */
    public static Map<Attivita, Boolean> acqParamAttivita(GestoreAttivita ga) {

        String nome = acqStringa("il nome dell'attivita'", false);
        LocalDate data = Acquisizione.acqData("dell'attivita'", false);
        int partecipanti = acqIntero("il numero massimo di partecipanti", false);

        return Map.of(ga.createAttivita(nome, data, partecipanti), false);
    }

    /**
     * Metodo per acquisire i parametri del {@link Prodotto}
     *
     * @param gp {@link GestoreProdotti} per creare un oggetto di tipo {@link Prodotto} con i parametri acquisiti
     * @return una {@link Map} contenente come 'chiave' il {@link Prodotto} creato, mentre come 'valore' un booleano che
     *         indica se quel {@link Prodotto} debba essere cancellato o meno
     */
    public static Map<Prodotto, Boolean> acqParamProdotto(GestoreProdotti gp) {
        sc.nextLine();

        String nome = acqStringa("il nome del prodotto", false);
        double prezzo = acqDouble("il prezzo del prodotto", false);
        int quantita = acqIntero("la quantita' del prodotto", false);
        Tipo tipo = acqTipo("del prodotto");

        return Map.of(gp.creazioneProdotto(nome, prezzo, quantita, tipo), false);
    }

    /**
     * Metodo per acquisire i parametri del {@link Prezzo}
     *
     * @param spiaggia {@link Spiaggia} per creare un oggetto di tipo {@link Prezzo} con i parametri acquisiti
     * @return il nuovo {@link Prezzo}, che prima viene memorizzato sul database
     */
    public static Prezzo acqParamPrezzo(Spiaggia spiaggia) {
        double prezzo = acqDouble("il prezzo", false);
        Integer meseInizio = acqIntero("il mese di inizio validita'", true);
        Integer meseFine = acqIntero("il mese di fine validita'", true);

        LocalDate dataInizio = acqData("la data di inizio validita'", meseInizio != null && meseFine != null);
        LocalDate dataFine = acqData("la data di fine validita'", meseInizio != null && meseFine != null);
        Durata durata = acqDurata("del prezzo");

        return spiaggia.addPrezzo(prezzo, meseInizio, meseFine, dataInizio, dataFine, durata);
    }

    public static <T> T scelta(Collection<T> listaOggetti, Function<T, String> funzioneChiaveMappa, Consumer<T> consumerStampaOggetti, String fraseSelezione, String fraseErrore) {
        String scelta = "";
        listaOggetti.forEach(consumerStampaOggetti);
        Map<String, T> mappaOggetti = listaOggetti.stream().collect(Collectors.toMap(funzioneChiaveMappa, o->o));

        do {
            if(!scelta.isEmpty()) System.out.println(fraseErrore);

            System.out.print("\n" + fraseSelezione + ": "); scelta = "" + sc.nextLine();
        }while(!mappaOggetti.containsKey(scelta));

        return mappaOggetti.get(scelta);
    }
}