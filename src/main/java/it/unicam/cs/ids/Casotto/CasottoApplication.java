package it.unicam.cs.ids.Casotto;

import it.unicam.cs.ids.Casotto.Classi.*;
import it.unicam.cs.ids.Casotto.Repository.AccountRepository;
import it.unicam.cs.ids.Casotto.Repository.OmbrelloneRepository;
import it.unicam.cs.ids.Casotto.Repository.PrenotazioniRepository;
import it.unicam.cs.ids.Casotto.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
@SpringBootApplication
public class CasottoApplication {

	@Autowired
	InteractionManager im;

	private static int menu() {

		int choice;
		Scanner sc = new Scanner(System.in);

		System.out.println("\nMenu'\n");

		System.out.println("1. Registrazione");
		System.out.println("2. Login");
		System.out.println("3. Logout");
		System.out.println("4. Effettua una prenotazione");
		System.out.println("5. Visualizza storico prenotazione");
		System.out.println("6. Visualizza prenotazioni attive");
		System.out.println("0. Esci");

		System.out.print("\nInsert a number: ");
		choice = sc.nextInt();
		return choice;
	}


	public static void main(String[] args)  {
		SpringApplication.run(CasottoApplication.class, args);
	}

	@Bean
	public CommandLineRunner mappingDemo(AccountRepository ar, PrenotazioniRepository pr, UtenteRepository ur, OmbrelloneRepository or) {

		return args -> {

			int choice = menu();

			while(true) {
				switch (choice) {
					case 1: im.registration();
						break;
					case 2: im.login();
						break;
					case 3: im.logout();
						break;
					case 4:
					case 5:
					case 6:
					case 0: System.exit(0);
					default:
						System.err.println("Error: invalid choice!!!");
				}
				choice = menu();
			}
		};
	}
}