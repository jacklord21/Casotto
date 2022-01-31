package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Acquisizione {

    private static final Scanner sc = new Scanner(System.in);

    public static double acqDouble(String descNumeroConVirgola) {
        return acqValore("Inserisci " + descNumeroConVirgola + ": ",
                "Errore: il numero digitato e' errato. Riprova.", Double::parseDouble);
    }

    public static int acqIntero(String descIntero) {
        return acqValore("Inserisci " + descIntero + ": ",
                "Errore: numero intero NON valido. Riprova.", Integer::parseInt);
    }

    public static LocalDate acqData(String descData) {
        return acqValore("Inserisci la data " + descData + " (YYYY-mm-dd): ",
                "Errore nel formato della data: deve essere del tipo YYYY-mm-dd. Riprova.", LocalDate::parse);
    }

    public static String acqStringa(String descStringa) {
        return acqValore("Inserisci " + descStringa + ": ", "", s->s);
    }

    private static <T> T acqValore(String fraseAcquisizione, String fraseErrore, Function<String, T> funzioneConversioneValore) {
        T ris;

        do {
            System.out.print(fraseAcquisizione);
            try {
                ris = funzioneConversioneValore.apply(sc.nextLine());
                break;
            }
            catch(Exception e) { System.out.println(fraseErrore); }
        } while(true);

        return ris;
    }

    public static int acqLettiniSdraie(int oggettiDisponibili, String descrizione, BiPredicate<Integer, Integer> predicato) {
        int oggettiScelti = acqIntero("il numero desiderato di " + descrizione + " (max. " + oggettiDisponibili + "): ");

        while(predicato.test(oggettiScelti, oggettiDisponibili)) {
            System.out.println("Errore: il numero di " + descrizione + " inserito non è valido. Riprova.");
            System.out.print("Inserisci il numero desiderato di " + descrizione + " (max. " + oggettiDisponibili + "): ");
            oggettiScelti = sc.nextInt();
        }

        return oggettiScelti;
    }

    public static Durata acqDurata(String descDurata) {
        return acqEnum("la durata " + descDurata, "Errore: la durata selezionata non è valido. Riprova.",
                Arrays.stream(Durata.values()).map(Durata::name).collect(Collectors.toList()), Durata::valueOf);
    }

    public static Livello acqLivello(String descLivello) {
        return acqEnum("il livello " + descLivello, "Errore: il livello selezionato non è valido. Riprova.",
                Arrays.stream(Livello.values()).map(Livello::name).collect(Collectors.toList()), Livello::valueOf);
    }

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


    public static Map<Attivita, Boolean> acqParamAttivita(GestoreAttivita ga) {
        sc.nextLine();

        String nome = acqStringa("il nome dell'attivita'");
        LocalDate data = Acquisizione.acqData("dell'attivita'");
        int partecipanti = acqIntero("il numero massimo di partecipanti");

        return Map.of(ga.createAttivita(nome, data, partecipanti), false);
    }


    public static Map<Prodotto, Boolean> acqParamProdotto(GestoreProdotti gp) {
        sc.nextLine();

        String nome = acqStringa("il nome del prodotto");
        double prezzo = acqDouble("il prezzo del prodotto");
        int quantita = acqIntero("la quantita' del prodotto");
        Tipo tipo = acqTipo("del prodotto");

        return Map.of(gp.creazioneProdotto(nome, prezzo, quantita, tipo), false);
    }
}