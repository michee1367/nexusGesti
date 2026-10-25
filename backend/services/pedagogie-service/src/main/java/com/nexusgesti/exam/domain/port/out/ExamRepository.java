package com.nexusgesti.exam.domain.port.out;

import com.nexusgesti.exam.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamRepository {

    SessionExamen saveSession(SessionExamen session);
    Optional<SessionExamen> findSession(UUID id);
    List<SessionExamen> listSessions();

    Epreuve saveEpreuve(Epreuve epreuve);
    Optional<Epreuve> findEpreuve(UUID id);
    List<Epreuve> listEpreuvesSession(UUID sessionId);

    Creneau saveCreneau(Creneau creneau);
    List<Creneau> listCreneauxSession(UUID sessionId);

    Convocation saveConvocation(Convocation convocation);
    Optional<Convocation> findConvocation(UUID id);
    List<Convocation> listConvocationsSession(UUID sessionId);
    long countConvocationsSession(UUID sessionId);

    NoteExamen saveNote(NoteExamen note);
    Optional<NoteExamen> findNote(UUID id);
    List<NoteExamen> listNotesEpreuve(UUID epreuveId);
    List<NoteExamen> listNotesEleve(UUID eleveId);

    Correction saveCorrection(Correction correction);
    Optional<Correction> findCorrection(UUID id);
    List<Correction> listCorrectionsEpreuve(UUID epreuveId);

    Deliberation saveDeliberation(Deliberation deliberation);
    Optional<Deliberation> findDeliberation(UUID id);
    List<Deliberation> listDeliberations(UUID sessionId);

    ResultatOfficiel saveResultat(ResultatOfficiel resultat);
    Optional<ResultatOfficiel> findResultat(UUID id);
    List<ResultatOfficiel> listResultatsDecision(UUID decisionId);

    Rattrapage saveRattrapage(Rattrapage rattrapage);
    Optional<Rattrapage> findRattrapage(UUID id);
    List<Rattrapage> listRattrapagesEleve(UUID eleveId);

    ProcesVerbal saveProcesVerbal(ProcesVerbal procesVerbal);
    Optional<ProcesVerbal> findProcesVerbal(UUID id);
    List<ProcesVerbal> listProcesVerbauxSession(UUID sessionId);
}
