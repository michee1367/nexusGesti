package com.nexusgesti.attendance.adapter.out.persistence;

import com.nexusgesti.attendance.adapter.out.persistence.entity.FeuillePresenceEntity;
import com.nexusgesti.attendance.adapter.out.persistence.entity.JustificatifEntity;
import com.nexusgesti.attendance.adapter.out.persistence.entity.PresenceEleveEntity;
import com.nexusgesti.attendance.adapter.out.persistence.entity.SeanceEntity;
import com.nexusgesti.attendance.domain.model.*;
import com.nexusgesti.attendance.domain.port.out.AttendanceRepository;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AttendancePersistenceAdapter implements AttendanceRepository {

    private final SeanceJpaRepository seanceJpaRepository;
    private final FeuillePresenceJpaRepository feuilleJpaRepository;
    private final PresenceEleveJpaRepository presenceJpaRepository;
    private final JustificatifJpaRepository justificatifJpaRepository;

    public AttendancePersistenceAdapter(SeanceJpaRepository seanceJpaRepository,
                                        FeuillePresenceJpaRepository feuilleJpaRepository,
                                        PresenceEleveJpaRepository presenceJpaRepository,
                                        JustificatifJpaRepository justificatifJpaRepository) {
        this.seanceJpaRepository = seanceJpaRepository;
        this.feuilleJpaRepository = feuilleJpaRepository;
        this.presenceJpaRepository = presenceJpaRepository;
        this.justificatifJpaRepository = justificatifJpaRepository;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- SEANCE ----------

    @Override
    public Seance saveSeance(Seance seance) {
        if (seance.getIdSeance() == null) {
            seance.setIdSeance(UUID.randomUUID());
        }
        SeanceEntity e = new SeanceEntity();
        e.setId(seance.getIdSeance());
        e.setClasseId(seance.getClasseId());
        e.setCoursId(seance.getCoursId());
        e.setEnseignantId(seance.getEnseignantId());
        e.setSalleId(seance.getSalleId());
        e.setAnneeId(seance.getAnneeId());
        e.setPeriodeId(seance.getPeriodeId());
        e.setDate(seance.getDate());
        e.setHeureDebut(seance.getHeureDebut());
        e.setHeureFin(seance.getHeureFin());
        e.setVolumeHoraireReel(seance.getVolumeHoraireReel());
        e.setType(seance.getType() == null ? null : seance.getType().name());
        e.setStatut(seance.getStatut() == null ? null : seance.getStatut().name());
        e.setAnnulee(seance.isAnnulee());
        e.setMotifAnnulation(seance.getMotifAnnulation());
        e.setTenantId(tenant());
        seanceJpaRepository.save(e);
        return seance;
    }

    @Override
    public Optional<Seance> findSeance(UUID id) {
        return seanceJpaRepository.findById(id).map(this::seanceToDomain);
    }

    @Override
    public List<Seance> listSeances() {
        return seanceJpaRepository.findAll().stream().map(this::seanceToDomain).toList();
    }

    @Override
    public List<Seance> listSeancesDate(LocalDate date) {
        return seanceJpaRepository.findByDate(date).stream().map(this::seanceToDomain).toList();
    }

    // ---------- FEUILLE DE PRESENCE ----------

    @Override
    @Transactional
    public FeuillePresence saveFeuille(FeuillePresence feuille) {
        if (feuille.getIdFeuille() == null) {
            feuille.setIdFeuille(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        presenceJpaRepository.deleteByFeuilleId(feuille.getIdFeuille());

        FeuillePresenceEntity e = new FeuillePresenceEntity();
        e.setId(feuille.getIdFeuille());
        e.setSeanceId(feuille.getSeanceId());
        e.setClasseId(feuille.getClasseId());
        e.setCoursId(feuille.getCoursId());
        e.setEnseignantId(feuille.getEnseignantId());
        e.setDate(feuille.getDate());
        e.setStatut(feuille.getStatut() == null ? null : feuille.getStatut().name());
        e.setMethode(feuille.getMethode() == null ? null : feuille.getMethode().name());
        e.setOuvertePar(feuille.getOuvertePar());
        e.setOuverteLe(feuille.getOuverteLe());
        e.setSoumiseLe(feuille.getSoumiseLe());
        e.setValideePar(feuille.getValideePar());
        e.setValideeLe(feuille.getValideeLe());
        e.setEffectifTheorique(feuille.getEffectifTheorique());
        e.setEffectifPresent(feuille.getEffectifPresent());
        e.setObservations(feuille.getObservations());
        e.setTenantId(tenantId);
        feuilleJpaRepository.save(e);

        feuille.getPresences().forEach(p -> {
            if (p.getIdPresence() == null) {
                p.setIdPresence(UUID.randomUUID());
            }
            PresenceEleveEntity pe = new PresenceEleveEntity();
            pe.setId(p.getIdPresence());
            pe.setFeuilleId(feuille.getIdFeuille());
            pe.setEleveId(p.getEleveId());
            pe.setStatut(p.getStatut() == null ? null : p.getStatut().name());
            pe.setHeureArrivee(p.getHeureArrivee());
            pe.setMinutesRetard(p.getMinutesRetard());
            pe.setHeureDepart(p.getHeureDepart());
            pe.setDepartAnticipe(p.isDepartAnticipe());
            pe.setTypeAbsence(p.getTypeAbsence() == null ? null : p.getTypeAbsence().name());
            pe.setJustificatifId(p.getJustificatifId());
            pe.setSaisiePar(p.getSaisiePar());
            pe.setSaisieLe(p.getSaisieLe());
            pe.setJustifiee(p.isJustifiee());
            pe.setJustifieeLe(p.getJustifieeLe());
            pe.setModifieeApresValidation(p.isModifieeApresValidation());
            pe.setTenantId(tenantId);
            presenceJpaRepository.save(pe);
        });

        return findFeuille(feuille.getIdFeuille()).orElseThrow();
    }

    @Override
    public Optional<FeuillePresence> findFeuille(UUID id) {
        return feuilleJpaRepository.findById(id).map(this::feuilleToDomain);
    }

    @Override
    public List<FeuillePresence> listFeuilles() {
        return feuilleJpaRepository.findAll().stream().map(this::feuilleToDomain).toList();
    }

    @Override
    public List<FeuillePresence> listFeuillesClasse(UUID classeId) {
        return feuilleJpaRepository.findByClasseId(classeId).stream().map(this::feuilleToDomain).toList();
    }

    @Override
    public List<FeuillePresence> listFeuillesDate(LocalDate date) {
        return feuilleJpaRepository.findByDate(date).stream().map(this::feuilleToDomain).toList();
    }

    // ---------- JUSTIFICATIF ----------

    @Override
    public Justificatif saveJustificatif(Justificatif justificatif) {
        if (justificatif.getIdJustificatif() == null) {
            justificatif.setIdJustificatif(UUID.randomUUID());
        }
        JustificatifEntity e = new JustificatifEntity();
        e.setId(justificatif.getIdJustificatif());
        e.setEleveId(justificatif.getEleveId());
        e.setType(justificatif.getType() == null ? null : justificatif.getType().name());
        e.setDateReference(justificatif.getDateReference());
        e.setDateDepot(justificatif.getDateDepot());
        e.setMotif(justificatif.getMotif());
        e.setFichierUri(justificatif.getFichierUri());
        e.setStatut(justificatif.getStatut() == null ? null : justificatif.getStatut().name());
        e.setTraitePar(justificatif.getTraitePar());
        e.setTraiteLe(justificatif.getTraiteLe());
        e.setMotifRejet(justificatif.getMotifRejet());
        e.setTenantId(tenant());
        justificatifJpaRepository.save(e);
        return justificatif;
    }

    @Override
    public Optional<Justificatif> findJustificatif(UUID id) {
        return justificatifJpaRepository.findById(id).map(this::justificatifToDomain);
    }

    @Override
    public List<Justificatif> listJustificatifs() {
        return justificatifJpaRepository.findAll().stream().map(this::justificatifToDomain).toList();
    }

    @Override
    public List<PresenceSuivi> listPresencesValideesEleve(UUID eleveId) {
        return presenceJpaRepository.findLignesSuivi(eleveId).stream()
                .map(p -> new PresenceSuivi(
                        p.getEleveId(),
                        p.getAnneeId(),
                        p.getPeriodeId(),
                        p.getStatut() == null ? null : StatutPresence.valueOf(p.getStatut()),
                        p.getTypeAbsence() == null ? null : TypeAbsence.valueOf(p.getTypeAbsence()),
                        p.getMinutesRetard(),
                        p.getVolumeHoraire()))
                .toList();
    }

    // ---------- MAPPERS ----------

    private Seance seanceToDomain(SeanceEntity e) {
        Seance s = new Seance();
        s.setIdSeance(e.getId());
        s.setClasseId(e.getClasseId());
        s.setCoursId(e.getCoursId());
        s.setEnseignantId(e.getEnseignantId());
        s.setSalleId(e.getSalleId());
        s.setAnneeId(e.getAnneeId());
        s.setPeriodeId(e.getPeriodeId());
        s.setDate(e.getDate());
        s.setHeureDebut(e.getHeureDebut());
        s.setHeureFin(e.getHeureFin());
        s.setVolumeHoraireReel(e.getVolumeHoraireReel());
        s.setType(e.getType() == null ? null : TypeSeance.valueOf(e.getType()));
        s.setStatut(e.getStatut() == null ? null : StatutSeance.valueOf(e.getStatut()));
        s.setAnnulee(e.isAnnulee());
        s.setMotifAnnulation(e.getMotifAnnulation());
        return s;
    }

    private FeuillePresence feuilleToDomain(FeuillePresenceEntity e) {
        FeuillePresence f = new FeuillePresence();
        f.setIdFeuille(e.getId());
        f.setSeanceId(e.getSeanceId());
        f.setClasseId(e.getClasseId());
        f.setCoursId(e.getCoursId());
        f.setEnseignantId(e.getEnseignantId());
        f.setDate(e.getDate());
        f.setStatut(e.getStatut() == null ? null : StatutFeuille.valueOf(e.getStatut()));
        f.setMethode(e.getMethode() == null ? null : MethodeSaisie.valueOf(e.getMethode()));
        f.setOuvertePar(e.getOuvertePar());
        f.setOuverteLe(e.getOuverteLe());
        f.setSoumiseLe(e.getSoumiseLe());
        f.setValideePar(e.getValideePar());
        f.setValideeLe(e.getValideeLe());
        f.setEffectifTheorique(e.getEffectifTheorique());
        f.setEffectifPresent(e.getEffectifPresent());
        f.setObservations(e.getObservations());
        f.setPresences(presenceJpaRepository.findByFeuilleId(e.getId()).stream()
                .map(this::presenceToDomain).toList());
        return f;
    }

    private PresenceEleve presenceToDomain(PresenceEleveEntity e) {
        PresenceEleve p = new PresenceEleve();
        p.setIdPresence(e.getId());
        p.setFeuilleId(e.getFeuilleId());
        p.setEleveId(e.getEleveId());
        p.setStatut(e.getStatut() == null ? null : StatutPresence.valueOf(e.getStatut()));
        p.setHeureArrivee(e.getHeureArrivee());
        p.setMinutesRetard(e.getMinutesRetard());
        p.setHeureDepart(e.getHeureDepart());
        p.setDepartAnticipe(e.isDepartAnticipe());
        p.setTypeAbsence(e.getTypeAbsence() == null ? null : TypeAbsence.valueOf(e.getTypeAbsence()));
        p.setJustificatifId(e.getJustificatifId());
        p.setSaisiePar(e.getSaisiePar());
        p.setSaisieLe(e.getSaisieLe());
        p.setJustifiee(e.isJustifiee());
        p.setJustifieeLe(e.getJustifieeLe());
        p.setModifieeApresValidation(e.isModifieeApresValidation());
        return p;
    }

    private Justificatif justificatifToDomain(JustificatifEntity e) {
        Justificatif j = new Justificatif();
        j.setIdJustificatif(e.getId());
        j.setEleveId(e.getEleveId());
        j.setType(e.getType() == null ? null : TypeAbsence.valueOf(e.getType()));
        j.setDateReference(e.getDateReference());
        j.setDateDepot(e.getDateDepot());
        j.setMotif(e.getMotif());
        j.setFichierUri(e.getFichierUri());
        j.setStatut(e.getStatut() == null ? null : StatutJustificatif.valueOf(e.getStatut()));
        j.setTraitePar(e.getTraitePar());
        j.setTraiteLe(e.getTraiteLe());
        j.setMotifRejet(e.getMotifRejet());
        return j;
    }
}
