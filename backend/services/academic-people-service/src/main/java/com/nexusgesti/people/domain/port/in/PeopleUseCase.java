package com.nexusgesti.people.domain.port.in;

import com.nexusgesti.people.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface PeopleUseCase {

    List<Eleve> listEleves();
    Eleve getEleve(UUID id);
    Eleve creerEleve(Eleve eleve);
    Eleve updateEleve(UUID id, Eleve eleve);
    Eleve transfererEleve(UUID id, String destination, String motif);
    Eleve radierEleve(UUID id, String motif);

    DossierEleve getDossier(UUID eleveId);
    DossierEleve ajouterPiece(UUID eleveId, FichierJoint piece);
    DossierEleve archiverDossier(UUID eleveId);

    List<Inscription> listInscriptions(UUID eleveId);
    Inscription inscrire(UUID eleveId, Inscription inscription);
    Inscription validerInscription(UUID eleveId, UUID inscriptionId);
    Inscription annulerInscription(UUID eleveId, UUID inscriptionId, String motif);

    List<CongeEtude> listConges(UUID eleveId);
    CongeEtude ouvrirConge(UUID eleveId, CongeEtude conge);

    List<SanctionDisciplinaire> listSanctions(UUID eleveId);
    SanctionDisciplinaire prononcerSanction(UUID eleveId, SanctionDisciplinaire sanction);

    List<Parent> listParents();
    Parent getParent(UUID id);
    Parent creerParent(Parent parent);
    Parent updateParent(UUID id, Parent parent);

    List<LienParentalite> listLiens(UUID eleveId);
    LienParentalite lierParent(UUID eleveId, LienParentalite lien);

    List<ServiceEtablissement> listServices();
    ServiceEtablissement getService(UUID id);
    ServiceEtablissement creerService(ServiceEtablissement service);
    ServiceEtablissement updateService(UUID id, ServiceEtablissement service);

    List<Agent> listAgents();
    Agent getAgent(UUID id);
    Agent creerAgent(Agent agent);
    Agent updateAgent(UUID id, Agent agent);

    List<Enseignant> listEnseignants();
    Enseignant getEnseignant(UUID id);
    Enseignant creerEnseignant(Enseignant enseignant);
    Enseignant updateEnseignant(UUID id, Enseignant enseignant);
    Enseignant changerStatutEnseignant(UUID id, StatutEnseignant statut);
    PieceIdentiteEnseignant ajouterPieceEnseignant(UUID enseignantId, PieceIdentiteEnseignant piece);

    List<AffectationEnseignant> listAffectations(UUID enseignantId);
    AffectationEnseignant affecter(UUID enseignantId, AffectationEnseignant affectation);
    AffectationEnseignant rendreTitulaire(UUID affectationId);
    AffectationEnseignant cederTitularite(UUID affectationId);
    List<AffectationEnseignant> listAffectationsClasse(UUID classeId);

    List<Titularite> listTitularites(UUID classeId);
    Titularite designerTitulaire(UUID classeId, UUID enseignantId, Titularite titularite);

    ResultatValidation validerAffectation(String profil, UUID classeId);
}
