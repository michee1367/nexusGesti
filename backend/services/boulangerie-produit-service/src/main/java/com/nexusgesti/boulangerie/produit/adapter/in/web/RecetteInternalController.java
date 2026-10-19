package com.nexusgesti.boulangerie.produit.adapter.in.web;

import com.nexusgesti.boulangerie.produit.domain.model.BesoinMatiere;
import com.nexusgesti.boulangerie.produit.domain.model.FicheRecette;
import com.nexusgesti.boulangerie.produit.domain.port.in.ProduitUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/internal/recettes")
public class RecetteInternalController {

    private final ProduitUseCase produitUseCase;

    public RecetteInternalController(ProduitUseCase produitUseCase) {
        this.produitUseCase = produitUseCase;
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('SERVICE')")
    public FicheRecette getRecette(@PathVariable String code,
                                   @RequestParam(required = false) Integer version) {
        return produitUseCase.getRecetteByCode(code, version);
    }

    @GetMapping("/{code}/besoins")
    @PreAuthorize("hasRole('SERVICE')")
    public List<BesoinMatiere> getBesoins(@PathVariable String code,
                                          @RequestParam(required = false) Integer version,
                                          @RequestParam BigDecimal quantite) {
        return produitUseCase.getBesoinsByCode(code, version, quantite);
    }
}
