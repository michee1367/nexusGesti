import type {
  AnneeScolaire,
  AvisPaiement,
  Classe,
  CoursECUE,
  Creneau,
  Eleve,
  Enseignant,
  Epreuve,
  Evaluation,
  Gamme,
  CategorieProduit,
  GrilleTarifaire,
  Matiere,
  Niveau,
  Parent,
  Periode,
  Produit,
  Programme,
  Promotion,
  Reduction,
  Salle,
  Seance,
  Semestre,
  SessionExamen,
  Site,
  UE,
  FicheRecette,
  IngredientRecette,
  PlanProduction,
  Fournee,
  FournisseurBoulangerie,
  CommandeAchat,
  Reception,
} from "@/types";
import { formatDate, formatTime } from "@/components/pedagogie/format";

export interface SelectOption {
  value: string;
  label: string;
}

const part = (value?: string | null) => (value ? ` ${value}` : "");

export const eleveLabel = (eleve: Eleve) =>
  `${eleve.prenom}${part(eleve.postNom)} ${eleve.nom} (${eleve.matricule})`;

export const enseignantLabel = (enseignant: Enseignant) =>
  `${enseignant.prenom}${part(enseignant.postNom)} ${enseignant.nom}`;

export const parentLabel = (parent: Parent) =>
  `${parent.prenom}${part(parent.postNom)} ${parent.nom}`;

export const classeLabel = (classe: Classe) => `${classe.code} — ${classe.libelle}`;

export const ueLabel = (ue: UE) => `${ue.code} — ${ue.libelle}`;

export const codeLabel = (value: { code: string; libelle?: string | null }) =>
  value.libelle ? `${value.code} — ${value.libelle}` : value.code;

export const matiereLabel = (matiere: Matiere) => codeLabel(matiere);
export const coursLabel = (cours: CoursECUE) => codeLabel(cours);
export const salleLabel = (salle: Salle) => codeLabel(salle);
export const niveauLabel = (niveau: Niveau) => codeLabel(niveau);
export const promotionLabel = (promotion: Promotion) => codeLabel(promotion);
export const semestreLabel = (semestre: Semestre) => codeLabel(semestre);
export const anneeLabel = (annee: AnneeScolaire) => codeLabel(annee);
export const siteLabel = (site: Site) => codeLabel(site);
export const programmeLabel = (programme: Programme) => codeLabel(programme);
export const periodeLabel = (periode: Periode) => `${periode.code} — ${periode.libelle}`;

export const creneauLabel = (creneau: Creneau) =>
  `${creneau.jour} ${formatTime(creneau.heureDebut)}-${formatTime(creneau.heureFin)}`;

export const seanceLabel = (seance: Seance) =>
  `Séance ${formatDate(seance.date)}${seance.heureDebut ? ` ${formatTime(seance.heureDebut)}` : ""}`;

export const grilleLabel = (grille: GrilleTarifaire) => codeLabel(grille);
export const reductionLabel = (reduction: Reduction) => codeLabel(reduction);
export const sessionLabel = (session: SessionExamen) => session.libelle ? `${session.reference} — ${session.libelle}` : session.reference;
export const epreuveLabel = (epreuve: Epreuve) => (epreuve.libelle ? `${epreuve.reference} — ${epreuve.libelle}` : epreuve.reference);
export const evaluationLabel = (evaluation: Evaluation) => evaluation.reference ?? evaluation.idEvaluation;
export const avisLabel = (avis: AvisPaiement) => avis.reference;

export const gammeLabel = (gamme: Gamme) => codeLabel(gamme);
export const categorieLabel = (categorie: CategorieProduit) => codeLabel(categorie);
export const produitLabel = (produit: Produit) =>
  produit.designation ? `${produit.code} — ${produit.designation}` : produit.code;
export const recetteLabel = (recette: FicheRecette) =>
  `${recette.code} (v${recette.version ?? 1})${recette.libelle ? ` — ${recette.libelle}` : ""}`;
export const ingredientLabel = (ingredient: IngredientRecette) =>
  ingredient.libelle ? `${ingredient.articleCode} — ${ingredient.libelle}` : ingredient.articleCode;

export const planLabel = (plan: PlanProduction) =>
  `${formatDate(plan.date)}${plan.atelierCode ? ` — ${plan.atelierCode}` : ""}`;

export const fourneeLabel = (fournee: Fournee) =>
  `${fournee.numero ?? fournee.idFournee}${fournee.produitCode ? ` — ${fournee.produitCode}` : ""}`;

export const fournisseurLabel = (fournisseur: FournisseurBoulangerie) =>
  fournisseur.raisonSociale ? `${fournisseur.code} — ${fournisseur.raisonSociale}` : fournisseur.code;

export const commandeLabel = (commande: CommandeAchat) =>
  `${commande.numero ?? commande.idCommande}${commande.fournisseurTiersCode ? ` — ${commande.fournisseurTiersCode}` : ""}`;

export const receptionLabel = (reception: Reception) =>
  `${reception.numero ?? reception.idReception}${reception.commandeNumero ? ` — ${reception.commandeNumero}` : ""}`;

export function toSelectOptions<T>(
  rows: T[] | null | undefined,
  keyOf: (row: T) => string,
  labelOf: (row: T) => string,
): SelectOption[] {
  return (rows ?? []).map((row) => ({ value: keyOf(row), label: labelOf(row) }));
}

export const lookupLabel = (
  map: Map<string, string>,
  id: string | null | undefined,
  fallback = "—",
): string => {
  if (!id) return fallback;
  return map.get(id) ?? `${id.slice(0, 8)}…`;
};
