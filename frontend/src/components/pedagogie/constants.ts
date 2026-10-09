export const enumOptions = (values: readonly string[]) =>
  values.map((value) => ({ value, label: value.replace(/_/g, " ") }));

export function toOptions<T>(
  rows: T[] | null | undefined,
  valueKey: keyof T,
  labelKey: keyof T,
): { value: string; label: string }[] {
  return (rows ?? []).map((row) => ({
    value: String(row[valueKey]),
    label: String(row[labelKey]),
  }));
}

export const PROFILS = ["PRIMAIRE", "SECONDAIRE", "UNIVERSITAIRE"] as const;

export const TYPE_CYCLE = ["PRIMAIRE", "SECONDAIRE", "SUPERIEUR", "LICENCE", "MASTER"] as const;
export const TYPE_MATIERE = ["OBLIGATOIRE", "OPTION", "CHOIX", "LIBRE"] as const;
export const TYPE_EVALUATION = [
  "INTERROGATION",
  "TP",
  "TD",
  "PROJET",
  "DEVOIR",
  "COMPOSITION",
  "ORAL",
  "EXAMEN_BLANC",
] as const;
export const TYPE_SALLE = [
  "COURS",
  "TP",
  "LABORATOIRE",
  "EXAMEN",
  "AMPHITHEATRE",
  "BUREAU",
  "DIRECTION",
  "MEDICALE",
  "CHAMBRE",
  "BLOC_OPERATOIRE",
] as const;
export const JOURS = ["LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"] as const;
export const TYPE_CONFLIT = [
  "SALL_DOUBLEE",
  "ENSEIGNANT_DOUBLE",
  "ELEVES_DOUBLE",
  "HORS_CYCLE",
  "COINCIDENCE_COURS_EXAMEN",
] as const;
export const STATUT_CLASSE = ["OUVERTE", "EN_COURS", "CLOTUREE"] as const;

export const SEXE = ["M", "F", "AUTRE"] as const;
export const STATUT_ELEVE = [
  "PREINSCRIT",
  "INSCRIT",
  "REINSCRIT",
  "TRANSFERE",
  "ABANDON",
  "CONGE",
  "OBTEN_DIPLOME",
  "RADIE",
] as const;
export const STATUT_DOSSIER = ["EN_COURS", "COMPLET", "INCOMPLET", "VERIFIE"] as const;
export const STATUT_INSCRIPTION = ["EN_ATTENTE", "VALIDE", "ANNULEE"] as const;
export const TYPE_CONGE = ["MEDICAL", "FAMILIAL", "SCOLAIRE", "FINANCIER", "DISCIPLINAIRE"] as const;
export const TYPE_SANCTION = [
  "AVERTISSEMENT",
  "BLAME",
  "EXCLUSION_TEMPORAIRE",
  "EXCLUSION_DEFINITIVE",
] as const;
export const TYPE_LIEN = [
  "PERE",
  "MERE",
  "TUTEUR",
  "TUTEUR_LEGAL",
  "GRAND_PARENT",
  "FRERE",
  "SOEUR",
  "TUTEUR_AFFECTATIF",
] as const;
export const TYPE_AFFECTION = ["TITULAIRE", "CO_TITULAIRE", "ADJOINT", "REMPLACANT"] as const;
export const TYPE_SERVICE = [
  "DIRECTION",
  "PEDAGOGIE",
  "SCOLARITE",
  "RESSOURCES_HUMAINES",
  "FINANCIER",
  "INFORMATIQUE",
  "LABORATOIRE",
  "BIBLIOTHEQUE",
  "MEDECINE_SCOLAIRE",
  "HYGIENE",
  "SECURITE",
] as const;
export const CATEGORIE_PIECE = [
  "PHOTO_IDENTITE",
  "ACTE_NAISSANCE",
  "BULLETIN_ANTERIEUR",
  "CERTIFICAT_SCOLARITE",
] as const;
export const STATUT_ENSEIGNANT = ["ACTIF", "CONGE", "DETACHE", "RETRAITE", "INACTIF"] as const;
export const FONCTION = ["TITULAIRE", "REMPLACANT", "VACATAIRE", "CONTRACTUEL", "STAGIAIRE", "CHERCHEUR"] as const;
