package com.nexusgesti.exam.application.service;

import com.nexusgesti.exam.domain.ExamException;
import com.nexusgesti.exam.domain.model.*;
import com.nexusgesti.exam.domain.port.in.ExamUseCase;
import com.nexusgesti.exam.domain.port.out.ExamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class ExamService implements ExamUseCase {

    private final ExamRepository repository;

    public ExamService(ExamRepository repository) {
        this.repository = repository;
    }

    // ---------- SESSION ----------

    @Override
    public List<SessionExamen> listSessions(UUID anneeId, UUID classeId) {
        return repository.listSessions().stream()
                .filter(s -> anneeId == null || anneeId.equals(s.getAnneeId()))
                .filter(s -> classeId == null || classeId.equals(s.getClasseId()))
                .toList();
    }

    @Override
    public SessionExamen getSession(UUID id) {
        return repository.findSession(id)
                .orElseThrow(() -> ExamException.notFound("SessionExamen", id));
    }

    @Override
    @Transactional
    public SessionExamen creerSession(SessionExamen session) {
        if (session.getReference() == null || session.getReference().isBlank()) {
            session.setReference(reference("SES"));
        }
        if (session.getStatut() == null) {
            session.setStatut(StatutSession.BROUILLON);
        }
        if (session.getDateOuverture() == null) {
            session.setDateOuverture(LocalDate.now());
        }
        return repository.saveSession(session);
    }

    @Override
    @Transactional
    public SessionExamen programmerSession(UUID id) {
        SessionExamen session = getSession(id);
        session.programmer();
        return repository.saveSession(session);
    }

    @Override
    @Transactional
    public SessionExamen ouvrirSession(UUID id) {
        SessionExamen session = getSession(id);
        session.ouvrir();
        return repository.saveSession(session);
    }

    @Override
    @Transactional
    public SessionExamen cloturerSession(UUID id) {
        SessionExamen session = getSession(id);
        session.cloturer();
        return repository.saveSession(session);
    }

    @Override
    @Transactional
    public SessionExamen annulerSession(UUID id, String motif) {
        SessionExamen session = getSession(id);
        session.annuler(motif);
        return repository.saveSession(session);
    }

    // ---------- EPREUVE ----------

    @Override
    public List<Epreuve> listEpreuves(UUID sessionId) {
        return repository.listEpreuvesSession(sessionId);
    }

    @Override
    public Epreuve getEpreuve(UUID id) {
        return repository.findEpreuve(id)
                .orElseThrow(() -> ExamException.notFound("Epreuve", id));
    }

    @Override
    @Transactional
    public Epreuve ajouterEpreuve(UUID sessionId, Epreuve epreuve) {
        SessionExamen session = getSession(sessionId);
        epreuve.setSessionId(sessionId);
        if (epreuve.getReference() == null || epreuve.getReference().isBlank()) {
            epreuve.setReference(reference("EPR"));
        }
        if (epreuve.getStatut() == null) {
            epreuve.setStatut(StatutEpreuve.PROJETEE);
        }
        return repository.saveEpreuve(epreuve);
    }

    @Override
    @Transactional
    public Epreuve validerEpreuve(UUID id) {
        Epreuve epreuve = getEpreuve(id);
        epreuve.valider();
        return repository.saveEpreuve(epreuve);
    }

    @Override
    @Transactional
    public Epreuve declarerEpreuveHorsMoyenne(UUID id) {
        Epreuve epreuve = getEpreuve(id);
        epreuve.declarerHorsMoyenne();
        return repository.saveEpreuve(epreuve);
    }

    @Override
    @Transactional
    public Epreuve rattacherEpreuvePeriode(UUID id, UUID periodeId) {
        Epreuve epreuve = getEpreuve(id);
        epreuve.rattacherPeriode(periodeId);
        return repository.saveEpreuve(epreuve);
    }

    // ---------- CRENEAU ----------

    @Override
    public List<Creneau> listCreneaux(UUID sessionId) {
        return repository.listCreneauxSession(sessionId);
    }

    @Override
    @Transactional
    public Creneau ajouterCreneau(UUID sessionId, Creneau creneau) {
        getSession(sessionId);
        creneau.setSessionId(sessionId);
        return repository.saveCreneau(creneau);
    }

    // ---------- CONVOCATION ----------

    @Override
    public List<Convocation> listConvocations(UUID sessionId) {
        return repository.listConvocationsSession(sessionId);
    }

    @Override
    public Convocation getConvocation(UUID id) {
        return repository.findConvocation(id)
                .orElseThrow(() -> ExamException.notFound("Convocation", id));
    }

    @Override
    @Transactional
    public Convocation creerConvocation(UUID sessionId, Convocation convocation) {
        SessionExamen session = getSession(sessionId);
        convocation.setSessionId(sessionId);
        if (convocation.getNumero() == null || convocation.getNumero().isBlank()) {
            long numero = repository.countConvocationsSession(sessionId) + 1;
            convocation.setNumero(String.format("CNV-%03d", numero));
        }
        if (convocation.getStatut() == null) {
            convocation.setStatut(StatutConvocation.EMISE);
        }
        if (convocation.getDate() == null) {
            convocation.setDate(LocalDate.now());
        }
        convocation.setQrCode(convocation.getNumero());
        session.setConvocationEmise(true);
        Convocation saved = repository.saveConvocation(convocation);
        saved.setUrlTelechargement("/api/v1/ped/convocations/" + saved.getId());
        return repository.saveConvocation(saved);
    }

    @Override
    @Transactional
    public Convocation emettreConvocation(UUID id) {
        Convocation convocation = getConvocation(id);
        convocation.emettre();
        return repository.saveConvocation(convocation);
    }

    @Override
    @Transactional
    public Convocation notifierConvocation(UUID id, String canal) {
        Convocation convocation = getConvocation(id);
        convocation.notifier(canal);
        return repository.saveConvocation(convocation);
    }

    // ---------- NOTES ----------

    @Override
    public List<NoteExamen> listNotesEpreuve(UUID epreuveId) {
        return repository.listNotesEpreuve(epreuveId);
    }

    @Override
    public List<NoteExamen> listNotesEleve(UUID eleveId) {
        return repository.listNotesEleve(eleveId);
    }

    @Override
    @Transactional
    public NoteExamen saisirNoteExamen(UUID epreuveId, UUID eleveId, BigDecimal valeur, UUID correcteur) {
        Epreuve epreuve = getEpreuve(epreuveId);
        NoteExamen note = repository.listNotesEpreuve(epreuveId).stream()
                .filter(n -> eleveId.equals(n.getEleveId()))
                .findFirst()
                .orElseGet(() -> {
                    NoteExamen n = new NoteExamen();
                    n.setEpreuveId(epreuveId);
                    n.setEleveId(eleveId);
                    return n;
                });
        note.saisir(valeur, correcteur);
        note.setHorsMoyenne(epreuve.estHorsMoyenne());
        note.setCoefficient(epreuve.getCoefficient());
        return repository.saveNote(note);
    }

    @Override
    @Transactional
    public NoteExamen marquerAbsentExamen(UUID epreuveId, UUID eleveId, UUID correcteur) {
        NoteExamen note = repository.listNotesEpreuve(epreuveId).stream()
                .filter(n -> eleveId.equals(n.getEleveId()))
                .findFirst()
                .orElseGet(() -> {
                    NoteExamen n = new NoteExamen();
                    n.setEpreuveId(epreuveId);
                    n.setEleveId(eleveId);
                    return n;
                });
        note.marquerAbsent(correcteur);
        return repository.saveNote(note);
    }

    @Override
    @Transactional
    public NoteExamen exclureNoteExamen(UUID noteId, String motif) {
        NoteExamen note = getNote(noteId);
        note.exclure(motif);
        return repository.saveNote(note);
    }

    @Override
    @Transactional
    public NoteExamen contesterNoteExamen(UUID noteId, String motif) {
        NoteExamen note = getNote(noteId);
        note.contester(motif);
        return repository.saveNote(note);
    }

    @Override
    @Transactional
    public NoteExamen validerNoteExamen(UUID noteId) {
        NoteExamen note = getNote(noteId);
        note.valider();
        return repository.saveNote(note);
    }

    private NoteExamen getNote(UUID id) {
        return repository.findNote(id)
                .orElseThrow(() -> ExamException.notFound("NoteExamen", id));
    }

    // ---------- CORRECTION ----------

    @Override
    public Correction getCorrection(UUID id) {
        return repository.findCorrection(id)
                .orElseThrow(() -> ExamException.notFound("Correction", id));
    }

    @Override
    @Transactional
    public Correction ouvrirCorrection(UUID epreuveId, UUID correcteur) {
        Epreuve epreuve = getEpreuve(epreuveId);
        Correction correction = new Correction();
        correction.setEpreuveId(epreuveId);
        correction.setCorrecteur(correcteur);
        correction.ouvrir();
        correction.setNbNotesCorrigees(repository.listNotesEpreuve(epreuveId).size());
        return repository.saveCorrection(correction);
    }

    @Override
    @Transactional
    public Correction calculerCorrection(UUID id) {
        Correction correction = getCorrection(id);
        correction.calculerStatistiques(repository.listNotesEpreuve(correction.getEpreuveId()));
        return repository.saveCorrection(correction);
    }

    @Override
    @Transactional
    public Correction cloturerCorrection(UUID id) {
        Correction correction = getCorrection(id);
        correction.cloturer();
        return repository.saveCorrection(correction);
    }

    // ---------- DELIBERATION ----------

    @Override
    public List<Deliberation> listDeliberations(UUID sessionId) {
        return repository.listDeliberations(sessionId);
    }

    @Override
    public Deliberation getDeliberation(UUID id) {
        return repository.findDeliberation(id)
                .orElseThrow(() -> ExamException.notFound("Deliberation", id));
    }

    @Override
    @Transactional
    public Deliberation creerDeliberation(Deliberation deliberation) {
        if (deliberation.getReference() == null || deliberation.getReference().isBlank()) {
            deliberation.setReference(reference("DEL"));
        }
        if (deliberation.getStatut() == null) {
            deliberation.setStatut(StatutDeliberation.PLANIFIEE);
        }
        if (deliberation.getDate() == null) {
            deliberation.setDate(LocalDate.now());
        }
        return repository.saveDeliberation(deliberation);
    }

    @Override
    @Transactional
    public Deliberation planifierDeliberation(UUID id) {
        Deliberation deliberation = getDeliberation(id);
        deliberation.planifier();
        return repository.saveDeliberation(deliberation);
    }

    @Override
    @Transactional
    public Deliberation ouvrirDeliberation(UUID id) {
        Deliberation deliberation = getDeliberation(id);
        deliberation.ouvrir();
        return repository.saveDeliberation(deliberation);
    }

    @Override
    @Transactional
    public Deliberation cloturerDeliberation(UUID id) {
        Deliberation deliberation = getDeliberation(id);
        deliberation.cloturer();
        return repository.saveDeliberation(deliberation);
    }

    @Override
    @Transactional
    public Decision decider(UUID deliberationId, UUID classeId, UUID eleveId) {
        Deliberation deliberation = getDeliberation(deliberationId);
        Decision decision = deliberation.decider(classeId, eleveId);
        repository.saveDeliberation(deliberation);
        return decision;
    }

    @Override
    @Transactional
    public DecisionUE ajouterDecisionUE(UUID deliberationId, DecisionUE decisionUe) {
        Deliberation deliberation = getDeliberation(deliberationId);
        deliberation.ajouterDecisionUE(decisionUe);
        repository.saveDeliberation(deliberation);
        return decisionUe;
    }

    // ---------- RESULTAT ----------

    @Override
    public ResultatOfficiel getResultat(UUID id) {
        return repository.findResultat(id)
                .orElseThrow(() -> ExamException.notFound("ResultatOfficiel", id));
    }

    @Override
    @Transactional
    public ResultatOfficiel publierResultat(UUID decisionId, UUID generateur) {
        ResultatOfficiel resultat = new ResultatOfficiel();
        resultat.setDecisionId(decisionId);
        resultat.setReference(reference("RES"));
        resultat.setGenerateur(generateur);
        resultat.publier();
        ResultatOfficiel saved = repository.saveResultat(resultat);
        saved.setUrlDocument("/api/v1/ped/resultats/" + saved.getId());
        return repository.saveResultat(saved);
    }

    // ---------- RATTRAPAGE ----------

    @Override
    public List<Rattrapage> listRattrapages(UUID eleveId) {
        return repository.listRattrapagesEleve(eleveId);
    }

    @Override
    public Rattrapage getRattrapage(UUID id) {
        return repository.findRattrapage(id)
                .orElseThrow(() -> ExamException.notFound("Rattrapage", id));
    }

    @Override
    @Transactional
    public Rattrapage creerRattrapage(Rattrapage rattrapage) {
        rattrapage.inscrire();
        return repository.saveRattrapage(rattrapage);
    }

    @Override
    @Transactional
    public Rattrapage declarerExemptRattrapage(UUID id, String motif) {
        Rattrapage rattrapage = getRattrapage(id);
        rattrapage.declareExempt(motif);
        return repository.saveRattrapage(rattrapage);
    }

    // ---------- PROCES VERBAL ----------

    @Override
    public List<ProcesVerbal> listProcesVerbaux(UUID sessionId) {
        return repository.listProcesVerbauxSession(sessionId);
    }

    @Override
    public ProcesVerbal getProcesVerbal(UUID id) {
        return repository.findProcesVerbal(id)
                .orElseThrow(() -> ExamException.notFound("ProcesVerbal", id));
    }

    @Override
    @Transactional
    public ProcesVerbal creerProcesVerbal(ProcesVerbal procesVerbal, UUID par) {
        procesVerbal.setReference(reference("PV"));
        procesVerbal.rediger(par);
        return repository.saveProcesVerbal(procesVerbal);
    }

    @Override
    @Transactional
    public ProcesVerbal soumettreProcesVerbal(UUID id) {
        ProcesVerbal procesVerbal = getProcesVerbal(id);
        procesVerbal.soumettre();
        return repository.saveProcesVerbal(procesVerbal);
    }

    @Override
    @Transactional
    public ProcesVerbal signerProcesVerbal(UUID id, UUID utilisateur, String role) {
        ProcesVerbal procesVerbal = getProcesVerbal(id);
        procesVerbal.signer(utilisateur, role);
        return repository.saveProcesVerbal(procesVerbal);
    }

    @Override
    @Transactional
    public ProcesVerbal homologuerProcesVerbal(UUID id) {
        ProcesVerbal procesVerbal = getProcesVerbal(id);
        procesVerbal.homologuer();
        return repository.saveProcesVerbal(procesVerbal);
    }

    @Override
    @Transactional
    public ProcesVerbal contesterProcesVerbal(UUID id, String motif) {
        ProcesVerbal procesVerbal = getProcesVerbal(id);
        procesVerbal.contester(motif);
        return repository.saveProcesVerbal(procesVerbal);
    }

    private String reference(String prefixe) {
        return prefixe + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}