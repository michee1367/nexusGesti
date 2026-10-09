package com.nexusgesti.structure.domain.port.in;

import com.nexusgesti.structure.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface StructureUseCase {

    List<Niveau> listNiveaux();
    Niveau getNiveau(UUID id);
    Niveau creerNiveau(Niveau niveau);
    Niveau updateNiveau(UUID id, Niveau niveau);
    void supprimerNiveau(UUID id);

    List<Classe> listClasses();
    Classe getClasse(UUID id);
    Classe creerClasse(Classe classe);
    Classe updateClasse(UUID id, Classe classe);
    Classe ouvrirClasse(UUID id);
    Classe cloturerClasse(UUID id);

    List<Promotion> listPromotions();
    Promotion getPromotion(UUID id);
    Promotion creerPromotion(Promotion promotion);
    Promotion updatePromotion(UUID id, Promotion promotion);

    List<Semestre> listSemestres();
    Semestre getSemestre(UUID id);
    Semestre creerSemestre(Semestre semestre);
    Semestre updateSemestre(UUID id, Semestre semestre);

    List<UE> listUEs();
    UE getUE(UUID id);
    UE creerUE(UE ue);
    UE updateUE(UUID id, UE ue);

    List<Matiere> listMatieres();
    Matiere getMatiere(UUID id);
    Matiere creerMatiere(Matiere matiere);
    Matiere updateMatiere(UUID id, Matiere matiere);
    void supprimerMatiere(UUID id);

    List<CoursECUE> listCours();
    CoursECUE getCours(UUID id);
    CoursECUE creerCours(CoursECUE cours);
    CoursECUE updateCours(UUID id, CoursECUE cours);

    List<Salle> listSalles();
    Salle getSalle(UUID id);
    Salle creerSalle(Salle salle);
    Salle updateSalle(UUID id, Salle salle);

    List<Creneau> listCreneaux();
    Creneau getCreneau(UUID id);
    Creneau creerCreneau(Creneau creneau);
    Creneau updateCreneau(UUID id, Creneau creneau);

    List<RegimeHoraire> listRegimesHoraires();
    RegimeHoraire getRegimeHoraire(String code);
    RegimeHoraire creerRegimeHoraire(RegimeHoraire regime);
    RegimeHoraire updateRegimeHoraire(String code, RegimeHoraire regime);

    List<EmploiDuTemps> listEmplois();
    EmploiDuTemps getEmploi(UUID id);
    EmploiDuTemps creerEmploi(EmploiDuTemps emploi);
    EmploiDuTemps ajouterPlage(UUID emploiId, PlageEmploi plage);
    EmploiDuTemps publierEmploi(UUID emploiId);
    List<ConflitEmploi> detecterConflits(UUID emploiId);
}
