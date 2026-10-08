package com.nexusgesti.tenant.domain.port.out;

import com.nexusgesti.tenant.domain.model.Referentiel;
import com.nexusgesti.tenant.domain.model.ReferentielItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferentielRepository {

    Referentiel save(Referentiel referentiel);

    Optional<Referentiel> findById(UUID id);

    List<Referentiel> findByEtablissement(UUID etablissementId);

    Optional<Referentiel> findByEtablissementAndCode(UUID etablissementId, String code);

    ReferentielItem saveItem(ReferentielItem item);

    Optional<ReferentielItem> findItem(UUID referentielId, UUID itemId);

    void deleteItem(UUID id, UUID referentielId);
}