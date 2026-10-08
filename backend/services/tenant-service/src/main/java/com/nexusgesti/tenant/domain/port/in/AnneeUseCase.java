package com.nexusgesti.tenant.domain.port.in;

import com.nexusgesti.tenant.domain.model.AnneeScolaire;
import com.nexusgesti.tenant.domain.model.Periode;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AnneeUseCase {

    AnneeScolaire creer(UUID etablissementId, AnneeScolaire annee);

    AnneeScolaire get(UUID etablissementId, UUID anneeId);

    AnneeScolaire update(UUID etablissementId, UUID anneeId, AnneeScolaire annee);

    List<AnneeScolaire> list(UUID etablissementId);

    AnneeScolaire ouvrir(UUID etablissementId, UUID anneeId);

    AnneeScolaire cloturer(UUID etablissementId, UUID anneeId, Date date);

    Periode addPeriode(UUID etablissementId, UUID anneeId, Periode periode);

    List<Periode> listPeriodes(UUID etablissementId, UUID anneeId);

    Periode cloturerPeriode(UUID etablissementId, UUID anneeId, UUID periodeId);

    AnneeScolaire courante(UUID etablissementId);
}