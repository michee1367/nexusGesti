package com.nexusgesti.tenant.domain.port.out;

import com.nexusgesti.tenant.domain.model.Etablissement;

import java.util.Optional;
import java.util.UUID;

public interface EtablissementRepository {

    Etablissement save(Etablissement etablissement);

    Optional<Etablissement> findById(UUID id);

    boolean existsById(UUID id);
}