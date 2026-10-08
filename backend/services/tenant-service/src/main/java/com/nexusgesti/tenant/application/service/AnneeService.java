package com.nexusgesti.tenant.application.service;

import com.nexusgesti.tenant.domain.TenantException;
import com.nexusgesti.tenant.domain.model.AnneeScolaire;
import com.nexusgesti.tenant.domain.model.Periode;
import com.nexusgesti.tenant.domain.model.StatutAnnee;
import com.nexusgesti.tenant.domain.port.in.AnneeUseCase;
import com.nexusgesti.tenant.domain.port.out.AnneeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AnneeService implements AnneeUseCase {

    private final AnneeRepository anneeRepository;

    public AnneeService(AnneeRepository anneeRepository) {
        this.anneeRepository = anneeRepository;
    }

    @Override
    @Transactional
    public AnneeScolaire creer(UUID etablissementId, AnneeScolaire annee) {
        annee.setIdAnnee(UUID.randomUUID());
        annee.setEtablissementId(etablissementId);
        if (annee.getStatut() == null) {
            annee.setStatut(StatutAnnee.PREPARATION);
        }
        return anneeRepository.save(annee);
    }

    @Override
    public AnneeScolaire get(UUID etablissementId, UUID anneeId) {
        AnneeScolaire annee = anneeRepository.findById(anneeId)
                .orElseThrow(() -> TenantException.notFound("AnneeScolaire", anneeId));
        if (!etablissementId.equals(annee.getEtablissementId())) {
            throw TenantException.notFound("AnneeScolaire", anneeId);
        }
        return annee;
    }

    @Override
    public AnneeScolaire update(UUID etablissementId, UUID anneeId, AnneeScolaire annee) {
        get(etablissementId, anneeId);
        annee.setIdAnnee(anneeId);
        annee.setEtablissementId(etablissementId);
        return anneeRepository.save(annee);
    }

    @Override
    public List<AnneeScolaire> list(UUID etablissementId) {
        return anneeRepository.findByEtablissement(etablissementId);
    }

    @Override
    @Transactional
    public AnneeScolaire ouvrir(UUID etablissementId, UUID anneeId) {
        AnneeScolaire annee = get(etablissementId, anneeId);
        annee.setPeriodes(anneeRepository.findPeriodes(anneeId));
        annee.ouvrir();
        return anneeRepository.save(annee);
    }

    @Override
    @Transactional
    public AnneeScolaire cloturer(UUID etablissementId, UUID anneeId, Date date) {
        AnneeScolaire annee = get(etablissementId, anneeId);
        annee.setPeriodes(anneeRepository.findPeriodes(anneeId));
        annee.cloturer(date);
        return anneeRepository.save(annee);
    }

    @Override
    @Transactional
    public Periode addPeriode(UUID etablissementId, UUID anneeId, Periode periode) {
        get(etablissementId, anneeId);
        periode.setIdPeriode(UUID.randomUUID());
        periode.setAnneeId(anneeId);
        return anneeRepository.savePeriode(periode);
    }

    @Override
    public List<Periode> listPeriodes(UUID etablissementId, UUID anneeId) {
        get(etablissementId, anneeId);
        return anneeRepository.findPeriodes(anneeId);
    }

    @Override
    @Transactional
    public Periode cloturerPeriode(UUID etablissementId, UUID anneeId, UUID periodeId) {
        get(etablissementId, anneeId);
        Periode periode = anneeRepository.findPeriode(anneeId, periodeId)
                .orElseThrow(() -> TenantException.notFound("Periode", periodeId));
        periode.cloturer();
        return anneeRepository.savePeriode(periode);
    }

    @Override
    public AnneeScolaire courante(UUID etablissementId) {
        return anneeRepository.findByEtablissement(etablissementId).stream()
                .filter(AnneeScolaire::estCourante)
                .findFirst()
                .orElseThrow(() -> TenantException.notFound("AnneeScolaire", null));
    }
}