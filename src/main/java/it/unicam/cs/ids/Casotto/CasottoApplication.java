package it.unicam.cs.ids.Casotto;

import it.unicam.cs.ids.Casotto.Classi.*;
import it.unicam.cs.ids.Casotto.Interazione.Acquisizione;
import it.unicam.cs.ids.Casotto.Interazione.InteractionManager;
import it.unicam.cs.ids.Casotto.Interazione.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Classe di partenza del programma
 */
@Service
@SpringBootApplication
public class CasottoApplication {
	@Autowired
	private InteractionManager im;

	@Autowired
	private Menu menu;

	public static void main(String[] args)  {
		SpringApplication.run(CasottoApplication.class, args);
	}

	@Bean
	public CommandLineRunner mappingDemo() {
		return args -> {
			int counter=0, indice = Integer.MIN_VALUE;
			Map<String, Runnable> menu = this.menu.menuInizio();

			while (true) {
				System.out.println("\n\nMenu'\n");
				for(String descrizione : menu.keySet()) System.out.println(counter++ + ". " + descrizione);
				System.out.println("\n");

				do {
					if (indice!=Integer.MIN_VALUE) System.out.print("Scelta NON valida. Riprova.\n");

					indice = Acquisizione.acqIntero("un numero", false);
				} while (indice<0 || indice>= menu.size());

				menu.get(new ArrayList<>(menu.keySet()).get(indice)).run();
				menu = cambiaMenu(this.im.getAccount());
				counter=0; indice = Integer.MIN_VALUE;
			}
		};
	}

	/**
	 * Metodo che permette di cambiare il men&ugrave; in base al {@link Livello} dell'{@link Account} passato
	 * come parametro
	 *
	 * @param account {@link Account} del quale catturare il livello
	 * @return il nuovo men&ugrave; in base al livello dell'{@link Account} passato, o il men&ugrave; iniziale se
	 * 		   l'{@link Account} passato &egrave; nullo
	 */
	private Map<String, Runnable> cambiaMenu(Account account) {
		return (account==null) ? this.menu.menuInizio() :
				(account.getLivello()==Livello.CLIENTE) ? this.menu.menuCliente() :
						(account.getLivello()==Livello.ADDETTO_SPIAGGIA) ? this.menu.menuAddettoSpiaggia() :
								(account.getLivello()==Livello.BARISTA) ? this.menu.menuBarista() : this.menu.menuGestore();
	}
}