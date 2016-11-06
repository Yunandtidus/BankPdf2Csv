package com.wintenbb.bankManager.business;

import com.wintenbb.bankManager.domain.AccountLine;

public class Organizer {

	private static Organizer instance;

	private Organizer() {

	}

	public static Organizer getInstance() {
		if (instance == null) {
			instance = new Organizer();
		}
		return instance;
	}

	public String lineType(AccountLine l) {
		// return
		// ";logement;Voiture;Loisirs;Nourriture;Santé;Divers;Impots;Banque;Revenus";
		if (l.getLabel().contains("PRLV NVO ")) {
			return ";Loyer;;;;;;;;";
		} else if (l.getFullLabel().contains(" CENTURY 21 ")) {
			return ";Loyer;;;;;;;;";
		} else if (l.getFullLabel().contains(" EDF ")) {
			return ";Electricité;;;;;;;;";
		} else if (l.getFullLabel().contains(" FREE HAUTDEBIT ")) {
			return ";Internet;;;;;;;;";
		} else if (l.getFullLabel().contains(" FREE MOBILE")) {
			return ";Téléphone;;;;;;;;";
		} else if (l.getFullLabel().contains(" APRR ")) {
			return ";;Péage;;;;;;;";
		} else if (l.getFullLabel().contains(" PARIS RHIN RH ")) {
			return ";;Péage;;;;;;;";
		} else if (l.getFullLabel().contains(" S/S ")) {
			return ";;Essence;;;;;;;";
		} else if (l.getFullLabel().contains(" CARBURANT ")) {
			return ";;Essence;;;;;;;";
		} else if (l.getFullLabel().contains(" DAC ")) {
			// distributeur automatique de carburant
			return ";;Essence;;;;;;;";
		} else if (l.getFullLabel().contains("RETRAIT DAB ")) {
			return ";;;;;;Autres;;;";
		} else if (l.getFullLabel().contains(" PARADISO ")) {
			return ";;;;Restaurant;;;;;";
		} else if (l.getFullLabel().contains(" CLAFOUTIS ")) {
			return ";;;;Restaurant;;;;;";
		} else if (l.getFullLabel().contains(" IMPOT ")) {
			return ";;;;;;;Impots;;";
		} else if (l.getFullLabel().contains("F COTIS ")) {
			return ";;;;;;;;Frais banquaires;";
		} else if (l.getFullLabel().contains("ECH PRET ")) {
			return ";;;;;;;;Frais banquaires;";
		} else if (l.getFullLabel().contains(" SYSBELF ")) {
			return ";;;;;;;;;Paye";
		} else if (l.getFullLabel().contains(" DECATHLON ")) {
			return ";;;Sport;;;;;;";
		} else if (l.getFullLabel().contains(" AUCHAN ")) {
			return ";;;;Courses;;;;;";
		} else if (l.getFullLabel().contains("LECLERC ")) {
			return ";;;;Courses;;;;;";
		} else if (l.getFullLabel().contains(" SAS BELFI")) {
			return ";;;Courses;;;;;;";
		} else if (l.getFullLabel().contains(" INTERMARCHE ")) {
			return ";;;;Courses;;;;;";
		} else if (l.getFullLabel().contains(" CORA ")) {
			return ";;;;Courses;;;;;";
		}
		return "";
	}
}
