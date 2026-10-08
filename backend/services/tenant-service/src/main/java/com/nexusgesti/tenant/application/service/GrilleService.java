package com.nexusgesti.tenant.application.service;

import com.nexusgesti.tenant.domain.TenantException;
import com.nexusgesti.tenant.domain.model.*;
import com.nexusgesti.tenant.domain.port.in.GrilleUseCase;
import com.nexusgesti.tenant.domain.port.out.GrilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GrilleService implements GrilleUseCase {

    private final GrilleRepository grilleRepository;

    public GrilleService(GrilleRepository grilleRepository) {
        this.grilleRepository = grilleRepository;
    }

    @Override
    @Transactional
    public Grille creer(UUID etablissementId, String type, Grille grille) {
        grille.setId(UUID.randomUUID());
        grille.setEtablissementId(etablissementId);
        grille.setVersion(1);
        grille.setActive(false);
        grille.setModifiable(true);
        grille.setDateActivation(null);
        return persister(type, grille);
    }

    @Override
    public Grille get(UUID etablissementId, String type, UUID grilleId) {
        return trouver(etablissementId, type, grilleId);
    }

    @Override
    @Transactional
    public Grille update(UUID etablissementId, String type, UUID grilleId, Grille grille) {
        Grille existant = get(etablissementId, type, grilleId);
        if (existant.estFigee()) {
            throw TenantException.grilleFigee(grilleId);
        }
        grille.setId(grilleId);
        grille.setEtablissementId(etablissementId);
        grille.setVersion(existant.getVersion());
        grille.setActive(existant.isActive());
        grille.setModifiable(true);
        grille.setDateActivation(existant.getDateActivation());
        return persister(type, grille);
    }

    @Override
    public List<? extends Grille> list(UUID etablissementId, String type) {
        return switch (type) {
            case "stock" -> grilleRepository.listStocks(etablissementId);
            case "conformite" -> grilleRepository.listConformites(etablissementId);
            default -> grilleRepository.listNotations(etablissementId);
        };
    }

    @Override
    @Transactional
    public Grille activer(UUID etablissementId, String type, UUID grilleId) {
        Grille grille = get(etablissementId, type, grilleId);
        if (grille.estFigee()) {
            throw TenantException.grilleFigee(grilleId);
        }
        grille.activer();
        grilleRepository.desactiverAutresActives(etablissementId, typePersiste(type), grille.getCode(), grilleId);
        return persister(type, grille);
    }

    @Override
    @Transactional
    public Grille figer(UUID etablissementId, String type, UUID grilleId) {
        Grille grille = get(etablissementId, type, grilleId);
        if (grille.estFigee()) {
            throw TenantException.grilleFigee(grilleId);
        }
        grille.figer();
        return persister(type, grille);
    }

    @Override
    @Transactional
    public Grille dupliquer(UUID etablissementId, String type, UUID grilleId, String libelle) {
        Grille source = get(etablissementId, type, grilleId);
        Grille copie = switch (type) {
            case "stock" -> ((GrilleStock) source).nouvelleVersion();
            case "conformite" -> ((GrilleConformite) source).nouvelleVersion();
            default -> ((GrilleNotation) source).nouvelleVersion();
        };
        copie.setId(UUID.randomUUID());
        copie.setLibelle(libelle == null || libelle.isBlank() ? source.getLibelle() : libelle);
        copie.setEtablissementId(etablissementId);
        return persister(type, copie);
    }

    @Override
    public Grille applicable(UUID etablissementId, String type, ProfilEtablissement profil, UUID anneeId) {
        Optional<Grille> found = switch (type) {
            case "stock" -> grilleRepository.findStockApplicable(etablissementId, profil, anneeId)
                    .map(g -> g);
            case "conformite" -> grilleRepository.findConformiteApplicable(etablissementId, profil, anneeId)
                    .map(g -> g);
            default -> grilleRepository.findNotationApplicable(etablissementId, profil, anneeId)
                    .map(g -> g);
        };
        return found.orElseThrow(() -> TenantException.notFound(rubrique(type) + " applicable", null));
    }

    // ---------- ENFANTS GRILLE DE NOTATION ----------

    @Override
    @Transactional
    public GrilleNotation addBareme(UUID etablissementId, UUID grilleId, Bareme bareme) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        bareme.setIdBareme(UUID.randomUUID());
        bareme.setGrilleId(grilleId);
        grille.getBaremes().add(bareme);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    public List<Bareme> listBaremes(UUID etablissementId, UUID grilleId) {
        return getNotation(etablissementId, grilleId).getBaremes();
    }

    @Override
    @Transactional
    public GrilleNotation deleteBareme(UUID etablissementId, UUID grilleId, UUID baremeId) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.getBaremes().removeIf(b -> b.getIdBareme().equals(baremeId));
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation addReglePonderation(UUID etablissementId, UUID grilleId, ReglePonderation regle) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        regle.setIdRegle(UUID.randomUUID());
        regle.setGrilleId(grilleId);
        grille.getReglesPonderation().add(regle);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    public List<ReglePonderation> listReglesPonderation(UUID etablissementId, UUID grilleId) {
        return getNotation(etablissementId, grilleId).getReglesPonderation();
    }

    @Override
    @Transactional
    public GrilleNotation deleteReglePonderation(UUID etablissementId, UUID grilleId, UUID regleId) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.getReglesPonderation().removeIf(r -> r.getIdRegle().equals(regleId));
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation setConfigCascade(UUID etablissementId, UUID grilleId, ConfigCascade config) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.setConfigCascade(config);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    public ConfigCascade getConfigCascade(UUID etablissementId, UUID grilleId) {
        return getNotation(etablissementId, grilleId).getConfigCascade();
    }

    @Override
    @Transactional
    public GrilleNotation addSeuilDecision(UUID etablissementId, UUID grilleId, SeuilDecision seuil) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        seuil.setIdSeuil(UUID.randomUUID());
        seuil.setGrilleId(grilleId);
        grille.getSeuilsDecision().add(seuil);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation deleteSeuilDecision(UUID etablissementId, UUID grilleId, UUID seuilId) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.getSeuilsDecision().removeIf(s -> s.getIdSeuil().equals(seuilId));
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation addRegleAssiduite(UUID etablissementId, UUID grilleId, RegleAssiduite regle) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        regle.setIdRegle(UUID.randomUUID());
        regle.setGrilleId(grilleId);
        grille.getReglesAssiduite().add(regle);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation deleteRegleAssiduite(UUID etablissementId, UUID grilleId, UUID regleId) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.getReglesAssiduite().removeIf(r -> r.getIdRegle().equals(regleId));
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation addMention(UUID etablissementId, UUID grilleId, Mention mention) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        mention.setIdMention(UUID.randomUUID());
        mention.setGrilleId(grilleId);
        grille.getMentions().add(mention);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation deleteMention(UUID etablissementId, UUID grilleId, UUID mentionId) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.getMentions().removeIf(m -> m.getIdMention().equals(mentionId));
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation addAppreciation(UUID etablissementId, UUID grilleId, RegleAppreciation appreciation) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        appreciation.setIdRegle(UUID.randomUUID());
        appreciation.setGrilleId(grilleId);
        grille.getAppreciations().add(appreciation);
        return grilleRepository.saveNotation(grille);
    }

    @Override
    @Transactional
    public GrilleNotation deleteAppreciation(UUID etablissementId, UUID grilleId, UUID appreciationId) {
        GrilleNotation grille = getNotation(etablissementId, grilleId);
        verifierModifiable(grille);
        grille.getAppreciations().removeIf(a -> a.getIdRegle().equals(appreciationId));
        return grilleRepository.saveNotation(grille);
    }

    // ---------- ENFANTS GRILLE DE STOCK ----------

    @Override
    @Transactional
    public AlerteStock addAlerte(UUID etablissementId, UUID grilleId, AlerteStock alerte) {
        getStock(etablissementId, grilleId);
        if (alerte.getIdAlerte() == null) {
            alerte.setIdAlerte(UUID.randomUUID());
        }
        alerte.setGrilleId(grilleId);
        if (!alerte.isActive()) {
            alerte.declencher();
        }
        return grilleRepository.saveAlerte(alerte);
    }

    @Override
    public List<AlerteStock> listAlertes(UUID etablissementId, UUID grilleId) {
        getStock(etablissementId, grilleId);
        return grilleRepository.findAlertes(grilleId);
    }

    @Override
    @Transactional
    public AlerteStock acquitterAlerte(UUID etablissementId, UUID grilleId, UUID alerteId, UUID acquitteur) {
        getStock(etablissementId, grilleId);
        AlerteStock alerte = grilleRepository.findAlerte(grilleId, alerteId)
                .orElseThrow(() -> TenantException.notFound("AlerteStock", alerteId));
        alerte.acquitter(acquitteur);
        return grilleRepository.saveAlerte(alerte);
    }

    // ---------- HELPERS ----------

    private Grille trouver(UUID etablissementId, String type, UUID grilleId) {
        Grille grille = switch (type) {
            case "stock" -> grilleRepository.findStock(grilleId).orElse(null);
            case "conformite" -> grilleRepository.findConformite(grilleId).orElse(null);
            default -> grilleRepository.findNotation(grilleId).orElse(null);
        };
        if (grille == null || !etablissementId.equals(grille.getEtablissementId())) {
            throw TenantException.notFound(rubrique(type), grilleId);
        }
        return grille;
    }

    private Grille persister(String type, Grille grille) {
        return switch (type) {
            case "stock" -> grilleRepository.saveStock((GrilleStock) grille);
            case "conformite" -> grilleRepository.saveConformite((GrilleConformite) grille);
            default -> grilleRepository.saveNotation((GrilleNotation) grille);
        };
    }

    private GrilleNotation getNotation(UUID etablissementId, UUID grilleId) {
        return (GrilleNotation) trouver(etablissementId, "notation", grilleId);
    }

    private GrilleStock getStock(UUID etablissementId, UUID grilleId) {
        return (GrilleStock) trouver(etablissementId, "stock", grilleId);
    }

    private void verifierModifiable(Grille grille) {
        if (grille.estFigee()) {
            throw TenantException.grilleFigee(grille.getId());
        }
    }

    private String rubrique(String type) {
        return switch (type) {
            case "stock" -> "GrilleStock";
            case "conformite" -> "GrilleConformite";
            default -> "GrilleNotation";
        };
    }

    private String typePersiste(String type) {
        return switch (type) {
            case "stock" -> "STOCK";
            case "conformite" -> "CONFORMITE";
            default -> "NOTATION";
        };
    }
}