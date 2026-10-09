package com.nexusgesti.people.domain.port.out;

import com.nexusgesti.people.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeopleRepository {

    Eleve saveEleve(Eleve eleve);
    Optional<Eleve> findEleve(UUID id);
    List<Eleve> listEleves();

    Parent saveParent(Parent parent);
    Optional<Parent> findParent(UUID id);
    List<Parent> listParents();

    LienParentalite saveLien(LienParentalite lien);
    List<LienParentalite> listLiens(UUID eleveId);

    ServiceEtablissement saveService(ServiceEtablissement service);
    Optional<ServiceEtablissement> findService(UUID id);
    List<ServiceEtablissement> listServices();

    Agent saveAgent(Agent agent);
    Optional<Agent> findAgent(UUID id);
    List<Agent> listAgents();

    Enseignant saveEnseignant(Enseignant enseignant);
    Optional<Enseignant> findEnseignant(UUID id);
    List<Enseignant> listEnseignants();

    AffectationEnseignant saveAffectation(UUID enseignantId, AffectationEnseignant affectation);
    Optional<UUID> findEnseignantIdByAffectation(UUID affectationId);
    List<AffectationEnseignant> listAffectations(UUID enseignantId);
    List<AffectationEnseignant> listAffectationsClasse(UUID classeId);

    Titularite saveTitularite(Titularite titularite);
    List<Titularite> listTitularites(UUID classeId);
}
