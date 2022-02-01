package it.unicam.cs.ids.Casotto.Interazione;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe che rappresenta i vari Men&ugrave;
 *
 */
@Controller
public class Menu
{
    @Autowired private InteractionManager im;
    @Autowired private InteractionManagerAddettoSpiaggia imAddettoSpiaggia;
    @Autowired private InteractionManagerBarista imBarista;
    @Autowired private InteractionManagerGestore imGestore;


    /**
     * Metodo che permette di ottenere il Men&ugarve; iniziale del programma
     *
     * @return una {@link Map} contenente, per ogni metodo, una sua descrizione come chiave, e un riferimento
     * a esso come valore
     */
    public Map<String, Runnable> menuInizio() {
        Map<String, Runnable> menu = new LinkedHashMap<>();

        menu.put("Esci", () -> System.exit(0));
        menu.put("Registrazione", ()->this.im.registrazione());
        menu.put("Login",()->this.im.login());

        return menu;
    }

    /**
     * Metodo che permette di ottenere il Men&ugarve; del cliente
     *
     * @return una {@link Map} contenente, per ogni metodo, una sua descrizione come chiave, e un riferimento
     * a esso come valore
     */
    public Map<String, Runnable> menuCliente() {
        Map<String, Runnable> menu = new LinkedHashMap<>();

        menu.put("Esci", () -> System.exit(0));
        menu.put("Effettua una prenotazione", ()->this.im.prenotaSpiaggia());
        menu.put("Cancella una prenotazione", ()->this.im.cancellaPrenotazione());
        menu.put("Visualizza storico prenotazioni", ()->this.im.visualizzaStoricoPrenotazioni());
        menu.put("Visualizza prenotazioni attive", ()->this.im.visualizzaPrenotazioniCorrenti());
        menu.put("Acquista prodotti", ()->this.im.acquistaProdotto());
        menu.put("Prenota attivita'", ()->this.im.prenotazioneAttivita());
        menu.put("Cancella prenotazione attivita'", ()->this.im.cancellazionePrenotazioneAttivita());
        menu.put("Notifica Reclamo", ()->this.im.notificaReclami());
        menu.put("Modifica Dati", ()->this.im.modificaDati());
        menu.put("Logout", ()->this.im.logout());

        return menu;
    }

    /**
     * Metodo che permette di ottenere il Men&ugarve; dell'addetto spiaggia
     *
     * @return una {@link Map} contenente, per ogni metodo, una sua descrizione come chiave, e un riferimento
     * a esso come valore
     */
    public Map<String, Runnable> menuAddettoSpiaggia() {
        Map<String, Runnable> menu = new LinkedHashMap<>();

        menu.put("Esci", () -> System.exit(0));
        menu.put("Pagamento ordinazione", ()->this.imAddettoSpiaggia.pagamentoOrdinazione());
        menu.put("Consegna comanda con scontrino", ()->this.imAddettoSpiaggia.consegnaOrdinazioneConScontrino());
        menu.put("Modifica Dati", ()->this.im.modificaDati());
        menu.put("Logout", ()->this.im.logout());

        return menu;
    }

    /**
     * Metodo che permette di ottenere il Men&ugarve; del barista
     *
     * @return una {@link Map} contenente, per ogni metodo, una sua descrizione come chiave, e un riferimento
     * a esso come valore
     */
    public Map<String, Runnable> menuBarista() {
        Map<String, Runnable> menu = new LinkedHashMap<>();

        menu.put("Esci", () -> System.exit(0));
        menu.put("Inizia una preparazione", ()->this.imBarista.inizioPreparazione());
        menu.put("Termina una preparazione", ()->this.imBarista.finePreparazione());
        menu.put("Modifica Dati", ()->this.im.modificaDati());
        menu.put("Notifica Problema", ()->this.im.notificaProblemi());
        menu.put("Logout", ()->this.im.logout());

        return menu;
    }

    /**
     * Metodo che permette di ottenere il Men&ugarve; del gestore
     *
     * @return una {@link Map} contenente, per ogni metodo, una sua descrizione come chiave, e un riferimento
     * a esso come valore
     */
    public Map<String, Runnable> menuGestore() {
        Map<String, Runnable> menu = new LinkedHashMap<>();

        menu.put("Esci", () -> System.exit(0));
        menu.put("Gestisci struttura", ()->this.imGestore.gestioneStruttura());
        menu.put("Modifica Dati", ()->this.im.modificaDati());
        menu.put("Invia Notifiche", ()->this.imGestore.invioNotifiche());
        menu.put("Modifica livello account", ()->this.imGestore.modificaLivello());
        menu.put("Inserisci un nuovo prezzo", ()->this.imGestore.inserisciPrezzo());
        menu.put("Logout", ()->this.im.logout());

        return menu;
    }
}