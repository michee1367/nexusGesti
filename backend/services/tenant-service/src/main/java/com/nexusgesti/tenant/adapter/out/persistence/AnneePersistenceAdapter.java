package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.adapter.out.persistence.entity.AnneeScolaireEntity;
import com.nexusgesti.tenant.adapter.out.persistence.entity.PeriodeEntity;
import com.nexusgesti.tenant.domain.model.AnneeScolaire;
import com.nexusgesti.tenant.domain.model.Periode;
import com.nexusgesti.tenant.domain.model.StatutAnnee;
import com.nexusgesti.tenant.domain.model.TypeAnnee;
import com.nexusgesti.tenant.domain.model.TypePeriode;
import com.nexusgesti.tenant.domain.port.out.AnneeRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AnneePersistenceAdapter implements AnneeRepository {

    private final AnneeScolaireJpaRepository anneeJpaRepository;
    private final PeriodeJpaRepository periodeJpaRepository;

    public AnneePersistenceAdapter(AnneeScolaireJpaRepository anneeJpaRepository,
                                   PeriodeJpaRepository periodeJpaRepository) {
        this.anneeJpaRepository = anneeJpaRepository;
        this.periodeJpaRepository = periodeJpaRepository;
    }

    @Override
    public AnneeScolaire save(AnneeScolaire annee) {
        AnneeScolaireEntity entity = toEntity(annee);
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(anneeJpaRepository.save(entity));
    }

    @Override
    public Optional<AnneeScolaire> findById(UUID id) {
        return anneeJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AnneeScolaire> findByEtablissement(UUID etablissementId) {
        return anneeJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Periode savePeriode(Periode periode) {
        PeriodeEntity entity = toEntity(periode);
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(periodeJpaRepository.save(entity));
    }

    @Override
    public Optional<Periode> findPeriode(UUID anneeId, UUID periodeId) {
        return periodeJpaRepository.findByIdAndAnneeId(periodeId, anneeId).map(this::toDomain);
    }

    @Override
    public List<Periode> findPeriodes(UUID anneeId) {
        return periodeJpaRepository.findByAnneeId(anneeId).stream()
                .map(this::toDomain).toList();
    }

    private AnneeScolaireEntity toEntity(AnneeScolaire a) {
        AnneeScolaireEntity e = new AnneeScolaireEntity();
        e.setId(a.getIdAnnee());
        e.setEtablissementId(a.getEtablissementId());
        e.setCode(a.getCode());
        e.setLibelle(a.getLibelle());
        e.setType(a.getType() == null ? null : a.getType().name());
        e.setDateDebut(a.getDateDebut() == null ? null : a.getDateDebut().toInstant());
        e.setDateFin(a.getDateFin() == null ? null : a.getDateFin().toInstant());
        e.setStatut(a.getStatut() == null ? null : a.getStatut().name());
        e.setAnneeReference(a.getAnneeReference());
        e.setSiteParDefautId(a.getSiteParDefautId());
        return e;
    }

    private AnneeScolaire toDomain(AnneeScolaireEntity e) {
        AnneeScolaire a = new AnneeScolaire();
        a.setIdAnnee(e.getId());
        a.setEtablissementId(e.getEtablissementId());
        a.setCode(e.getCode());
        a.setLibelle(e.getLibelle());
        a.setType(e.getType() == null ? null : TypeAnnee.valueOf(e.getType()));
        a.setDateDebut(e.getDateDebut() == null ? null : Date.from(e.getDateDebut()));
        a.setDateFin(e.getDateFin() == null ? null : Date.from(e.getDateFin()));
        a.setStatut(e.getStatut() == null ? null : StatutAnnee.valueOf(e.getStatut()));
        a.setAnneeReference(e.getAnneeReference());
        a.setSiteParDefautId(e.getSiteParDefautId());
        return a;
    }

    private PeriodeEntity toEntity(Periode p) {
        PeriodeEntity e = new PeriodeEntity();
        e.setId(p.getIdPeriode());
        e.setAnneeId(p.getAnneeId());
        e.setCode(p.getCode());
        e.setLibelle(p.getLibelle());
        e.setRang(p.getRang());
        e.setType(p.getType() == null ? null : p.getType().name());
        e.setDateDebut(p.getDateDebut() == null ? null : p.getDateDebut().toInstant());
        e.setDateFin(p.getDateFin() == null ? null : p.getDateFin().toInstant());
        e.setCloturee(p.isCloturee());
        e.setDateCloture(p.getDateCloture() == null ? null : p.getDateCloture().toInstant());
        return e;
    }

    private Periode toDomain(PeriodeEntity e) {
        Periode p = new Periode();
        p.setIdPeriode(e.getId());
        p.setAnneeId(e.getAnneeId());
        p.setCode(e.getCode());
        p.setLibelle(e.getLibelle());
        p.setRang(e.getRang());
        p.setType(e.getType() == null ? null : TypePeriode.valueOf(e.getType()));
        p.setDateDebut(e.getDateDebut() == null ? null : Date.from(e.getDateDebut()));
        p.setDateFin(e.getDateFin() == null ? null : Date.from(e.getDateFin()));
        p.setCloturee(e.isCloturee());
        p.setDateCloture(e.getDateCloture() == null ? null : Date.from(e.getDateCloture()));
        return p;
    }
}