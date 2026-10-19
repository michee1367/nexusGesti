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

export const PROFILS = ["PRIMAIRE", "SECONDAIRE", "UNIVERSITAIRE", "BOULANGERIE"] as const;

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

// ---- 4c enrollment ----
export const STATUT_INSCRIPTION_UE = [
  "EN_ATTENTE",
  "INSCRIT",
  "EN_LISTE_ATTENTE",
  "VALIDE",
  "REFUSE",
  "RATTAPE",
  "ANNULE",
  "TRANSFERE",
] as const;
export const STATUT_GROUPE = ["OUVERT", "COMPLET", "CLOS", "ARCHIVE"] as const;
export const TYPE_GROUPE = ["TD", "TP", "SEMINAIRE", "ATELIER", "EXERCICE", "PROJET"] as const;
export const DECISION_INSCRIPTION = ["ELIGIBLE", "INELIGIBLE", "LISTE_ATTENTE", "OBLIGATOIRE"] as const;

// ---- 4d attendance ----
export const TYPE_SEANCE = ["COURS", "TD", "TP", "EXAMEN", "RATTRAPAGE", "VISITE", "SORTIE"] as const;
export const STATUT_SEANCE = ["PLANIFIEE", "EN_COURS", "TERMINEE", "ANNULEE", "REPORTEE"] as const;
export const STATUT_PRESENCE = [
  "PRESENT",
  "ABSENT",
  "RETARD",
  "ABSENT_EXCUSE",
  "ABSENT_NON_EXCUSE",
  "CONGE",
  "MALADE",
  "SORTIE_AUTORISEE",
] as const;
export const TYPE_ABSENCE = [
  "INJUSTIFIEE",
  "JUSTIFIEE",
  "MEDICALE",
  "FAMILIALE",
  "SCOLAIRE",
  "DISCIPLINAIRE",
  "RELIGIEUSE",
  "AUTRE",
] as const;
export const TYPE_ABSENCE_JUSTIF = ["MEDICALE", "FAMILIALE", "SCOLAIRE", "DISCIPLINAIRE", "RELIGIEUSE"] as const;
export const STATUT_FEUILLE = ["BROUILLON", "SOUMISE", "VALIDEE", "CORRIGEE", "CLOTUREE"] as const;
export const METHODE_SAISIE = ["MANUELLE", "CODE_BARRE", "BIOMETRIE", "VOCAL", "IMPORT", "AUTOMATIQUE"] as const;
export const STATUT_JUSTIFICATIF = ["EN_ATTENTE", "ACCEPTE", "REJETE", "EXPIRE"] as const;

// ---- 4e assessment ----
export const TYPE_EVALUATION_ELARGI = [
  "INTERROGATION",
  "TP",
  "TD",
  "PROJET",
  "DEVOIR",
  "COMPOSITION",
  "ORAL",
  "EXAMEN_BLANC",
  "CONCOURS",
  "DEVOIR_SURVEILLE",
] as const;
export const STATUT_EVALUATION = ["BROUILLON", "PLANIFIEE", "OUVERTE", "CLOTUREE", "PUBLIEE", "ANNULEE"] as const;
export const TYPE_BULLETIN = ["TRIMESTRIEL", "SEMESTRIEL", "ANNUEL", "PROVISOIRE", "RATTRAPAGE", "CERTIFICAT"] as const;
export const STATUT_BULLETIN = ["BROUILLON", "CALCULE", "PUBLIE", "GENERE", "SIGNE", "RECTIFIE", "ANNULE"] as const;
export const STATUT_NOTE = ["PROVISOIRE", "DEFINITIVE", "CONTESTEE", "ANNULEE", "EXCLUE"] as const;
export const SOURCE_NOTE = ["EVALUATION_CONTINUE", "SESSION_EXAMEN", "RATTRAPAGE", "CONTROLE_CONTINU", "EXCEPTION"] as const;
export const STATUT_COUPON_NOTES = [
  "A_TIRER",
  "TIRE",
  "DETACHE",
  "SIGNE_PAR_TUTEUR",
  "RENDU",
  "NON_RENDU",
  "PERDU",
  "INVALIDE",
] as const;

// ---- 4f exam ----
export const TYPE_SESSION = [
  "SESSION_TRIMESTRIELLE",
  "SESSION_SEMESTRIELLE",
  "SESSION_RATTRAPAGE",
  "RESESSION_ANNUELLE",
  "EXAMEN_BLANC",
  "SESSION_EXTRAORDINAIRE",
] as const;
export const STATUT_SESSION = [
  "BROUILLON",
  "PROGRAMMEE",
  "EN_COURS",
  "CORRECTION",
  "DELIBERATION",
  "CLOTUREE",
  "ANNULEE",
] as const;
export const TYPE_EPREUVE = ["ECRIT", "ORAL", "QCM", "PRATIQUE", "TP_NOTE", "DISSERTATION", "PROBLEME", "MIXTE"] as const;
export const STATUT_EPREUVE = ["PROJETEE", "VALIDEE", "PROGRAMMEE", "TENUE", "ANNULEE"] as const;
export const STATUT_CONVOCATION = ["EMISE", "NOTIFIEE", "PRESENTE", "ANNULEE"] as const;
export const STATUT_NOTE_EXAMEN = ["PROVISOIRE", "DEFINITIVE", "CONTESTEE", "ANNULEE", "RATTRAPAGE"] as const;
export const STATUT_CORRECTION = ["OUVERTE", "EN_COURS", "CLOTUREE", "REPRISE", "HOMOLOGUEE"] as const;
export const STATUT_DELIBERATION = ["PLANIFIEE", "EN_COURS", "CLOTUREE", "REPRISE"] as const;
export const DECISION_TYPE = ["ADMIS", "ADMIS_MENTION", "RATTAPAGE", "REDOUBLANT", "REJETE"] as const;
export const STATUT_PV = ["BROUILLON", "SOUMIS", "SIGNE", "CONTESTE", "HOMOLOGUE"] as const;
export const TYPE_CRENEAU_EXAM = ["MATIN", "APRES_MIDI", "SOIR", "MATIN_APRES_MIDI"] as const;

// ---- 4g fees ----
export const STATUT_GRILLE = ["BROUILLON", "ACTIVE", "ARCHIVEE"] as const;
export const TYPE_REDUCTION = ["POURCENTAGE", "MONTANT_FIXE", "TRANCHE", "TARIF_REDUIT"] as const;
export const STATUT_AVIS = [
  "BROUILLON",
  "EMIS",
  "PARTIELLEMENT_REGLE",
  "REGLE",
  "ANNULE",
  "EN_RETARD",
  "TRANSFERE",
] as const;
export const STATUT_ECHEANCE = [
  "A_VENIR",
  "ECHUE",
  "REGLEE",
  "PARTIELLEMENT_REGLEE",
  "ANNULEE",
  "REPORTEE",
] as const;
export const STATUT_PAIEMENT = ["EN_ATTENTE", "VALIDE", "REJETE", "ANNULE", "REMBOURSE", "A_VERIFIER"] as const;
export const MOYEN_PAIEMENT = [
  "ESPECES",
  "MOBILE_MONEY",
  "VIREMENT",
  "CHEQUE",
  "CARTE",
  "PRELEVEMENT",
  "COMPENSATION",
  "DEVIS_ELEVE",
] as const;
export const STATUT_COUPON = ["A_TIRER", "TIRE", "REMIS", "ARCHIVE", "ANNULE"] as const;
export const TYPE_FRAIS = [
  "INSCRIPTION",
  "SCOLARITE",
  "CANTINE",
  "TRANSPORT",
  "FOURNITURES",
  "EXAMEN",
  "TENUE",
  "GARDERIE",
  "EXCURSION",
  "LABORATOIRE",
  "BIBLIOTHEQUE",
  "DIPLOME",
  "RATTRAPAGE",
  "AUTRE",
] as const;
export const CATEGORIE_FRAIS = ["OBLIGATOIRE", "OPTIONNEL", "FACULTATIF", "SANCTION"] as const;
export const TYPE_BENEFICIAIRE = ["ELEVE", "TIERS_PAYEUR", "EMPLOYEUR", "ANCIENNE", "AUTRE"] as const;
export const PERIODICITE = ["PONCTUELLE", "MENSUELLE", "TRIMESTRIELLE", "SEMESTRIELLE", "ANNUELLE"] as const;

// ---- boulangerie lot 1 (produit) ----
export const UNITE_VENTE = ["PIECE", "LOT", "KG", "GRAMME", "TRANCHE", "SACHET", "PLATEAU"] as const;
export const ALLERGENE = [
  "GLUTEN",
  "LACTOSE",
  "OEUF",
  "ARACHIDE",
  "SOJA",
  "NOIX",
  "POISSON",
  "CRUSTACE",
  "SESAME",
  "SULFITE",
] as const;
export const NATURE_COUT = [
  "MATIERE",
  "MAIN_OEUVRE",
  "ENERGIE",
  "EMBALLAGE",
  "AMORTISSEMENT",
  "AUTRE",
] as const;

// ---- boulangerie lot 2 (production) ----
export const STATUT_PLAN = ["BROUILLON", "VALIDE", "LANCE", "CLOTURE"] as const;
export const STATUT_FOURNEE = [
  "PLANIFIEE",
  "EN_COURS",
  "CUITE",
  "VALIDEE",
  "ANNULEE",
] as const;
export const TYPE_PERTE = [
  "PATE_PERDUE",
  "CUISSON_RATEE",
  "CASSE",
  "INVENDU_DESTRUCTION",
  "DEMARQUE",
  "ECHANTILLON",
] as const;
