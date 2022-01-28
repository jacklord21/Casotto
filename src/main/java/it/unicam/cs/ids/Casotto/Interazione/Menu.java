package it.unicam.cs.ids.Casotto.Interazione;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@FunctionalInterface
public interface Menu
{
    Map<String, Map<String, Runnable>> getMenu(InteractionManager im);

    static Map<String, Map<String, Runnable>> menuInizio(InteractionManager im) {
        Map<String, Map<String, Runnable>> menu = new HashMap<>();

        menu.put("1", new HashMap<>()); menu.get("1").put("Registrazione", im::registrazione);
        menu.put("2", new HashMap<>()); menu.get("2").put("Login", im::login);
        menu.put("0", new HashMap<>()); menu.get("0").put("Esci", () -> System.exit(0));

        return menu;
    }

    static Map<String, Map<String, Runnable>> menuCliente(InteractionManager im) {
        Map<String, Map<String, Runnable>> menu = new HashMap<>();

        menu.put("1", new HashMap<>()); menu.get("1").put("Effettua una prenotazione", im::prenotaSpiaggia);
        menu.put("2", new HashMap<>()); menu.get("2").put("Cancella una prenotazione", im::cancellaPrenotazione);
        menu.put("3", new HashMap<>()); menu.get("3").put("Visualizza storico prenotazione", im::visualizzaStoricoPrenotazioni);
        menu.put("4", new HashMap<>()); menu.get("4").put("Visualizza prenotazioni attive", im::visualizzaPrenotazioniCorrenti);
        menu.put("5", new HashMap<>()); menu.get("5").put("Acquista prodotti", im::acquistaProdotto);
        menu.put("6", new HashMap<>()); menu.get("6").put("Prenota attivita'", im::prenotazioneAttivita);
        menu.put("7", new HashMap<>()); menu.get("7").put("Cancella prenotazione attivita'", im::cancellazionePrenotazioneAttivita);
        menu.put("8", new HashMap<>()); menu.get("8").put("Modifica Dati", im::modificaDati);
        menu.put("9", new HashMap<>()); menu.get("9").put("Logout", im::logout);
        menu.put("0", new HashMap<>()); menu.get("0").put("Esci", () -> System.exit(0));

        return menu;
    }

    static Map<String, Map<String, Runnable>> menuAddettoSpiaggia(InteractionManager im) {
        Map<String, Map<String, Runnable>> menu = new HashMap<>();

        menu.put("1", new HashMap<>()); menu.get("1").put("Pagamento ordinazione", im::pagamentoOrdinazione);
        menu.put("2", new HashMap<>()); menu.get("2").put("Consegna comanda con scontrino", im::consegnaComandaConScontrino);
        menu.put("3", new HashMap<>()); menu.get("3").put("Modifica Dati", im::modificaDati);
        menu.put("4", new HashMap<>()); menu.get("4").put("Logout", im::logout);
        menu.put("0", new HashMap<>()); menu.get("0").put("Esci", () -> System.exit(0));

        return menu;
    }

    static Map<String, Map<String, Runnable>> menuBarista(InteractionManager im) {
        Map<String, Map<String, Runnable>> menu = new HashMap<>();

        menu.put("1", new HashMap<>()); menu.get("1").put("Inizia una preparazione", im::inizioPreparazione);
        menu.put("2", new HashMap<>()); menu.get("2").put("Termina una preparazione", im::finePreparazione);
        menu.put("3", new HashMap<>()); menu.get("3").put("Modifica Dati", im::modificaDati);
        menu.put("4", new HashMap<>()); menu.get("4").put("Logout", im::logout);
        menu.put("0", new HashMap<>()); menu.get("0").put("Esci", () -> System.exit(0));

        return menu;
    }

    static Map<String, Map<String, Runnable>> menuGestore(InteractionManager im) {
        Map<String, Map<String, Runnable>> menu = new HashMap<>();

        menu.put("1", new HashMap<>()); menu.get("1").put("Inserisci un nuovo prezzo", im::inserisciPrezzoPerOmbrellone);
        menu.put("2", new HashMap<>()); menu.get("2").put("Imposta prezzo ombrellone", im::impostaPrezzoOmbrellone);
        menu.put("3", new HashMap<>()); menu.get("3").put("Modifica Dati", im::modificaDati);
        menu.put("4", new HashMap<>()); menu.get("4").put("Logout", im::logout);
        menu.put("0", new HashMap<>()); menu.get("0").put("Esci", () -> System.exit(0));

        return menu;
    }


}
