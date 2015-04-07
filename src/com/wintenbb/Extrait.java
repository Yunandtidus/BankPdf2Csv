package com.wintenbb;

import java.util.Map;
import java.util.TreeMap;

public class Extrait {
	private Map<String, Compte> comptes = new TreeMap<String, Compte>();

	public Map<String, Compte> getComptes() {
		return comptes;
	}

	public void setComptes(Map<String, Compte> comptes) {
		this.comptes = comptes;
	}

	@Override
	public String toString() {
		return comptes.toString();
	}
}
