package com.nexusgesti.tenant.domain.port.out;

import com.nexusgesti.tenant.domain.model.AnneeScolaire;
import com.nexusgesti.tenant.domain.model.Periode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnneeRepository {

    AnneeScolaire save(AnneeScolaire annee);

    Optional<AnneeScolaire> findById(UUID id);

    List<AnneeScolaire> findByEtablissement(UUID etablissementId);

    Periode savePeriode(Periode periode);

    Optional<Periode> findPeriode(UUID anneeId, UUID periodeId);

    List<Periode> findPeriodes(UUID anneeId);
}