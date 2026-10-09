package com.nexusgesti.people.application;

import com.nexusgesti.people.domain.PeopleException;
import com.nexusgesti.people.domain.model.*;
import com.nexusgesti.people.domain.port.in.PeopleUseCase;
import com.nexusgesti.people.domain.port.out.InscriptionEventPublisher;
import com.nexusgesti.people.domain.port.out.PeopleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PeopleService implements PeopleUseCase {

    private final PeopleRepository repository;
    private final InscriptionEventPublisher eventPublisher;
    private final RegleAffectation regleAffectation = new RegleAffectation();

    public PeopleService(PeopleRepository repository, InscriptionEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    private Eleve chargerEleve(UUID id) {
        return repository.findEleve(id).orElseThrow(() -> PeopleException.notFound("Eleve", id));
    }

    private Enseignant chargerEnseignant(UUID id) {
        return repository.findEnseignant(id).orElseThrow(() -> PeopleException.notFound("Enseignant", id));
    }

    private String matricule(String prefix, String valeur) {
        return valeur == null || valeur.isBlank()
                ? prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : valeur;
    }

    // ---------- ELEVE ----------

    @Override
    public List<Eleve> listEleves() {
        return repository.listEleves();
    }

    @Override
    public Eleve getEleve(UUID id) {
        return chargerEleve(id);
    }

    @Override
    public Eleve creerEleve(Eleve eleve) {
        eleve.setIdEleve(UUID.randomUUID());
        eleve.setMatricule(matricule("ELV", eleve.getMatricule()));
        if (eleve.getStatut() == null) {
            eleve.setStatut(StatutEleve.PREINSCRIT);
        }
        if (eleve.getStatutDossier() == null) {
            eleve.setStatutDossier(StatutDossier.EN_COURS);
        }
        if (eleve.getDateInscription() == null) {
            eleve.setDateInscription(LocalDate.now());
        }
        return repository.saveEleve(eleve);
    }

    @Override
    public Eleve updateEleve(UUID id, Eleve eleve) {
        Eleve existant = chargerEleve(id);
        eleve.setIdEleve(id);
        if (eleve.getMatricule() == null) {
            eleve.setMatricule(existant.getMatricule());
        }
        if (eleve.getDateInscription() == null) {
            eleve.setDateInscription(existant.getDateInscription());
        }
        return repository.saveEleve(eleve);
    }

    @Override
    public Eleve transfererEleve(UUID id, String destination, String motif) {
        Eleve eleve = chargerEleve(id);
        eleve.transferer(destination, motif);
        return repository.saveEleve(eleve);
    }

    @Override
    public Eleve radierEleve(UUID id, String motif) {
        Eleve eleve = chargerEleve(id);
        eleve.radier(motif);
        return repository.saveEleve(eleve);
    }

    // ---------- DOSSIER ----------

    @Override
    public DossierEleve getDossier(UUID eleveId) {
        Eleve eleve = chargerEleve(eleveId);
        DossierEleve dossier = eleve.obtenirDossier();
        repository.saveEleve(eleve);
        return dossier;
    }

    @Override
    public DossierEleve ajouterPiece(UUID eleveId, FichierJoint piece) {
        Eleve eleve = chargerEleve(eleveId);
        DossierEleve dossier = eleve.obtenirDossier();
        if (piece.getId() == null) {
            piece.setId(UUID.randomUUID());
        }
        if (piece.getDateDepot() == null) {
            piece.setDateDepot(java.time.Instant.now());
        }
        dossier.ajouterPiece(piece);
        eleve.setStatutDossier(dossier.getStatut());
        repository.saveEleve(eleve);
        return dossier;
    }

    @Override
    public DossierEleve archiverDossier(UUID eleveId) {
        Eleve eleve = chargerEleve(eleveId);
        DossierEleve dossier = eleve.obtenirDossier();
        dossier.archiver();
        eleve.setStatutDossier(dossier.getStatut());
        repository.saveEleve(eleve);
        return dossier;
    }

    // ---------- INSCRIPTION ----------

    @Override
    public List<Inscription> listInscriptions(UUID eleveId) {
        return chargerEleve(eleveId).getInscriptions();
    }

    @Override
    public Inscription inscrire(UUID eleveId, Inscription inscription) {
        Eleve eleve = chargerEleve(eleveId);
        inscription.setIdInscription(UUID.randomUUID());
        if (inscription.getDateInscription() == null) {
            inscription.setDateInscription(LocalDate.now());
        }
        if (inscription.getStatut() == null) {
            inscription.setStatut(StatutInscription.EN_ATTENTE);
        }
        eleve.ajouterInscription(inscription);
        repository.saveEleve(eleve);
        eventPublisher.publierEleveInscrit(eleve, inscription);
        return inscription;
    }

    @Override
    public Inscription validerInscription(UUID eleveId, UUID inscriptionId) {
        Eleve eleve = chargerEleve(eleveId);
        Inscription inscription = eleve.getInscriptions().stream()
                .filter(i -> i.getIdInscription().equals(inscriptionId))
                .findFirst()
                .orElseThrow(() -> PeopleException.notFound("Inscription", inscriptionId));
        inscription.valider();
        if (eleve.getStatut() == StatutEleve.PREINSCRIT) {
            eleve.setStatut(StatutEleve.INSCRIT);
        }
        repository.saveEleve(eleve);
        return inscription;
    }

    @Override
    public Inscription annulerInscription(UUID eleveId, UUID inscriptionId, String motif) {
        Eleve eleve = chargerEleve(eleveId);
        Inscription inscription = eleve.getInscriptions().stream()
                .filter(i -> i.getIdInscription().equals(inscriptionId))
                .findFirst()
                .orElseThrow(() -> PeopleException.notFound("Inscription", inscriptionId));
        inscription.annuler(motif);
        repository.saveEleve(eleve);
        return inscription;
    }

    // ---------- CONGE / SANCTION ----------

    @Override
    public List<CongeEtude> listConges(UUID eleveId) {
        return chargerEleve(eleveId).getConges();
    }

    @Override
    public CongeEtude ouvrirConge(UUID eleveId, CongeEtude conge) {
        Eleve eleve = chargerEleve(eleveId);
        conge.setId(UUID.randomUUID());
        eleve.getConges().add(conge);
        repository.saveEleve(eleve);
        return conge;
    }

    @Override
    public List<SanctionDisciplinaire> listSanctions(UUID eleveId) {
        return chargerEleve(eleveId).getSanctions();
    }

    @Override
    public SanctionDisciplinaire prononcerSanction(UUID eleveId, SanctionDisciplinaire sanction) {
        Eleve eleve = chargerEleve(eleveId);
        sanction.setId(UUID.randomUUID());
        if (sanction.getDateSanction() == null) {
            sanction.setDateSanction(LocalDate.now());
        }
        eleve.getSanctions().add(sanction);
        repository.saveEleve(eleve);
        return sanction;
    }

    // ---------- PARENT ----------

    @Override
    public List<Parent> listParents() {
        return repository.listParents();
    }

    @Override
    public Parent getParent(UUID id) {
        return repository.findParent(id).orElseThrow(() -> PeopleException.notFound("Parent", id));
    }

    @Override
    public Parent creerParent(Parent parent) {
        parent.setId(UUID.randomUUID());
        if (parent.getMatricule() == null || parent.getMatricule().isBlank()) {
            parent.setMatricule(matricule("PAR", null));
        }
        return repository.saveParent(parent);
    }

    @Override
    public Parent updateParent(UUID id, Parent parent) {
        Parent existant = getParent(id);
        parent.setId(id);
        if (parent.getMatricule() == null) {
            parent.setMatricule(existant.getMatricule());
        }
        return repository.saveParent(parent);
    }

    @Override
    public List<LienParentalite> listLiens(UUID eleveId) {
        return repository.listLiens(eleveId);
    }

    @Override
    public LienParentalite lierParent(UUID eleveId, LienParentalite lien) {
        chargerEleve(eleveId);
        getParent(lien.getParentId());
        lien.setId(UUID.randomUUID());
        lien.setEleveId(eleveId);
        if (lien.getDateDebut() == null) {
            lien.setDateDebut(LocalDate.now());
        }
        return repository.saveLien(lien);
    }

    // ---------- SERVICE ----------

    @Override
    public List<ServiceEtablissement> listServices() {
        return repository.listServices();
    }

    @Override
    public ServiceEtablissement getService(UUID id) {
        return repository.findService(id).orElseThrow(() -> PeopleException.notFound("Service", id));
    }

    @Override
    public ServiceEtablissement creerService(ServiceEtablissement service) {
        service.setId(UUID.randomUUID());
        return repository.saveService(service);
    }

    @Override
    public ServiceEtablissement updateService(UUID id, ServiceEtablissement service) {
        getService(id);
        service.setId(id);
        return repository.saveService(service);
    }

    // ---------- AGENT ----------

    @Override
    public List<Agent> listAgents() {
        return repository.listAgents();
    }

    @Override
    public Agent getAgent(UUID id) {
        return repository.findAgent(id).orElseThrow(() -> PeopleException.notFound("Agent", id));
    }

    @Override
    public Agent creerAgent(Agent agent) {
        agent.setId(UUID.randomUUID());
        agent.setMatricule(matricule("AGT", agent.getMatricule()));
        return repository.saveAgent(agent);
    }

    @Override
    public Agent updateAgent(UUID id, Agent agent) {
        Agent existant = getAgent(id);
        agent.setId(id);
        if (agent.getMatricule() == null) {
            agent.setMatricule(existant.getMatricule());
        }
        return repository.saveAgent(agent);
    }

    // ---------- ENSEIGNANT ----------

    @Override
    public List<Enseignant> listEnseignants() {
        return repository.listEnseignants();
    }

    @Override
    public Enseignant getEnseignant(UUID id) {
        return chargerEnseignant(id);
    }

    @Override
    public Enseignant creerEnseignant(Enseignant enseignant) {
        enseignant.setId(UUID.randomUUID());
        enseignant.setMatricule(matricule("ENS", enseignant.getMatricule()));
        if (enseignant.getStatut() == null) {
            enseignant.setStatut(StatutEnseignant.A_PUBLIER);
        }
        return repository.saveEnseignant(enseignant);
    }

    @Override
    public Enseignant updateEnseignant(UUID id, Enseignant enseignant) {
        Enseignant existant = chargerEnseignant(id);
        enseignant.setId(id);
        if (enseignant.getMatricule() == null) {
            enseignant.setMatricule(existant.getMatricule());
        }
        if (enseignant.getAffectations().isEmpty()) {
            enseignant.setAffectations(existant.getAffectations());
        }
        return repository.saveEnseignant(enseignant);
    }

    @Override
    public Enseignant changerStatutEnseignant(UUID id, StatutEnseignant statut) {
        Enseignant enseignant = chargerEnseignant(id);
        enseignant.setStatut(statut);
        return repository.saveEnseignant(enseignant);
    }

    @Override
    public PieceIdentiteEnseignant ajouterPieceEnseignant(UUID enseignantId, PieceIdentiteEnseignant piece) {
        Enseignant enseignant = chargerEnseignant(enseignantId);
        piece.setId(UUID.randomUUID());
        enseignant.getPieces().add(piece);
        repository.saveEnseignant(enseignant);
        return piece;
    }

    // ---------- AFFECTATION ----------

    @Override
    public List<AffectationEnseignant> listAffectations(UUID enseignantId) {
        return repository.listAffectations(enseignantId);
    }

    @Override
    public AffectationEnseignant affecter(UUID enseignantId, AffectationEnseignant affectation) {
        Enseignant enseignant = chargerEnseignant(enseignantId);
        affectation.setId(UUID.randomUUID());
        if (affectation.getDateDebut() == null) {
            affectation.setDateDebut(LocalDate.now());
        }
        if (affectation.getType() == null) {
            affectation.setType(TypeAffection.ADJOINT);
        }
        enseignant.getAffectations().add(affectation);
        repository.saveEnseignant(enseignant);
        return affectation;
    }

    @Override
    public AffectationEnseignant rendreTitulaire(UUID affectationId) {
        Enseignant enseignant = chargerEnseignant(
                repository.findEnseignantIdByAffectation(affectationId)
                        .orElseThrow(() -> PeopleException.notFound("Affectation", affectationId)));
        AffectationEnseignant affectation = enseignant.getAffectations().stream()
                .filter(a -> a.getId().equals(affectationId))
                .findFirst()
                .orElseThrow(() -> PeopleException.notFound("Affectation", affectationId));
        affectation.rendreTitulaire();
        repository.saveEnseignant(enseignant);
        return affectation;
    }

    @Override
    public AffectationEnseignant cederTitularite(UUID affectationId) {
        Enseignant enseignant = chargerEnseignant(
                repository.findEnseignantIdByAffectation(affectationId)
                        .orElseThrow(() -> PeopleException.notFound("Affectation", affectationId)));
        AffectationEnseignant affectation = enseignant.getAffectations().stream()
                .filter(a -> a.getId().equals(affectationId))
                .findFirst()
                .orElseThrow(() -> PeopleException.notFound("Affectation", affectationId));
        affectation.cederTitularite();
        repository.saveEnseignant(enseignant);
        return affectation;
    }

    @Override
    public List<AffectationEnseignant> listAffectationsClasse(UUID classeId) {
        return repository.listAffectationsClasse(classeId);
    }

    // ---------- TITULARITE ----------

    @Override
    public List<Titularite> listTitularites(UUID classeId) {
        return repository.listTitularites(classeId);
    }

    @Override
    public Titularite designerTitulaire(UUID classeId, UUID enseignantId, Titularite titularite) {
        chargerEnseignant(enseignantId);
        titularite.setId(UUID.randomUUID());
        titularite.setClasseId(classeId);
        titularite.setEnseignantId(enseignantId);
        if (titularite.getDateDelegation() == null) {
            titularite.setDateDelegation(LocalDate.now());
        }
        return repository.saveTitularite(titularite);
    }

    @Override
    public ResultatValidation validerAffectation(String profil, UUID classeId) {
        return regleAffectation.valider(profil, classeId, repository.listAffectationsClasse(classeId));
    }
}
