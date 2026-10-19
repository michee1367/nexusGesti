package com.nexusgesti.boulangerie.produit.adapter.in.web;

import com.nexusgesti.boulangerie.produit.domain.model.*;
import com.nexusgesti.boulangerie.produit.domain.port.in.ProduitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProduitController {

    private final ProduitUseCase produitUseCase;

    public ProduitController(ProduitUseCase produitUseCase) {
        this.produitUseCase = produitUseCase;
    }

    // ---------- GAMMES ----------

    @GetMapping("/gammes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Gamme> listGammes() {
        return produitUseCase.listGammes();
    }

    @PostMapping("/gammes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Gamme> creerGamme(@RequestBody Gamme gamme) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitUseCase.creerGamme(gamme));
    }

    // ---------- CATEGORIES ----------

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CategorieProduit> listCategories() {
        return produitUseCase.listCategories();
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategorieProduit> creerCategorie(@RequestBody CategorieProduit categorie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitUseCase.creerCategorie(categorie));
    }

    // ---------- PRODUITS ----------

    @GetMapping("/produits")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Produit> listProduits(@RequestParam(required = false) String gammeCode,
                                      @RequestParam(required = false) String categorieCode) {
        return produitUseCase.listProduits(gammeCode, categorieCode);
    }

    @PostMapping("/produits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produit> creerProduit(@RequestBody Produit produit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitUseCase.creerProduit(produit));
    }

    @GetMapping("/produits/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Produit getProduit(@PathVariable UUID id) {
        return produitUseCase.getProduit(id);
    }

    @PostMapping("/produits/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public Produit activerProduit(@PathVariable UUID id) {
        return produitUseCase.activerProduit(id);
    }

    @PostMapping("/produits/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    public Produit desactiverProduit(@PathVariable UUID id) {
        return produitUseCase.desactiverProduit(id);
    }

    // ---------- FICHES RECETTE ----------

    @GetMapping("/recettes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<FicheRecette> listRecettes(@RequestParam(required = false) String produitCode) {
        return produitUseCase.listRecettes(produitCode);
    }

    @PostMapping("/recettes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FicheRecette> creerRecette(@RequestBody FicheRecette recette) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitUseCase.creerRecette(recette));
    }

    @GetMapping("/recettes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public FicheRecette getRecette(@PathVariable UUID id) {
        return produitUseCase.getRecette(id);
    }

    @PostMapping("/recettes/{id}/ingredients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FicheRecette> ajouterIngredient(@PathVariable UUID id,
                                                          @RequestBody IngredientRecette ingredient) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produitUseCase.ajouterIngredient(id, ingredient));
    }

    @PostMapping("/recettes/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public FicheRecette activerRecette(@PathVariable UUID id) {
        return produitUseCase.activerRecette(id);
    }

    @PostMapping("/recettes/{id}/figer")
    @PreAuthorize("hasRole('ADMIN')")
    public FicheRecette figerRecette(@PathVariable UUID id) {
        return produitUseCase.figerRecette(id);
    }

    @GetMapping("/recettes/{id}/besoins")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<BesoinMatiere> getBesoins(@PathVariable UUID id, @RequestParam BigDecimal quantite) {
        return produitUseCase.getBesoins(id, quantite);
    }

    @GetMapping("/recettes/{id}/cout")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public CoutRecette getCout(@PathVariable UUID id,
                               @RequestParam(required = false, value = "prix") List<String> prix) {
        return produitUseCase.getCout(id, parsePrix(prix));
    }

    private Map<String, BigDecimal> parsePrix(List<String> params) {
        Map<String, BigDecimal> prixMoyens = new HashMap<>();
        if (params == null) {
            return prixMoyens;
        }
        for (String param : params) {
            if (param == null || !param.contains(":")) {
                continue;
            }
            String[] parts = param.split(":", 2);
            try {
                prixMoyens.put(parts[0].trim(), new BigDecimal(parts[1].trim()));
            } catch (NumberFormatException ignored) {
                // un parametre de prix mal forme est ignore
            }
        }
        return prixMoyens;
    }
}
