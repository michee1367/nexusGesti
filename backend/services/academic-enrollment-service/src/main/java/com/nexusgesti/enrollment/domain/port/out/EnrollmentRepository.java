package com.nexusgesti.enrollment.domain.port.out;

import com.nexusgesti.enrollment.domain.model.Groupe;
import com.nexusgesti.enrollment.domain.model.InscriptionUE;
import com.nexusgesti.enrollment.domain.model.RegleInscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository {

    InscriptionUE saveInscription(InscriptionUE inscription);
    Optional<InscriptionUE> findInscription(UUID id);
    List<InscriptionUE> listInscriptions();
    List<InscriptionUE> listInscriptionsEleve(UUID eleveId);

    Groupe saveGroupe(Groupe groupe);
    Optional<Groupe> findGroupe(UUID id);
    List<Groupe> listGroupes();

    RegleInscription saveRegle(RegleInscription regle);
    Optional<RegleInscription> findRegle(UUID id);
    List<RegleInscription> listRegles();
    void deleteRegle(UUID id);
}
