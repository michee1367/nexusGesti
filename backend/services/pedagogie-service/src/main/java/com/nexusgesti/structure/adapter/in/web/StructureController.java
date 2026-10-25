package com.nexusgesti.structure.adapter.in.web;

import com.nexusgesti.structure.domain.model.*;
import com.nexusgesti.structure.domain.port.in.StructureUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StructureController {

    private final StructureUseCase structureUseCase;

    public StructureController(StructureUseCase structureUseCase) {
        this.structureUseCase = structureUseCase;
    }

    // ---------- NIVEAUX ----------

    @GetMapping("/niveaux")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Niveau> listNiveaux() {
        return structureUseCase.listNiveaux();
    }

    @PostMapping("/niveaux")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Niveau> creerNiveau(@RequestBody Niveau niveau) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerNiveau(niveau));
    }

    @GetMapping("/niveaux/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Niveau getNiveau(@PathVariable UUID id) {
        return structureUseCase.getNiveau(id);
    }

    @PutMapping("/niveaux/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Niveau updateNiveau(@PathVariable UUID id, @RequestBody Niveau niveau) {
        return structureUseCase.updateNiveau(id, niveau);
    }

    @DeleteMapping("/niveaux/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerNiveau(@PathVariable UUID id) {
        structureUseCase.supprimerNiveau(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- CLASSES ----------

    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Classe> listClasses() {
        return structureUseCase.listClasses();
    }

    @PostMapping("/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Classe> creerClasse(@RequestBody Classe classe) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerClasse(classe));
    }

    @GetMapping("/classes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Classe getClasse(@PathVariable UUID id) {
        return structureUseCase.getClasse(id);
    }

    @PutMapping("/classes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Classe updateClasse(@PathVariable UUID id, @RequestBody Classe classe) {
        return structureUseCase.updateClasse(id, classe);
    }

    @PostMapping("/classes/{id}/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public Classe ouvrirClasse(@PathVariable UUID id) {
        return structureUseCase.ouvrirClasse(id);
    }

    @PostMapping("/classes/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public Classe cloturerClasse(@PathVariable UUID id) {
        return structureUseCase.cloturerClasse(id);
    }

    // ---------- PROMOTIONS ----------

    @GetMapping("/promotions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Promotion> listPromotions() {
        return structureUseCase.listPromotions();
    }

    @PostMapping("/promotions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Promotion> creerPromotion(@RequestBody Promotion promotion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerPromotion(promotion));
    }

    @GetMapping("/promotions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Promotion getPromotion(@PathVariable UUID id) {
        return structureUseCase.getPromotion(id);
    }

    @PutMapping("/promotions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Promotion updatePromotion(@PathVariable UUID id, @RequestBody Promotion promotion) {
        return structureUseCase.updatePromotion(id, promotion);
    }

    // ---------- SEMESTRES ----------

    @GetMapping("/semestres")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Semestre> listSemestres() {
        return structureUseCase.listSemestres();
    }

    @PostMapping("/semestres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Semestre> creerSemestre(@RequestBody Semestre semestre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerSemestre(semestre));
    }

    @GetMapping("/semestres/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Semestre getSemestre(@PathVariable UUID id) {
        return structureUseCase.getSemestre(id);
    }

    @PutMapping("/semestres/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Semestre updateSemestre(@PathVariable UUID id, @RequestBody Semestre semestre) {
        return structureUseCase.updateSemestre(id, semestre);
    }

    // ---------- UE ----------

    @GetMapping("/ues")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<UE> listUEs() {
        return structureUseCase.listUEs();
    }

    @PostMapping("/ues")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UE> creerUE(@RequestBody UE ue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerUE(ue));
    }

    @GetMapping("/ues/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public UE getUE(@PathVariable UUID id) {
        return structureUseCase.getUE(id);
    }

    @PutMapping("/ues/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UE updateUE(@PathVariable UUID id, @RequestBody UE ue) {
        return structureUseCase.updateUE(id, ue);
    }

    // ---------- MATIERES ----------

    @GetMapping("/matieres")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Matiere> listMatieres() {
        return structureUseCase.listMatieres();
    }

    @PostMapping("/matieres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Matiere> creerMatiere(@RequestBody Matiere matiere) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerMatiere(matiere));
    }

    @GetMapping("/matieres/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Matiere getMatiere(@PathVariable UUID id) {
        return structureUseCase.getMatiere(id);
    }

    @PutMapping("/matieres/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Matiere updateMatiere(@PathVariable UUID id, @RequestBody Matiere matiere) {
        return structureUseCase.updateMatiere(id, matiere);
    }

    @DeleteMapping("/matieres/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerMatiere(@PathVariable UUID id) {
        structureUseCase.supprimerMatiere(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- COURS ECUE ----------

    @GetMapping("/cours")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CoursECUE> listCours() {
        return structureUseCase.listCours();
    }

    @PostMapping("/cours")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoursECUE> creerCours(@RequestBody CoursECUE cours) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerCours(cours));
    }

    @GetMapping("/cours/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public CoursECUE getCours(@PathVariable UUID id) {
        return structureUseCase.getCours(id);
    }

    @PutMapping("/cours/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CoursECUE updateCours(@PathVariable UUID id, @RequestBody CoursECUE cours) {
        return structureUseCase.updateCours(id, cours);
    }

    // ---------- SALLES ----------

    @GetMapping("/salles")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Salle> listSalles() {
        return structureUseCase.listSalles();
    }

    @PostMapping("/salles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Salle> creerSalle(@RequestBody Salle salle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerSalle(salle));
    }

    @GetMapping("/salles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Salle getSalle(@PathVariable UUID id) {
        return structureUseCase.getSalle(id);
    }

    @PutMapping("/salles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Salle updateSalle(@PathVariable UUID id, @RequestBody Salle salle) {
        return structureUseCase.updateSalle(id, salle);
    }

    // ---------- CRENEAUX ----------

    @GetMapping("/creneaux")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Creneau> listCreneaux() {
        return structureUseCase.listCreneaux();
    }

    @PostMapping("/creneaux")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Creneau> creerCreneau(@RequestBody Creneau creneau) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerCreneau(creneau));
    }

    @GetMapping("/creneaux/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Creneau getCreneau(@PathVariable UUID id) {
        return structureUseCase.getCreneau(id);
    }

    @PutMapping("/creneaux/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Creneau updateCreneau(@PathVariable UUID id, @RequestBody Creneau creneau) {
        return structureUseCase.updateCreneau(id, creneau);
    }

    // ---------- REGIMES HORAIRES ----------

    @GetMapping("/regimes-horaires")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<RegimeHoraire> listRegimesHoraires() {
        return structureUseCase.listRegimesHoraires();
    }

    @PostMapping("/regimes-horaires")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegimeHoraire> creerRegimeHoraire(@RequestBody RegimeHoraire regime) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerRegimeHoraire(regime));
    }

    @GetMapping("/regimes-horaires/{code}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public RegimeHoraire getRegimeHoraire(@PathVariable String code) {
        return structureUseCase.getRegimeHoraire(code);
    }

    @PutMapping("/regimes-horaires/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public RegimeHoraire updateRegimeHoraire(@PathVariable String code, @RequestBody RegimeHoraire regime) {
        return structureUseCase.updateRegimeHoraire(code, regime);
    }

    // ---------- EMPLOIS DU TEMPS ----------

    @GetMapping("/emplois-du-temps")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<EmploiDuTemps> listEmplois() {
        return structureUseCase.listEmplois();
    }

    @PostMapping("/emplois-du-temps")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmploiDuTemps> creerEmploi(@RequestBody EmploiDuTemps emploi) {
        return ResponseEntity.status(HttpStatus.CREATED).body(structureUseCase.creerEmploi(emploi));
    }

    @GetMapping("/emplois-du-temps/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public EmploiDuTemps getEmploi(@PathVariable UUID id) {
        return structureUseCase.getEmploi(id);
    }

    @PostMapping("/emplois-du-temps/{id}/plages")
    @PreAuthorize("hasRole('ADMIN')")
    public EmploiDuTemps ajouterPlage(@PathVariable UUID id, @RequestBody PlageEmploi plage) {
        return structureUseCase.ajouterPlage(id, plage);
    }

    @PostMapping("/emplois-du-temps/{id}/publier")
    @PreAuthorize("hasRole('ADMIN')")
    public EmploiDuTemps publierEmploi(@PathVariable UUID id) {
        return structureUseCase.publierEmploi(id);
    }

    @GetMapping("/emplois-du-temps/{id}/conflits")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<ConflitEmploi> detecterConflits(@PathVariable UUID id) {
        return structureUseCase.detecterConflits(id);
    }
}
