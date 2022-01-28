package it.unicam.cs.ids.Casotto;

import it.unicam.cs.ids.Casotto.Classi.*;
import it.unicam.cs.ids.Casotto.Interazione.InteractionManager;
import it.unicam.cs.ids.Casotto.Interazione.Menu;
import it.unicam.cs.ids.Casotto.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@SpringBootApplication
public class CasottoApplication {

	@Autowired
	InteractionManager im;

	@Autowired
	OmbrelloneRepository or;

	@Autowired
	GestoreProdotti gestoreProdotti;

	@Autowired
	AccountRepository accountRepository;

	public static void main(String[] args)  {
		SpringApplication.run(CasottoApplication.class, args);
	}

	@Bean
	public CommandLineRunner mappingDemo(AccountRepository ar, PrenotazioniRepository pr, UtenteRepository ur, OmbrelloneRepository or, PrezzoRepository prr) {

		return args -> {

			String scelta = "";
			Scanner sc = new Scanner(System.in);
			Map<String, Map<String, Runnable>> menu = cambiaMenu(this.im.getAccount()).getMenu(this.im);

			while (true) {
				System.out.println("\n\n\nMenu'\n");
				for (Map.Entry<String, Map<String, Runnable>> e : menu.entrySet())
					System.out.println(e.getKey() + ". " + Objects.requireNonNull(e.getValue().entrySet().stream().findFirst().orElse(null)).getKey());

				do {
					if (!scelta.isEmpty())
						System.out.print("Scelta NON valida. Riprova: ");

					System.out.print("\nInserisci un numero: ");
					scelta = sc.next();
				}
				while (!menu.containsKey(scelta));

				Objects.requireNonNull(menu.get(scelta).entrySet().stream().findFirst().orElse(null)).getValue().run();
				menu = cambiaMenu(this.im.getAccount()).getMenu(this.im);
				scelta = "";
			}
		};
	}

	private Menu cambiaMenu(Account account) {
		return (account==null) ? Menu::menuInizio : (account.getLivello()==Livello.CLIENTE) ? Menu::menuCliente : Menu::menuGestore;
	}
}