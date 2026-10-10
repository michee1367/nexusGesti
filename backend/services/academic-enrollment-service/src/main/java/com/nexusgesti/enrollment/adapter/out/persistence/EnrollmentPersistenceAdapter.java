package com.nexusgesti.enrollment.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.enrollment.adapter.out.persistence.entity.GroupeEntity;
import com.nexusgesti.enrollment.adapter.out.persistence.entity.InscriptionUEEntity;
import com.nexusgesti.enrollment.adapter.out.persistence.entity.MembreGroupeEntity;
import com.nexusgesti.enrollment.adapter.out.persistence.entity.RegleInscriptionEntity;
import com.nexusgesti.enrollment.domain.model.*;
import com.nexusgesti.enrollment.domain.port.out.EnrollmentRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EnrollmentPersistenceAdapter implements EnrollmentRepository {

    private final InscriptionUEJpaRepository inscriptionJpaRepository;
    private final GroupeJpaRepository groupeJpaRepository;
    private final MembreGroupeJpaRepository membreJpaRepository;
    private final RegleInscriptionJpaRepository regleJpaRepository;

    public EnrollmentPersistenceAdapter(InscriptionUEJpaRepository inscriptionJpaRepository,
                                        GroupeJpaRepository groupeJpaRepository,
                                        MembreGroupeJpaRepository membreJpaRepository,
                                        RegleInscriptionJpaRepository regleJpaRepository) {
        this.inscriptionJpaRepository = inscriptionJpaRepository;
        this.groupeJpaRepository = groupeJpaRepository;
        this.membreJpaRepository = membreJpaRepository;
        this.regleJpaRepository = regleJpaRepository;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- INSCRIPTION UE ----------

    @Override
    public InscriptionUE saveInscription(InscriptionUE inscription) {
        if (inscription.getIdInscription() == null) {
            inscription.setIdInscription(UUID.randomUUID());
        }
        InscriptionUEEntity e = new InscriptionUEEntity();
        e.setId(inscription.getIdInscription());
        e.setEleveId(inscription.getEleveId());
        e.setProgrammeId(inscription.getProgrammeId());
        e.setPromotionId(inscription.getPromotionId());
        e.setUeId(inscription.getUeId());
        e.setAnneeId(inscription.getAnneeId());
        e.setSemestreId(inscription.getSemestreId());
        e.setStatut(inscription.getStatut() == null ? null : inscription.getStatut().name());
        e.setDateInscription(inscription.getDateInscription());
        e.setCreditsReconnus(inscription.getCreditsReconnus());
        e.setMoyenneAnneePrecedente(inscription.getMoyenneAnneePrecedente());
        e.setMoyenneUEPrecedente(inscription.getMoyenneUEPrecedente());
        e.setOption(inscription.getOption());
        e.setMotifRefus(inscription.getMotifRefus());
        e.setRangListeAttente(inscription.getRangListeAttente());
        e.setValideLe(inscription.getValideLe());
        e.setValidePar(inscription.getValidePar());
        e.setTenantId(tenant());
        inscriptionJpaRepository.save(e);
        return inscription;
    }

    @Override
    public Optional<InscriptionUE> findInscription(UUID id) {
        return inscriptionJpaRepository.findById(id).map(this::inscriptionToDomain);
    }

    @Override
    public List<InscriptionUE> listInscriptions() {
        return inscriptionJpaRepository.findAll().stream().map(this::inscriptionToDomain).toList();
    }

    @Override
    public List<InscriptionUE> listInscriptionsEleve(UUID eleveId) {
        return inscriptionJpaRepository.findByEleveId(eleveId).stream().map(this::inscriptionToDomain).toList();
    }

    // ---------- GROUPE ----------

    @Override
    @Transactional
    public Groupe saveGroupe(Groupe groupe) {
        if (groupe.getIdGroupe() == null) {
            groupe.setIdGroupe(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        membreJpaRepository.deleteByGroupeId(groupe.getIdGroupe());

        GroupeEntity e = new GroupeEntity();
        e.setId(groupe.getIdGroupe());
        e.setCode(groupe.getCode());
        e.setLibelle(groupe.getLibelle());
        e.setType(groupe.getType() == null ? null : groupe.getType().name());
        e.setCoursId(groupe.getCoursId());
        e.setUeId(groupe.getUeId());
        e.setClasseId(groupe.getClasseId());
        e.setEnseignantId(groupe.getEnseignantId());
        e.setSalleId(groupe.getSalleId());
        e.setCreneauId(groupe.getCreneauId());
        e.setJour(groupe.getJour());
        e.setCapacite(groupe.getCapacite());
        e.setEffectif(groupe.getEffectif());
        e.setStatut(groupe.getStatut() == null ? null : groupe.getStatut().name());
        e.setTenantId(tenantId);
        groupeJpaRepository.save(e);

        groupe.getMembres().forEach(m -> {
            if (m.getIdMembre() == null) {
                m.setIdMembre(UUID.randomUUID());
            }
            MembreGroupeEntity me = new MembreGroupeEntity();
            me.setId(m.getIdMembre());
            me.setGroupeId(groupe.getIdGroupe());
            me.setEleveId(m.getEleveId());
            me.setDateAffectation(m.getDateAffectation());
            me.setAffectePar(m.getAffectePar());
            me.setRang(m.getRang());
            me.setTenantId(tenantId);
            membreJpaRepository.save(me);
        });

        return findGroupe(groupe.getIdGroupe()).orElseThrow();
    }

    @Override
    public Optional<Groupe> findGroupe(UUID id) {
        return groupeJpaRepository.findById(id).map(this::groupeToDomain);
    }

    @Override
    public List<Groupe> listGroupes() {
        return groupeJpaRepository.findAll().stream().map(this::groupeToDomain).toList();
    }

    // ---------- REGLE INSCRIPTION ----------

    @Override
    public RegleInscription saveRegle(RegleInscription regle) {
        if (regle.getIdRegle() == null) {
            regle.setIdRegle(UUID.randomUUID());
        }
        RegleInscriptionEntity e = new RegleInscriptionEntity();
        e.setId(regle.getIdRegle());
        e.setLibelle(regle.getLibelle());
        e.setProgrammeId(regle.getProgrammeId());
        e.setCondition(regle.getCondition());
        e.setCreditsMin(regle.getCreditsMin());
        e.setMoyenneMin(regle.getMoyenneMin());
        e.setCreneauAge(regle.getCreneauAge());
        e.setPriorite(regle.getPriorite());
        e.setTenantId(tenant());
        regleJpaRepository.save(e);
        return regle;
    }

    @Override
    public Optional<RegleInscription> findRegle(UUID id) {
        return regleJpaRepository.findById(id).map(this::regleToDomain);
    }

    @Override
    public List<RegleInscription> listRegles() {
        return regleJpaRepository.findAll().stream().map(this::regleToDomain).toList();
    }

    @Override
    public void deleteRegle(UUID id) {
        regleJpaRepository.deleteById(id);
    }

    // ---------- MAPPERS ----------

    private InscriptionUE inscriptionToDomain(InscriptionUEEntity e) {
        InscriptionUE i = new InscriptionUE();
        i.setIdInscription(e.getId());
        i.setEleveId(e.getEleveId());
        i.setProgrammeId(e.getProgrammeId());
        i.setPromotionId(e.getPromotionId());
        i.setUeId(e.getUeId());
        i.setAnneeId(e.getAnneeId());
        i.setSemestreId(e.getSemestreId());
        i.setStatut(e.getStatut() == null ? null : StatutInscriptionUE.valueOf(e.getStatut()));
        i.setDateInscription(e.getDateInscription());
        i.setCreditsReconnus(e.getCreditsReconnus());
        i.setMoyenneAnneePrecedente(e.getMoyenneAnneePrecedente());
        i.setMoyenneUEPrecedente(e.getMoyenneUEPrecedente());
        i.setOption(e.getOption());
        i.setMotifRefus(e.getMotifRefus());
        i.setRangListeAttente(e.getRangListeAttente());
        i.setValideLe(e.getValideLe());
        i.setValidePar(e.getValidePar());
        return i;
    }

    private Groupe groupeToDomain(GroupeEntity e) {
        Groupe g = new Groupe();
        g.setIdGroupe(e.getId());
        g.setCode(e.getCode());
        g.setLibelle(e.getLibelle());
        g.setType(e.getType() == null ? null : TypeGroupe.valueOf(e.getType()));
        g.setCoursId(e.getCoursId());
        g.setUeId(e.getUeId());
        g.setClasseId(e.getClasseId());
        g.setEnseignantId(e.getEnseignantId());
        g.setSalleId(e.getSalleId());
        g.setCreneauId(e.getCreneauId());
        g.setJour(e.getJour());
        g.setCapacite(e.getCapacite());
        g.setEffectif(e.getEffectif());
        g.setStatut(e.getStatut() == null ? null : StatutGroupe.valueOf(e.getStatut()));
        g.setMembres(membreJpaRepository.findByGroupeId(e.getId()).stream().map(this::membreToDomain).toList());
        return g;
    }

    private MembreGroupe membreToDomain(MembreGroupeEntity e) {
        MembreGroupe m = new MembreGroupe();
        m.setIdMembre(e.getId());
        m.setGroupeId(e.getGroupeId());
        m.setEleveId(e.getEleveId());
        m.setDateAffectation(e.getDateAffectation());
        m.setAffectePar(e.getAffectePar());
        m.setRang(e.getRang());
        return m;
    }

    private RegleInscription regleToDomain(RegleInscriptionEntity e) {
        RegleInscription r = new RegleInscription();
        r.setIdRegle(e.getId());
        r.setLibelle(e.getLibelle());
        r.setProgrammeId(e.getProgrammeId());
        r.setCondition(e.getCondition());
        r.setCreditsMin(e.getCreditsMin());
        r.setMoyenneMin(e.getMoyenneMin());
        r.setCreneauAge(e.getCreneauAge());
        r.setPriorite(e.getPriorite());
        return r;
    }
}
