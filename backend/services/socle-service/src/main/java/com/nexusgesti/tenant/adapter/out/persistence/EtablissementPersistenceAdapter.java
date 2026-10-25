package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.adapter.out.persistence.entity.EtablissementEntity;
import com.nexusgesti.tenant.domain.model.Adresse;
import com.nexusgesti.tenant.domain.model.Etablissement;
import com.nexusgesti.tenant.domain.model.ProfilEtablissement;
import com.nexusgesti.tenant.domain.model.StatutEtablissement;
import com.nexusgesti.tenant.domain.port.out.EtablissementRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EtablissementPersistenceAdapter implements EtablissementRepository {

    private final EtablissementJpaRepository jpaRepository;

    public EtablissementPersistenceAdapter(EtablissementJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Etablissement save(Etablissement etablissement) {
        EtablissementEntity entity = toEntity(etablissement);
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Etablissement> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    private EtablissementEntity toEntity(Etablissement e) {
        EtablissementEntity entity = new EtablissementEntity();
        entity.setId(e.getId());
        entity.setCode(e.getCode());
        entity.setDenomination(e.getDenomination());
        entity.setSigle(e.getSigle());
        entity.setProfil(e.getProfil() == null ? null : e.getProfil().name());
        entity.setStatut(e.getStatut() == null ? null : e.getStatut().name());
        Adresse a = e.getAdresse();
        if (a != null) {
            entity.setAdrRue(a.getRue());
            entity.setAdrAvenue(a.getAvenue());
            entity.setAdrQuartier(a.getQuartier());
            entity.setAdrCommune(a.getCommune());
            entity.setAdrVille(a.getVille());
            entity.setAdrProvince(a.getProvince());
            entity.setAdrTerritoire(a.getTerritoire());
            entity.setAdrCodePostal(a.getCodePostal());
            entity.setAdrPays(a.getPays());
        }
        entity.setTelephone(e.getTelephone());
        entity.setEmailOfficiel(e.getEmailOfficiel());
        entity.setDevise(e.getDevise());
        entity.setPays(e.getPays());
        entity.setFuseauHoraire(e.getFuseauHoraire());
        entity.setCapaciteAccueil(e.getCapaciteAccueil());
        return entity;
    }

    private Etablissement toDomain(EtablissementEntity entity) {
        Etablissement e = new Etablissement();
        e.setId(entity.getId());
        e.setCode(entity.getCode());
        e.setDenomination(entity.getDenomination());
        e.setSigle(entity.getSigle());
        e.setProfil(entity.getProfil() == null ? null : ProfilEtablissement.valueOf(entity.getProfil()));
        e.setStatut(entity.getStatut() == null ? null : StatutEtablissement.valueOf(entity.getStatut()));
        Adresse a = new Adresse();
        a.setRue(entity.getAdrRue());
        a.setAvenue(entity.getAdrAvenue());
        a.setQuartier(entity.getAdrQuartier());
        a.setCommune(entity.getAdrCommune());
        a.setVille(entity.getAdrVille());
        a.setProvince(entity.getAdrProvince());
        a.setTerritoire(entity.getAdrTerritoire());
        a.setCodePostal(entity.getAdrCodePostal());
        a.setPays(entity.getAdrPays());
        e.setAdresse(a);
        e.setTelephone(entity.getTelephone());
        e.setEmailOfficiel(entity.getEmailOfficiel());
        e.setDevise(entity.getDevise());
        e.setPays(entity.getPays());
        e.setFuseauHoraire(entity.getFuseauHoraire());
        e.setCapaciteAccueil(entity.getCapaciteAccueil());
        return e;
    }
}