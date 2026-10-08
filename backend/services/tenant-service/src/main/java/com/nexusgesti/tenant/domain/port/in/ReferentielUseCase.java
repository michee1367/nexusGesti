package com.nexusgesti.tenant.domain.port.in;

import com.nexusgesti.tenant.domain.model.Referentiel;
import com.nexusgesti.tenant.domain.model.ReferentielItem;

import java.util.List;
import java.util.UUID;

public interface ReferentielUseCase {

    Referentiel get(UUID etablissementId, UUID referentielId);

    Referentiel getByCode(UUID etablissementId, String code);

    List<Referentiel> list(UUID etablissementId);

    Referentiel addItem(UUID etablissementId, UUID referentielId, ReferentielItem item);

    Referentiel desactiverItem(UUID etablissementId, UUID referentielId, UUID itemId);
}