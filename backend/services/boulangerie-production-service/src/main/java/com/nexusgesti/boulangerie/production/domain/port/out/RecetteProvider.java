package com.nexusgesti.boulangerie.production.domain.port.out;

import com.nexusgesti.boulangerie.production.domain.model.BesoinMatiere;

import java.math.BigDecimal;
import java.util.List;

public interface RecetteProvider {

    List<BesoinMatiere> resoudreBesoins(String recetteCode, Integer versionRecette, BigDecimal quantite);
}
