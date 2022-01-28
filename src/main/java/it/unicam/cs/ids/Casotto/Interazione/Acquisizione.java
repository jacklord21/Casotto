package it.unicam.cs.ids.Casotto.Interazione;

import it.unicam.cs.ids.Casotto.Classi.Durata;
import it.unicam.cs.ids.Casotto.Classi.Livello;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Acquisizione {


    private static final Scanner sc = new Scanner(System.in);

    public static double acquisizionePrezzo(String descrizionePrezzo) {

        String prezzo;

        do {
            System.out.print("Inserisci il prezzo " + descrizionePrezzo + ": ");
            try {
                prezzo = sc.next();
                break;
            }
            catch(DateTimeParseException e) { System.out.println("Errore nel formato della data: deve essere del tipo YYYY-mm-dd. Riprova."); }
        } while(true);

        return Double.parseDouble(prezzo);
    }

    public static int acquisizioneMese(String tipoMese) {

        String mese = "";
        Set<String> mesi = IntStream.range(1, 13).mapToObj(String::valueOf).collect(Collectors.toSet());

        do {
            if(!mese.isEmpty())
                System.out.println("Errore: il mese digitato non è valido. Riprova.");

            System.out.print("Inserisci il mese di " + tipoMese + ": ");
        }while(!mesi.contains(mese=sc.next()));

        return Integer.parseInt(mese);
    }

    public static String acquisizioneCredenziali(String credName) {
        System.out.print("Inserisci " + credName + ": ");
        return sc.next();
    }


    public static LocalDate acquisizioneData(String tipoData) {
        LocalDate date;

        do {
            System.out.print("Inserisci la data " + tipoData + ": ");
            try {
                date = LocalDate.parse(sc.next());
                break;
            }
            catch(DateTimeParseException e) { System.out.println("Errore nel formato della data: deve essere del tipo YYYY-mm-dd. Riprova."); }
        } while(true);

        return date;
    }

    public static int acquisizioneLettiniSdraie(int oggettiDisponibili, String descrizione, BiPredicate<Integer, Integer> predicato) {
        System.out.print("Inserisci il numero desiderato di " + descrizione + " (max. " + oggettiDisponibili + "): ");
        int oggettiScelti = sc.nextInt();
        while(predicato.test(oggettiScelti, oggettiDisponibili)) {
            System.out.println("Errore: il numero di " + descrizione + " inserito non è valido. Riprova.");
            System.out.print("Inserisci il numero desiderato di " + descrizione + " (max. " + oggettiDisponibili + "): ");
            oggettiScelti = sc.nextInt();
        }

        return oggettiScelti;
    }


    public static Durata acquisizioneDurata(String descrizioneDurata) {
        String ris = "";
        List<String> possibleLevels = Arrays.stream(Durata.values()).map(Enum::name).collect(Collectors.toList());

        do {
            if(!ris.isEmpty())
                System.out.println("Errore: la durata selezionata non e' prevista. Prova ancora.");

            System.out.println("\nDurate previste: ");
            possibleLevels.forEach(System.out::println);
            System.out.print("\nSeleziona la durata temporale " + descrizioneDurata + ": ");
            ris = sc.next();
        }
        while(!possibleLevels.contains(ris));

        return Durata.valueOf(ris);
    }


    public static Livello acquisizioneLivello() {

       String ris = "";
       List<String> possibleLevels = Arrays.stream(Livello.values()).map(Livello::name).collect(Collectors.toList());

        do {
            if(!ris.isEmpty())
                System.out.println("Errore: il livello selezionato non è valido. Riprova.");

            System.out.println("\nLivelli: ");
            possibleLevels.forEach(System.out::println);
            System.out.print("\nDigita il livello: ");
            ris = sc.next();
        }
        while(!possibleLevels.contains(ris));

        return Livello.valueOf(ris);
    }
}