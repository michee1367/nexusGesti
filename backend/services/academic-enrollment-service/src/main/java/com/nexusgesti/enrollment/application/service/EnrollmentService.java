package com.nexusgesti.enrollment.application.service;

import com.nexusgesti.enrollment.domain.EnrollmentException;
import com.nexusgesti.enrollment.domain.model.*;
import com.nexusgesti.enrollment.domain.port.in.EnrollmentUseCase;
import com.nexusgesti.enrollment.domain.port.out.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService implements EnrollmentUseCase {

    private final EnrollmentRepository repository;

    public EnrollmentService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    // ---------- INSCRIPTION UE ----------

    @Override
    public List<InscriptionUE> listInscriptions() {
        return repository.listInscriptions();
    }

    @Override
    public InscriptionUE getInscription(UUID id) {
        return repository.findInscription(id).orElseThrow(() -> EnrollmentException.notFound("InscriptionUE", id));
    }

    @Override
    @Transactional
    public InscriptionUE creerInscription(InscriptionUE inscription) {
        if (inscription.getStatut() == null) {
            inscription.inscrire();
        }
        return repository.saveInscription(inscription);
    }

    @Override
    @Transactional
    public InscriptionUE updateInscription(UUID id, InscriptionUE inscription) {
        getInscription(id);
        inscription.setIdInscription(id);
        return repository.saveInscription(inscription);
    }

    @Override
    @Transactional
    public InscriptionUE validerInscription(UUID id, UUID par) {
        InscriptionUE inscription = getInscription(id);
        inscription.valider(par);
        return repository.saveInscription(inscription);
    }

    @Override
    @Transactional
    public InscriptionUE refuserInscription(UUID id, String motif) {
        InscriptionUE inscription = getInscription(id);
        inscription.refuser(motif);
        return repository.saveInscription(inscription);
    }

    @Override
    @Transactional
    public InscriptionUE mettreEnListeAttente(UUID id, int rang) {
        InscriptionUE inscription = getInscription(id);
        inscription.placerEnListeAttente(rang);
        return repository.saveInscription(inscription);
    }

    @Override
    @Transactional
    public InscriptionUE annulerInscription(UUID id) {
        InscriptionUE inscription = getInscription(id);
        inscription.annuler();
        return repository.saveInscription(inscription);
    }

    // ---------- GROUPE ----------

    @Override
    public List<Groupe> listGroupes() {
        return repository.listGroupes();
    }

    @Override
    public Groupe getGroupe(UUID id) {
        return repository.findGroupe(id).orElseThrow(() -> EnrollmentException.notFound("Groupe", id));
    }

    @Override
    @Transactional
    public Groupe creerGroupe(Groupe groupe) {
        if (groupe.getStatut() == null) {
            groupe.ouvrir();
        }
        return repository.saveGroupe(groupe);
    }

    @Override
    @Transactional
    public Groupe updateGroupe(UUID id, Groupe groupe) {
        getGroupe(id);
        groupe.setIdGroupe(id);
        return repository.saveGroupe(groupe);
    }

    @Override
    @Transactional
    public Groupe ouvrirGroupe(UUID id) {
        Groupe groupe = getGroupe(id);
        groupe.ouvrir();
        return repository.saveGroupe(groupe);
    }

    @Override
    @Transactional
    public Groupe cloreGroupe(UUID id) {
        Groupe groupe = getGroupe(id);
        groupe.clore();
        return repository.saveGroupe(groupe);
    }

    @Override
    public List<MembreGroupe> listMembres(UUID groupeId) {
        return getGroupe(groupeId).getMembres();
    }

    @Override
    @Transactional
    public Groupe ajouterMembre(UUID groupeId, MembreGroupe membre) {
        Groupe groupe = getGroupe(groupeId);
        if (membre.getDateAffectation() == null) {
            membre.setDateAffectation(LocalDate.now());
        }
        if (membre.getRang() == null) {
            membre.setRang(groupe.getEffectif() + 1);
        }
        groupe.ajouterMembre(membre);
        return repository.saveGroupe(groupe);
    }

    @Override
    @Transactional
    public Groupe exclureMembre(UUID groupeId, UUID membreId) {
        Groupe groupe = getGroupe(groupeId);
        groupe.exclureMembre(membreId)
                .orElseThrow(() -> EnrollmentException.notFound("MembreGroupe", membreId));
        return repository.saveGroupe(groupe);
    }

    // ---------- REGLE INSCRIPTION ----------

    @Override
    public List<RegleInscription> listRegles() {
        return repository.listRegles();
    }

    @Override
    public RegleInscription getRegle(UUID id) {
        return repository.findRegle(id).orElseThrow(() -> EnrollmentException.notFound("RegleInscription", id));
    }

    @Override
    @Transactional
    public RegleInscription creerRegle(RegleInscription regle) {
        return repository.saveRegle(regle);
    }

    @Override
    @Transactional
    public RegleInscription updateRegle(UUID id, RegleInscription regle) {
        getRegle(id);
        regle.setIdRegle(id);
        return repository.saveRegle(regle);
    }

    @Override
    @Transactional
    public void supprimerRegle(UUID id) {
        getRegle(id);
        repository.deleteRegle(id);
    }

    @Override
    public DecisionInscription evaluer(Candidat candidat) {
        List<RegleInscription> regles = repository.listRegles().stream()
                .filter(r -> r.sAppliqueA(candidat.getProgrammeId()))
                .sorted(Comparator.comparingInt(RegleInscription::getPriorite).reversed())
                .toList();
        boolean ineligible = regles.stream()
                .anyMatch(r -> r.evaluer(candidat) == DecisionInscription.INELIGIBLE);
        return ineligible ? DecisionInscription.INELIGIBLE : DecisionInscription.ELIGIBLE;
    }

    // ---------- PARCOURS ----------

    @Override
    public ParcoursAcademique getParcours(UUID eleveId, UUID programmeId, BigDecimal creditsRequis) {
        List<InscriptionUE> inscriptions = repository.listInscriptionsEleve(eleveId).stream()
                .filter(i -> programmeId == null || programmeId.equals(i.getProgrammeId()))
                .toList();
        ParcoursAcademique parcours = new ParcoursAcademique();
        parcours.setEleveId(eleveId);
        parcours.setProgrammeId(programmeId);
        parcours.setCreditsRequis(creditsRequis);
        parcours.setInscriptions(inscriptions);
        return parcours;
    }
}
