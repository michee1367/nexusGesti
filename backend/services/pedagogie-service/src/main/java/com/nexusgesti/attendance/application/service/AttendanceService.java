package com.nexusgesti.attendance.application.service;

import com.nexusgesti.attendance.domain.AttendanceException;
import com.nexusgesti.attendance.domain.model.*;
import com.nexusgesti.attendance.domain.port.in.AttendanceUseCase;
import com.nexusgesti.attendance.domain.port.out.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService implements AttendanceUseCase {

    private final AttendanceRepository repository;

    public AttendanceService(AttendanceRepository repository) {
        this.repository = repository;
    }

    // ---------- SEANCE ----------

    @Override
    public List<Seance> listSeances(LocalDate date) {
        return date == null ? repository.listSeances() : repository.listSeancesDate(date);
    }

    @Override
    public Seance getSeance(UUID id) {
        return repository.findSeance(id).orElseThrow(() -> AttendanceException.notFound("Seance", id));
    }

    @Override
    @Transactional
    public Seance creerSeance(Seance seance) {
        if (seance.getStatut() == null) {
            seance.setStatut(StatutSeance.PLANIFIEE);
        }
        return repository.saveSeance(seance);
    }

    @Override
    @Transactional
    public Seance updateSeance(UUID id, Seance seance) {
        getSeance(id);
        seance.setIdSeance(id);
        return repository.saveSeance(seance);
    }

    @Override
    @Transactional
    public Seance demarrerSeance(UUID id) {
        Seance seance = getSeance(id);
        seance.demarrer();
        return repository.saveSeance(seance);
    }

    @Override
    @Transactional
    public Seance terminerSeance(UUID id) {
        Seance seance = getSeance(id);
        seance.cloturer();
        return repository.saveSeance(seance);
    }

    @Override
    @Transactional
    public Seance reporterSeance(UUID id) {
        Seance seance = getSeance(id);
        seance.reporter();
        return repository.saveSeance(seance);
    }

    @Override
    @Transactional
    public Seance annulerSeance(UUID id, String motif) {
        Seance seance = getSeance(id);
        seance.annuler(motif);
        return repository.saveSeance(seance);
    }

    // ---------- FEUILLE DE PRESENCE ----------

    @Override
    public List<FeuillePresence> listFeuilles() {
        return repository.listFeuilles();
    }

    @Override
    public FeuillePresence getFeuille(UUID id) {
        return repository.findFeuille(id).orElseThrow(() -> AttendanceException.notFound("FeuillePresence", id));
    }

    @Override
    @Transactional
    public FeuillePresence creerFeuille(FeuillePresence feuille) {
        if (feuille.getStatut() == null) {
            feuille.ouvrir();
        }
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence pointer(UUID feuilleId, UUID eleveId, StatutPresence statut, UUID par) {
        FeuillePresence feuille = getFeuille(feuilleId);
        feuille.pointer(eleveId, statut, par);
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence marquerAbsence(UUID feuilleId, UUID eleveId, TypeAbsence type, UUID par) {
        FeuillePresence feuille = getFeuille(feuilleId);
        feuille.marquerAbsence(eleveId, type, par);
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence marquerRetard(UUID feuilleId, UUID eleveId, int minutes, UUID par) {
        FeuillePresence feuille = getFeuille(feuilleId);
        feuille.marquerRetard(eleveId, minutes, par);
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence faireAppelGlobal(UUID feuilleId, StatutPresence statut, UUID par) {
        FeuillePresence feuille = getFeuille(feuilleId);
        feuille.faireAppelGlobal(statut, par);
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence soumettreFeuille(UUID id) {
        FeuillePresence feuille = getFeuille(id);
        feuille.soumettre();
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence validerFeuille(UUID id, UUID par) {
        FeuillePresence feuille = getFeuille(id);
        feuille.valider(par);
        return repository.saveFeuille(feuille);
    }

    @Override
    @Transactional
    public FeuillePresence cloturerFeuille(UUID id) {
        FeuillePresence feuille = getFeuille(id);
        feuille.cloturer();
        return repository.saveFeuille(feuille);
    }

    // ---------- JUSTIFICATIF ----------

    @Override
    public List<Justificatif> listJustificatifs() {
        return repository.listJustificatifs();
    }

    @Override
    public Justificatif getJustificatif(UUID id) {
        return repository.findJustificatif(id).orElseThrow(() -> AttendanceException.notFound("Justificatif", id));
    }

    @Override
    @Transactional
    public Justificatif deposerJustificatif(Justificatif justificatif) {
        justificatif.deposer();
        return repository.saveJustificatif(justificatif);
    }

    @Override
    @Transactional
    public Justificatif accepterJustificatif(UUID id, UUID par) {
        Justificatif justificatif = getJustificatif(id);
        justificatif.accepter(par);
        repository.saveJustificatif(justificatif);
        requalifierAbsences(justificatif);
        return justificatif;
    }

    @Override
    @Transactional
    public Justificatif rejeterJustificatif(UUID id, String motif, UUID par) {
        Justificatif justificatif = getJustificatif(id);
        justificatif.rejeter(motif, par);
        return repository.saveJustificatif(justificatif);
    }

    private void requalifierAbsences(Justificatif justificatif) {
        if (justificatif.getDateReference() == null) {
            return;
        }
        repository.listFeuillesDate(justificatif.getDateReference()).forEach(feuille -> {
            boolean modifiee = false;
            for (PresenceEleve presence : feuille.getPresences()) {
                if (justificatif.getEleveId().equals(presence.getEleveId()) && presence.estAbsent()) {
                    presence.excuser(justificatif.getIdJustificatif());
                    modifiee = true;
                }
            }
            if (modifiee) {
                repository.saveFeuille(feuille);
            }
        });
    }

    // ---------- SUIVI ----------

    @Override
    public SuiviAssiduite getSuivi(UUID eleveId, UUID anneeId, UUID periodeId) {
        List<PresenceSuivi> lignes = repository.listPresencesValideesEleve(eleveId).stream()
                .filter(l -> anneeId == null || anneeId.equals(l.anneeId()))
                .filter(l -> periodeId == null || periodeId.equals(l.periodeId()))
                .toList();

        SuiviAssiduite suivi = new SuiviAssiduite();
        suivi.setEleveId(eleveId);
        suivi.setAnneeId(anneeId);
        suivi.setPeriodeId(periodeId);
        suivi.setNbSeancesPrevues(lignes.size());
        suivi.setNbSeancesTenues(lignes.size());

        BigDecimal heuresPrevues = BigDecimal.ZERO;
        BigDecimal heuresAbsentes = BigDecimal.ZERO;
        int justifiees = 0;
        int injustifiees = 0;
        int retards = 0;
        int minutesRetard = 0;

        for (PresenceSuivi ligne : lignes) {
            BigDecimal volume = ligne.volumeHoraire() == null ? BigDecimal.ZERO : ligne.volumeHoraire();
            heuresPrevues = heuresPrevues.add(volume);
            StatutPresence statut = ligne.statut();
            if (statut == null) {
                continue;
            }
            if (statut.estAbsent()) {
                heuresAbsentes = heuresAbsentes.add(volume);
                if (statut.estJustifiee()) {
                    justifiees++;
                } else {
                    injustifiees++;
                }
            }
            if (statut.estRetard()) {
                retards++;
                minutesRetard += ligne.minutesRetard() == null ? 0 : ligne.minutesRetard();
            }
        }

        suivi.setNbHeuresPrevues(heuresPrevues);
        suivi.setNbHeuresAbsentes(heuresAbsentes);
        suivi.setNbAbsencesJustifiees(justifiees);
        suivi.setNbAbsencesInjustifiees(injustifiees);
        suivi.setNbRetards(retards);
        suivi.setMinutesRetardTotal(minutesRetard);
        return suivi.recalculer();
    }

    @Override
    public BigDecimal getTauxClasse(UUID classeId) {
        List<FeuillePresence> feuilles = repository.listFeuillesClasse(classeId).stream()
                .filter(f -> f.getStatut() == StatutFeuille.VALIDEE || f.getStatut() == StatutFeuille.CLOTUREE)
                .toList();
        if (feuilles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal somme = feuilles.stream()
                .map(FeuillePresence::getTauxPresence)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return somme.divide(BigDecimal.valueOf(feuilles.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public List<FeuillePresence> getRegistre(UUID classeId, LocalDate date) {
        if (classeId != null) {
            return repository.listFeuillesClasse(classeId);
        }
        if (date != null) {
            return repository.listFeuillesDate(date);
        }
        return repository.listFeuilles();
    }
}
