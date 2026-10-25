package com.nexusgesti.people.adapter.in.web;

import com.nexusgesti.people.domain.model.*;
import com.nexusgesti.people.domain.port.in.PeopleUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PeopleController {

    private final PeopleUseCase peopleUseCase;

    public PeopleController(PeopleUseCase peopleUseCase) {
        this.peopleUseCase = peopleUseCase;
    }

    public record MotifRequest(String motif) {
    }

    public record TransfertRequest(String destination, String motif) {
    }

    public record StatutEnseignantRequest(StatutEnseignant statut) {
    }

    // ---------- ELEVES ----------

    @GetMapping("/eleves")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Eleve> listEleves() {
        return peopleUseCase.listEleves();
    }

    @PostMapping("/eleves")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Eleve> creerEleve(@RequestBody Eleve eleve) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.creerEleve(eleve));
    }

    @GetMapping("/eleves/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Eleve getEleve(@PathVariable UUID id) {
        return peopleUseCase.getEleve(id);
    }

    @PutMapping("/eleves/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Eleve updateEleve(@PathVariable UUID id, @RequestBody Eleve eleve) {
        return peopleUseCase.updateEleve(id, eleve);
    }

    @PostMapping("/eleves/{id}/transferer")
    @PreAuthorize("hasRole('ADMIN')")
    public Eleve transfererEleve(@PathVariable UUID id, @RequestBody TransfertRequest request) {
        return peopleUseCase.transfererEleve(id, request.destination(), request.motif());
    }

    @PostMapping("/eleves/{id}/radier")
    @PreAuthorize("hasRole('ADMIN')")
    public Eleve radierEleve(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return peopleUseCase.radierEleve(id, request.motif());
    }

    // ---------- DOSSIER ----------

    @GetMapping("/eleves/{id}/dossier")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public DossierEleve getDossier(@PathVariable UUID id) {
        return peopleUseCase.getDossier(id);
    }

    @PostMapping("/eleves/{id}/dossier/pieces")
    @PreAuthorize("hasRole('ADMIN')")
    public DossierEleve ajouterPiece(@PathVariable UUID id, @RequestBody FichierJoint piece) {
        return peopleUseCase.ajouterPiece(id, piece);
    }

    @PostMapping("/eleves/{id}/dossier/archiver")
    @PreAuthorize("hasRole('ADMIN')")
    public DossierEleve archiverDossier(@PathVariable UUID id) {
        return peopleUseCase.archiverDossier(id);
    }

    // ---------- INSCRIPTIONS ----------

    @GetMapping("/eleves/{id}/inscriptions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Inscription> listInscriptions(@PathVariable UUID id) {
        return peopleUseCase.listInscriptions(id);
    }

    @PostMapping("/eleves/{id}/inscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inscription> inscrire(@PathVariable UUID id, @RequestBody Inscription inscription) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.inscrire(id, inscription));
    }

    @PostMapping("/eleves/{id}/inscriptions/{inscriptionId}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public Inscription validerInscription(@PathVariable UUID id, @PathVariable UUID inscriptionId) {
        return peopleUseCase.validerInscription(id, inscriptionId);
    }

    @PostMapping("/eleves/{id}/inscriptions/{inscriptionId}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public Inscription annulerInscription(@PathVariable UUID id, @PathVariable UUID inscriptionId,
                                          @RequestBody MotifRequest request) {
        return peopleUseCase.annulerInscription(id, inscriptionId, request.motif());
    }

    // ---------- CONGES ----------

    @GetMapping("/eleves/{id}/conges")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CongeEtude> listConges(@PathVariable UUID id) {
        return peopleUseCase.listConges(id);
    }

    @PostMapping("/eleves/{id}/conges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CongeEtude> ouvrirConge(@PathVariable UUID id, @RequestBody CongeEtude conge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.ouvrirConge(id, conge));
    }

    // ---------- SANCTIONS ----------

    @GetMapping("/eleves/{id}/sanctions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<SanctionDisciplinaire> listSanctions(@PathVariable UUID id) {
        return peopleUseCase.listSanctions(id);
    }

    @PostMapping("/eleves/{id}/sanctions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanctionDisciplinaire> prononcerSanction(@PathVariable UUID id,
                                                                   @RequestBody SanctionDisciplinaire sanction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.prononcerSanction(id, sanction));
    }

    // ---------- LIENS ----------

    @GetMapping("/eleves/{id}/liens")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<LienParentalite> listLiens(@PathVariable UUID id) {
        return peopleUseCase.listLiens(id);
    }

    @PostMapping("/eleves/{id}/liens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LienParentalite> lierParent(@PathVariable UUID id, @RequestBody LienParentalite lien) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.lierParent(id, lien));
    }

    // ---------- PARENTS ----------

    @GetMapping("/parents")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Parent> listParents() {
        return peopleUseCase.listParents();
    }

    @PostMapping("/parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parent> creerParent(@RequestBody Parent parent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.creerParent(parent));
    }

    @GetMapping("/parents/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Parent getParent(@PathVariable UUID id) {
        return peopleUseCase.getParent(id);
    }

    @PutMapping("/parents/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Parent updateParent(@PathVariable UUID id, @RequestBody Parent parent) {
        return peopleUseCase.updateParent(id, parent);
    }

    // ---------- ENSEIGNANTS ----------

    @GetMapping("/enseignants")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Enseignant> listEnseignants() {
        return peopleUseCase.listEnseignants();
    }

    @PostMapping("/enseignants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Enseignant> creerEnseignant(@RequestBody Enseignant enseignant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.creerEnseignant(enseignant));
    }

    @GetMapping("/enseignants/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Enseignant getEnseignant(@PathVariable UUID id) {
        return peopleUseCase.getEnseignant(id);
    }

    @PutMapping("/enseignants/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Enseignant updateEnseignant(@PathVariable UUID id, @RequestBody Enseignant enseignant) {
        return peopleUseCase.updateEnseignant(id, enseignant);
    }

    @PostMapping("/enseignants/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public Enseignant changerStatutEnseignant(@PathVariable UUID id, @RequestBody StatutEnseignantRequest request) {
        return peopleUseCase.changerStatutEnseignant(id, request.statut());
    }

    @PostMapping("/enseignants/{id}/pieces")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PieceIdentiteEnseignant> ajouterPieceEnseignant(@PathVariable UUID id,
                                                                          @RequestBody PieceIdentiteEnseignant piece) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.ajouterPieceEnseignant(id, piece));
    }

    @GetMapping("/enseignants/{id}/affectations")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<AffectationEnseignant> listAffectations(@PathVariable UUID id) {
        return peopleUseCase.listAffectations(id);
    }

    @PostMapping("/enseignants/{id}/affectations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AffectationEnseignant> affecter(@PathVariable UUID id,
                                                          @RequestBody AffectationEnseignant affectation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.affecter(id, affectation));
    }

    // ---------- PERSONNEL : SERVICES ----------

    @GetMapping("/personnel/services")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<ServiceEtablissement> listServices() {
        return peopleUseCase.listServices();
    }

    @PostMapping("/personnel/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceEtablissement> creerService(@RequestBody ServiceEtablissement service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.creerService(service));
    }

    @GetMapping("/personnel/services/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ServiceEtablissement getService(@PathVariable UUID id) {
        return peopleUseCase.getService(id);
    }

    @PutMapping("/personnel/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceEtablissement updateService(@PathVariable UUID id, @RequestBody ServiceEtablissement service) {
        return peopleUseCase.updateService(id, service);
    }

    // ---------- PERSONNEL : AGENTS ----------

    @GetMapping("/personnel/agents")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Agent> listAgents() {
        return peopleUseCase.listAgents();
    }

    @PostMapping("/personnel/agents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Agent> creerAgent(@RequestBody Agent agent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peopleUseCase.creerAgent(agent));
    }

    @GetMapping("/personnel/agents/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Agent getAgent(@PathVariable UUID id) {
        return peopleUseCase.getAgent(id);
    }

    @PutMapping("/personnel/agents/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Agent updateAgent(@PathVariable UUID id, @RequestBody Agent agent) {
        return peopleUseCase.updateAgent(id, agent);
    }

    // ---------- PERSONNEL : AFFECTATIONS ----------

    @PostMapping("/personnel/affectations/{id}/titulaire")
    @PreAuthorize("hasRole('ADMIN')")
    public AffectationEnseignant rendreTitulaire(@PathVariable UUID id) {
        return peopleUseCase.rendreTitulaire(id);
    }

    @PostMapping("/personnel/affectations/{id}/ceder")
    @PreAuthorize("hasRole('ADMIN')")
    public AffectationEnseignant cederTitularite(@PathVariable UUID id) {
        return peopleUseCase.cederTitularite(id);
    }

    @GetMapping("/personnel/affectations/classe/{classeId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<AffectationEnseignant> listAffectationsClasse(@PathVariable UUID classeId) {
        return peopleUseCase.listAffectationsClasse(classeId);
    }

    @PostMapping("/personnel/affectations/valider")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResultatValidation validerAffectation(@RequestParam String profil, @RequestParam UUID classeId) {
        return peopleUseCase.validerAffectation(profil, classeId);
    }

    // ---------- PERSONNEL : TITULARITES ----------

    @GetMapping("/personnel/titularites/classe/{classeId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Titularite> listTitularites(@PathVariable UUID classeId) {
        return peopleUseCase.listTitularites(classeId);
    }

    @PostMapping("/personnel/titularites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Titularite> designerTitulaire(@RequestBody Titularite titularite) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peopleUseCase.designerTitulaire(titularite.getClasseId(), titularite.getEnseignantId(), titularite));
    }
}
