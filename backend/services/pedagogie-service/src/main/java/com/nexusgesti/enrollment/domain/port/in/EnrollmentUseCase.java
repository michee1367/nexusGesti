package com.nexusgesti.enrollment.domain.port.in;

import com.nexusgesti.enrollment.domain.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EnrollmentUseCase {

    List<InscriptionUE> listInscriptions();
    InscriptionUE getInscription(UUID id);
    InscriptionUE creerInscription(InscriptionUE inscription);
    InscriptionUE updateInscription(UUID id, InscriptionUE inscription);
    InscriptionUE validerInscription(UUID id, UUID par);
    InscriptionUE refuserInscription(UUID id, String motif);
    InscriptionUE mettreEnListeAttente(UUID id, int rang);
    InscriptionUE annulerInscription(UUID id);

    List<Groupe> listGroupes();
    Groupe getGroupe(UUID id);
    Groupe creerGroupe(Groupe groupe);
    Groupe updateGroupe(UUID id, Groupe groupe);
    Groupe ouvrirGroupe(UUID id);
    Groupe cloreGroupe(UUID id);
    List<MembreGroupe> listMembres(UUID groupeId);
    Groupe ajouterMembre(UUID groupeId, MembreGroupe membre);
    Groupe exclureMembre(UUID groupeId, UUID membreId);

    List<RegleInscription> listRegles();
    RegleInscription getRegle(UUID id);
    RegleInscription creerRegle(RegleInscription regle);
    RegleInscription updateRegle(UUID id, RegleInscription regle);
    void supprimerRegle(UUID id);
    DecisionInscription evaluer(Candidat candidat);

    ParcoursAcademique getParcours(UUID eleveId, UUID programmeId, BigDecimal creditsRequis);
}
