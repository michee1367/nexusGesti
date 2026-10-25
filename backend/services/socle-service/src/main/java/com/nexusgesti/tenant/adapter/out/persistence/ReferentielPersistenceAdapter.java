package com.nexusgesti.tenant.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.adapter.out.persistence.entity.ReferentielEntity;
import com.nexusgesti.tenant.adapter.out.persistence.entity.ReferentielItemEntity;
import com.nexusgesti.tenant.domain.model.NatureReferentiel;
import com.nexusgesti.tenant.domain.model.Referentiel;
import com.nexusgesti.tenant.domain.model.ReferentielItem;
import com.nexusgesti.tenant.domain.port.out.ReferentielRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReferentielPersistenceAdapter implements ReferentielRepository {

    private final ReferentielJpaRepository referentielJpaRepository;
    private final ReferentielItemJpaRepository itemJpaRepository;
    private final ObjectMapper objectMapper;

    public ReferentielPersistenceAdapter(ReferentielJpaRepository referentielJpaRepository,
                                         ReferentielItemJpaRepository itemJpaRepository,
                                         ObjectMapper objectMapper) {
        this.referentielJpaRepository = referentielJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Referentiel save(Referentiel referentiel) {
        UUID tenantId = TenantContext.require().asUuid();
        ReferentielEntity entity = toEntity(referentiel, tenantId);
        referentielJpaRepository.save(entity);
        return findById(referentiel.getIdReferentiel()).orElseThrow();
    }

    @Override
    public Optional<Referentiel> findById(UUID id) {
        return referentielJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Referentiel> findByEtablissement(UUID etablissementId) {
        return referentielJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<Referentiel> findByEtablissementAndCode(UUID etablissementId, String code) {
        return referentielJpaRepository.findByEtablissementIdAndCode(etablissementId, code).map(this::toDomain);
    }

    @Override
    public ReferentielItem saveItem(ReferentielItem item) {
        UUID tenantId = TenantContext.require().asUuid();
        ReferentielItemEntity entity = toItemEntity(item, tenantId);
        return itemToDomain(itemJpaRepository.save(entity));
    }

    @Override
    public Optional<ReferentielItem> findItem(UUID referentielId, UUID itemId) {
        return itemJpaRepository.findByIdAndReferentielId(itemId, referentielId).map(this::itemToDomain);
    }

    @Override
    public void deleteItem(UUID id, UUID referentielId) {
        itemJpaRepository.deleteByIdAndReferentielId(id, referentielId);
    }

    private ReferentielEntity toEntity(Referentiel r, UUID tenantId) {
        ReferentielEntity e = new ReferentielEntity();
        e.setId(r.getIdReferentiel());
        e.setEtablissementId(r.getEtablissementId());
        e.setCode(r.getCode());
        e.setLibelle(r.getLibelle());
        e.setNature(r.getNature() == null ? null : r.getNature().name());
        e.setOrdre(r.getOrdre());
        e.setActif(r.isActif());
        e.setTenantId(tenantId);
        return e;
    }

    private Referentiel toDomain(ReferentielEntity e) {
        Referentiel r = new Referentiel();
        r.setIdReferentiel(e.getId());
        r.setEtablissementId(e.getEtablissementId());
        r.setCode(e.getCode());
        r.setLibelle(e.getLibelle());
        r.setNature(e.getNature() == null ? null : NatureReferentiel.valueOf(e.getNature()));
        r.setOrdre(e.getOrdre());
        r.setActif(e.isActif());
        r.setItems(itemJpaRepository.findByReferentielId(e.getId()).stream()
                .map(this::itemToDomain).toList());
        return r;
    }

    private ReferentielItemEntity toItemEntity(ReferentielItem item, UUID tenantId) {
        ReferentielItemEntity e = new ReferentielItemEntity();
        e.setId(item.getIdItem() == null ? UUID.randomUUID() : item.getIdItem());
        e.setReferentielId(item.getReferentielId());
        e.setCode(item.getCode());
        e.setLibelle(item.getLibelle());
        e.setValeur(item.getValeur());
        e.setMetaDonnees(json(item.getMetaDonnees()));
        e.setOrdre(item.getOrdre());
        e.setActif(item.isActif());
        e.setComptabilisePresence(item.getComptabilisePresence());
        e.setBloqueSaisie(item.getBloqueSaisie());
        e.setTenantId(tenantId);
        return e;
    }

    private ReferentielItem itemToDomain(ReferentielItemEntity e) {
        ReferentielItem item = new ReferentielItem();
        item.setIdItem(e.getId());
        item.setReferentielId(e.getReferentielId());
        item.setCode(e.getCode());
        item.setLibelle(e.getLibelle());
        item.setValeur(e.getValeur());
        item.setMetaDonnees(parseMap(e.getMetaDonnees()));
        item.setOrdre(e.getOrdre());
        item.setActif(e.isActif());
        item.setComptabilisePresence(e.getComptabilisePresence());
        item.setBloqueSaisie(e.getBloqueSaisie());
        return item;
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? new HashMap<>() : value);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private Map<String, String> parseMap(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}