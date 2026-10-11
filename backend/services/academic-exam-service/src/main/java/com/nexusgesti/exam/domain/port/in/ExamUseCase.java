package com.nexusgesti.exam.domain.port.in;

import com.nexusgesti.exam.domain.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ExamUseCase {

    List<SessionExamen> listSessions(UUID anneeId, UUID classeId);
    SessionExamen getSession(UUID id);
    SessionExamen creerSession(SessionExamen session);
    SessionExamen programmerSession(UUID id);
    SessionExamen ouvrirSession(UUID id);
    SessionExamen cloturerSession(UUID id);
    SessionExamen annulerSession(UUID id, String motif);

    List<Epreuve> listEpreuves(UUID sessionId);
    Epreuve getEpreuve(UUID id);
    Epreuve ajouterEpreuve(UUID sessionId, Epreuve epreuve);
    Epreuve validerEpreuve(UUID id);
    Epreuve declarerEpreuveHorsMoyenne(UUID id);
    Epreuve rattacherEpreuvePeriode(UUID id, UUID periodeId);

    List<Creneau> listCreneaux(UUID sessionId);
    Creneau ajouterCreneau(UUID sessionId, Creneau creneau);

    List<Convocation> listConvocations(UUID sessionId);
    Convocation getConvocation(UUID id);
    Convocation creerConvocation(UUID sessionId, Convocation convocation);
    Convocation emettreConvocation(UUID id);
    Convocation notifierConvocation(UUID id, String canal);

    List<NoteExamen> listNotesEpreuve(UUID epreuveId);
    List<NoteExamen> listNotesEleve(UUID eleveId);
    NoteExamen saisirNoteExamen(UUID epreuveId, UUID eleveId, BigDecimal valeur, UUID correcteur);
    NoteExamen marquerAbsentExamen(UUID epreuveId, UUID eleveId, UUID correcteur);
    NoteExamen exclureNoteExamen(UUID noteId, String motif);
    NoteExamen contesterNoteExamen(UUID noteId, String motif);
    NoteExamen validerNoteExamen(UUID noteId);

    Correction getCorrection(UUID id);
    Correction ouvrirCorrection(UUID epreuveId, UUID correcteur);
    Correction calculerCorrection(UUID id);
    Correction cloturerCorrection(UUID id);

    List<Deliberation> listDeliberations(UUID sessionId);
    Deliberation getDeliberation(UUID id);
    Deliberation creerDeliberation(Deliberation deliberation);
    Deliberation planifierDeliberation(UUID id);
    Deliberation ouvrirDeliberation(UUID id);
    Deliberation cloturerDeliberation(UUID id);
    Decision decider(UUID deliberationId, UUID classeId, UUID eleveId);
    DecisionUE ajouterDecisionUE(UUID deliberationId, DecisionUE decisionUe);

    ResultatOfficiel getResultat(UUID id);
    ResultatOfficiel publierResultat(UUID decisionId, UUID generateur);

    List<Rattrapage> listRattrapages(UUID eleveId);
    Rattrapage getRattrapage(UUID id);
    Rattrapage creerRattrapage(Rattrapage rattrapage);
    Rattrapage declarerExemptRattrapage(UUID id, String motif);

    List<ProcesVerbal> listProcesVerbaux(UUID sessionId);
    ProcesVerbal getProcesVerbal(UUID id);
    ProcesVerbal creerProcesVerbal(ProcesVerbal procesVerbal, UUID par);
    ProcesVerbal soumettreProcesVerbal(UUID id);
    ProcesVerbal signerProcesVerbal(UUID id, UUID utilisateur, String role);
    ProcesVerbal homologuerProcesVerbal(UUID id);
    ProcesVerbal contesterProcesVerbal(UUID id, String motif);
}
