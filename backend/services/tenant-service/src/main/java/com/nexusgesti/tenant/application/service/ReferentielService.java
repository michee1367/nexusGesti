package com.nexusgesti.tenant.application.service;

import com.nexusgesti.tenant.domain.TenantException;
import com.nexusgesti.tenant.domain.model.Referentiel;
import com.nexusgesti.tenant.domain.model.ReferentielItem;
import com.nexusgesti.tenant.domain.port.in.ReferentielUseCase;
import com.nexusgesti.tenant.domain.port.out.EtablissementRepository;
import com.nexusgesti.tenant.domain.port.out.ReferentielRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReferentielService implements ReferentielUseCase {

    private final ReferentielRepository referentielRepository;
    private final EtablissementRepository etablissementRepository;

    public ReferentielService(ReferentielRepository referentielRepository,
                              EtablissementRepository etablissementRepository) {
        this.referentielRepository = referentielRepository;
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public Referentiel get(UUID etablissementId, UUID referentielId) {
        verifierEtablissement(etablissementId);
        Referentiel referentiel = referentielRepository.findById(referentielId)
                .orElseThrow(() -> TenantException.notFound("Referentiel", referentielId));
        if (!etablissementId.equals(referentiel.getEtablissementId())) {
            throw TenantException.notFound("Referentiel", referentielId);
        }
        return referentiel;
    }

    @Override
    public Referentiel getByCode(UUID etablissementId, String code) {
        verifierEtablissement(etablissementId);
        return referentielRepository.findByEtablissementAndCode(etablissementId, code)
                .orElseThrow(() -> TenantException.notFound("Referentiel", null));
    }

    @Override
    public List<Referentiel> list(UUID etablissementId) {
        verifierEtablissement(etablissementId);
        return referentielRepository.findByEtablissement(etablissementId);
    }

    @Override
    @Transactional
    public Referentiel addItem(UUID etablissementId, UUID referentielId, ReferentielItem item) {
        Referentiel referentiel = get(etablissementId, referentielId);
        item.setIdItem(UUID.randomUUID());
        item.setReferentielId(referentielId);
        if (item.isActif()) {
            referentiel.getItems().stream()
                    .filter(i -> i.getCode().equals(item.getCode()))
                    .findFirst()
                    .ifPresent(ignore -> {
                        throw TenantException.conflit("ITEM_EXISTANT", "Item deja present : " + item.getCode());
                    });
        }
        item.setOrdre(referentiel.getItems().size());
        referentielRepository.saveItem(item);
        return get(etablissementId, referentielId);
    }

    @Override
    @Transactional
    public Referentiel desactiverItem(UUID etablissementId, UUID referentielId, UUID itemId) {
        Referentiel referentiel = get(etablissementId, referentielId);
        ReferentielItem item = referentiel.getItems().stream()
                .filter(i -> i.getIdItem().equals(itemId))
                .findFirst()
                .orElseThrow(() -> TenantException.notFound("ReferentielItem", itemId));
        item.setActif(false);
        referentielRepository.saveItem(item);
        return get(etablissementId, referentielId);
    }

    private void verifierEtablissement(UUID etablissementId) {
        if (!etablissementRepository.existsById(etablissementId)) {
            throw TenantException.notFound("Etablissement", etablissementId);
        }
    }
}