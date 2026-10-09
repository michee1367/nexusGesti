package com.nexusgesti.structure.application.service;

import com.nexusgesti.structure.domain.StructureException;
import com.nexusgesti.structure.domain.model.*;
import com.nexusgesti.structure.domain.port.in.StructureUseCase;
import com.nexusgesti.structure.domain.port.out.StructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StructureService implements StructureUseCase {

    private final StructureRepository repository;

    public StructureService(StructureRepository repository) {
        this.repository = repository;
    }

    // ---------- NIVEAU ----------

    @Override
    public List<Niveau> listNiveaux() {
        return repository.listNiveaux();
    }

    @Override
    public Niveau getNiveau(UUID id) {
        return repository.findNiveau(id).orElseThrow(() -> StructureException.notFound("Niveau", id));
    }

    @Override
    @Transactional
    public Niveau creerNiveau(Niveau niveau) {
        return repository.saveNiveau(niveau);
    }

    @Override
    @Transactional
    public Niveau updateNiveau(UUID id, Niveau niveau) {
        getNiveau(id);
        niveau.setIdNiveau(id);
        return repository.saveNiveau(niveau);
    }

    @Override
    @Transactional
    public void supprimerNiveau(UUID id) {
        getNiveau(id);
        repository.deleteNiveau(id);
    }

    // ---------- CLASSE ----------

    @Override
    public List<Classe> listClasses() {
        return repository.listClasses();
    }

    @Override
    public Classe getClasse(UUID id) {
        return repository.findClasse(id).orElseThrow(() -> StructureException.notFound("Classe", id));
    }

    @Override
    @Transactional
    public Classe creerClasse(Classe classe) {
        if (classe.getStatut() == null) {
            classe.setStatut(StatutClasse.OUVERTE);
        }
        return repository.saveClasse(classe);
    }

    @Override
    @Transactional
    public Classe updateClasse(UUID id, Classe classe) {
        getClasse(id);
        classe.setIdClasse(id);
        return repository.saveClasse(classe);
    }

    @Override
    @Transactional
    public Classe ouvrirClasse(UUID id) {
        Classe classe = getClasse(id);
        classe.ouvrir();
        return repository.saveClasse(classe);
    }

    @Override
    @Transactional
    public Classe cloturerClasse(UUID id) {
        Classe classe = getClasse(id);
        classe.cloturer();
        return repository.saveClasse(classe);
    }

    // ---------- PROMOTION ----------

    @Override
    public List<Promotion> listPromotions() {
        return repository.listPromotions();
    }

    @Override
    public Promotion getPromotion(UUID id) {
        return repository.findPromotion(id).orElseThrow(() -> StructureException.notFound("Promotion", id));
    }

    @Override
    @Transactional
    public Promotion creerPromotion(Promotion promotion) {
        if (promotion.getStatut() == null) {
            promotion.setStatut(StatutClasse.OUVERTE);
        }
        return repository.savePromotion(promotion);
    }

    @Override
    @Transactional
    public Promotion updatePromotion(UUID id, Promotion promotion) {
        getPromotion(id);
        promotion.setIdPromotion(id);
        return repository.savePromotion(promotion);
    }

    // ---------- SEMESTRE ----------

    @Override
    public List<Semestre> listSemestres() {
        return repository.listSemestres();
    }

    @Override
    public Semestre getSemestre(UUID id) {
        return repository.findSemestre(id).orElseThrow(() -> StructureException.notFound("Semestre", id));
    }

    @Override
    @Transactional
    public Semestre creerSemestre(Semestre semestre) {
        return repository.saveSemestre(semestre);
    }

    @Override
    @Transactional
    public Semestre updateSemestre(UUID id, Semestre semestre) {
        getSemestre(id);
        semestre.setIdSemestre(id);
        return repository.saveSemestre(semestre);
    }

    // ---------- UE ----------

    @Override
    public List<UE> listUEs() {
        return repository.listUEs();
    }

    @Override
    public UE getUE(UUID id) {
        return repository.findUE(id).orElseThrow(() -> StructureException.notFound("UE", id));
    }

    @Override
    @Transactional
    public UE creerUE(UE ue) {
        return repository.saveUE(ue);
    }

    @Override
    @Transactional
    public UE updateUE(UUID id, UE ue) {
        getUE(id);
        ue.setIdUE(id);
        return repository.saveUE(ue);
    }

    // ---------- MATIERE ----------

    @Override
    public List<Matiere> listMatieres() {
        return repository.listMatieres();
    }

    @Override
    public Matiere getMatiere(UUID id) {
        return repository.findMatiere(id).orElseThrow(() -> StructureException.notFound("Matiere", id));
    }

    @Override
    @Transactional
    public Matiere creerMatiere(Matiere matiere) {
        return repository.saveMatiere(matiere);
    }

    @Override
    @Transactional
    public Matiere updateMatiere(UUID id, Matiere matiere) {
        getMatiere(id);
        matiere.setIdMatiere(id);
        return repository.saveMatiere(matiere);
    }

    @Override
    @Transactional
    public void supprimerMatiere(UUID id) {
        getMatiere(id);
        repository.deleteMatiere(id);
    }

    // ---------- COURS ECUE ----------

    @Override
    public List<CoursECUE> listCours() {
        return repository.listCours();
    }

    @Override
    public CoursECUE getCours(UUID id) {
        return repository.findCours(id).orElseThrow(() -> StructureException.notFound("CoursECUE", id));
    }

    @Override
    @Transactional
    public CoursECUE creerCours(CoursECUE cours) {
        return repository.saveCours(cours);
    }

    @Override
    @Transactional
    public CoursECUE updateCours(UUID id, CoursECUE cours) {
        getCours(id);
        cours.setIdCours(id);
        return repository.saveCours(cours);
    }

    // ---------- SALLE ----------

    @Override
    public List<Salle> listSalles() {
        return repository.listSalles();
    }

    @Override
    public Salle getSalle(UUID id) {
        return repository.findSalle(id).orElseThrow(() -> StructureException.notFound("Salle", id));
    }

    @Override
    @Transactional
    public Salle creerSalle(Salle salle) {
        return repository.saveSalle(salle);
    }

    @Override
    @Transactional
    public Salle updateSalle(UUID id, Salle salle) {
        getSalle(id);
        salle.setIdSalle(id);
        return repository.saveSalle(salle);
    }

    // ---------- CRENEAU ----------

    @Override
    public List<Creneau> listCreneaux() {
        return repository.listCreneaux();
    }

    @Override
    public Creneau getCreneau(UUID id) {
        return repository.findCreneau(id).orElseThrow(() -> StructureException.notFound("Creneau", id));
    }

    @Override
    @Transactional
    public Creneau creerCreneau(Creneau creneau) {
        return repository.saveCreneau(creneau);
    }

    @Override
    @Transactional
    public Creneau updateCreneau(UUID id, Creneau creneau) {
        getCreneau(id);
        creneau.setIdCreneau(id);
        return repository.saveCreneau(creneau);
    }

    // ---------- REGIME HORAIRE ----------

    @Override
    public List<RegimeHoraire> listRegimesHoraires() {
        return repository.listRegimesHoraires();
    }

    @Override
    public RegimeHoraire getRegimeHoraire(String code) {
        return repository.findRegimeHoraire(code).orElseThrow(() -> StructureException.conflit("NOT_FOUND",
                "RegimeHoraire introuvable : " + code));
    }

    @Override
    @Transactional
    public RegimeHoraire creerRegimeHoraire(RegimeHoraire regime) {
        return repository.saveRegimeHoraire(regime);
    }

    @Override
    @Transactional
    public RegimeHoraire updateRegimeHoraire(String code, RegimeHoraire regime) {
        getRegimeHoraire(code);
        regime.setCode(code);
        return repository.saveRegimeHoraire(regime);
    }

    // ---------- EMPLOI DU TEMPS ----------

    @Override
    public List<EmploiDuTemps> listEmplois() {
        return repository.listEmplois();
    }

    @Override
    public EmploiDuTemps getEmploi(UUID id) {
        return repository.findEmploi(id).orElseThrow(() -> StructureException.notFound("EmploiDuTemps", id));
    }

    @Override
    @Transactional
    public EmploiDuTemps creerEmploi(EmploiDuTemps emploi) {
        return repository.saveEmploi(emploi);
    }

    @Override
    @Transactional
    public EmploiDuTemps ajouterPlage(UUID emploiId, PlageEmploi plage) {
        EmploiDuTemps emploi = getEmploi(emploiId);
        plage.setIdPlage(UUID.randomUUID());
        plage.setEmploiId(emploiId);
        emploi.getPlages().add(plage);
        emploi.detecterConflits();
        return repository.saveEmploi(emploi);
    }

    @Override
    @Transactional
    public EmploiDuTemps publierEmploi(UUID emploiId) {
        EmploiDuTemps emploi = getEmploi(emploiId);
        List<ConflitEmploi> conflits = emploi.detecterConflits();
        boolean bloquant = conflits.stream().anyMatch(ConflitEmploi::estBloquant);
        if (bloquant) {
            throw StructureException.conflit("CONFLIT_EMPLOI",
                    "Publication impossible : conflits bloquants detectes");
        }
        emploi.publier();
        return repository.saveEmploi(emploi);
    }

    @Override
    public List<ConflitEmploi> detecterConflits(UUID emploiId) {
        return getEmploi(emploiId).detecterConflits();
    }
}
