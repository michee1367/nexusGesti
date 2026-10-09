package com.nexusgesti.structure.domain.port.out;

import com.nexusgesti.structure.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StructureRepository {

    Niveau saveNiveau(Niveau niveau);
    Optional<Niveau> findNiveau(UUID id);
    List<Niveau> listNiveaux();
    void deleteNiveau(UUID id);

    Classe saveClasse(Classe classe);
    Optional<Classe> findClasse(UUID id);
    List<Classe> listClasses();

    Promotion savePromotion(Promotion promotion);
    Optional<Promotion> findPromotion(UUID id);
    List<Promotion> listPromotions();

    Semestre saveSemestre(Semestre semestre);
    Optional<Semestre> findSemestre(UUID id);
    List<Semestre> listSemestres();

    UE saveUE(UE ue);
    Optional<UE> findUE(UUID id);
    List<UE> listUEs();

    Matiere saveMatiere(Matiere matiere);
    Optional<Matiere> findMatiere(UUID id);
    List<Matiere> listMatieres();
    void deleteMatiere(UUID id);

    CoursECUE saveCours(CoursECUE cours);
    Optional<CoursECUE> findCours(UUID id);
    List<CoursECUE> listCours();

    Salle saveSalle(Salle salle);
    Optional<Salle> findSalle(UUID id);
    List<Salle> listSalles();

    Creneau saveCreneau(Creneau creneau);
    Optional<Creneau> findCreneau(UUID id);
    List<Creneau> listCreneaux();

    RegimeHoraire saveRegimeHoraire(RegimeHoraire regime);
    Optional<RegimeHoraire> findRegimeHoraire(String code);
    List<RegimeHoraire> listRegimesHoraires();

    EmploiDuTemps saveEmploi(EmploiDuTemps emploi);
    Optional<EmploiDuTemps> findEmploi(UUID id);
    List<EmploiDuTemps> listEmplois();
}
