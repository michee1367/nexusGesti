"use client";

import { useMemo } from "react";
import { useApi } from "@/hooks/use-api";
import { anneeApi, assessmentApi, attendanceApi, boulangerieApi, etablissementApi, examApi, feesApi, peopleApi, structureApi } from "@/services/api";
import {
  anneeLabel,
  avisLabel,
  categorieLabel,
  classeLabel,
  coursLabel,
  creneauLabel,
  eleveLabel,
  enseignantLabel,
  evaluationLabel,
  gammeLabel,
  grilleLabel,
  matiereLabel,
  niveauLabel,
  parentLabel,
  periodeLabel,
  produitLabel,
  programmeLabel,
  promotionLabel,
  recetteLabel,
  reductionLabel,
  salleLabel,
  seanceLabel,
  semestreLabel,
  sessionLabel,
  siteLabel,
  ueLabel,
} from "@/components/pedagogie/labels";

export function useLookup<T>(
  fetcher: () => Promise<T[]>,
  keyOf: (row: T) => string,
  labelOf: (row: T) => string,
  deps: unknown[] = [],
) {
  const { data } = useApi(fetcher, deps);
  return useMemo(() => {
    const map = new Map<string, string>();
    for (const row of data ?? []) map.set(keyOf(row), labelOf(row));
    return map;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [data]);
}

export const useElevesMap = (deps: unknown[] = []) =>
  useLookup(() => peopleApi.eleves.list(), (eleve) => eleve.idEleve, eleveLabel, deps);

export const useEnseignantsMap = (deps: unknown[] = []) =>
  useLookup(() => peopleApi.enseignants.list(), (enseignant) => enseignant.id, enseignantLabel, deps);

export const useParentsMap = (deps: unknown[] = []) =>
  useLookup(() => peopleApi.parents.list(), (parent) => parent.id, parentLabel, deps);

export const useClassesMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.classes.list(), (classe) => classe.idClasse, classeLabel, deps);

export const useUesMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.ues.list(), (ue) => ue.idUE, ueLabel, deps);

export const useMatieresMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.matieres.list(), (matiere) => matiere.idMatiere, matiereLabel, deps);

export const useCoursMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.cours.list(), (cours) => cours.idCours, coursLabel, deps);

export const useSallesMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.salles.list(), (salle) => salle.idSalle, salleLabel, deps);

export const useCreneauxMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.creneaux.list(), (creneau) => creneau.idCreneau, creneauLabel, deps);

export const useSeancesMap = (deps: unknown[] = []) =>
  useLookup(() => attendanceApi.seances.list(), (seance) => seance.idSeance, seanceLabel, deps);

export const useNiveauxMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.niveaux.list(), (niveau) => niveau.idNiveau, niveauLabel, deps);

export const useSemestresMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.semestres.list(), (semestre) => semestre.idSemestre, semestreLabel, deps);

export const usePromotionsMap = (deps: unknown[] = []) =>
  useLookup(() => structureApi.promotions.list(), (promotion) => promotion.idPromotion, promotionLabel, deps);

export const useProgrammesMap = (deps: unknown[] = []) =>
  useLookup(() => etablissementApi.programmes.list(), (programme) => programme.idProgramme, programmeLabel, deps);

export const useSitesMap = (deps: unknown[] = []) =>
  useLookup(() => etablissementApi.sites.list(), (site) => site.idSite, siteLabel, deps);

export const useAnneesMap = (deps: unknown[] = []) =>
  useLookup(() => anneeApi.list(), (annee) => annee.idAnnee, anneeLabel, deps);

export const useSessionsMap = (deps: unknown[] = []) =>
  useLookup(() => examApi.sessions.list(), (session) => session.idSession, sessionLabel, deps);

export const useGrillesTarifairesMap = (deps: unknown[] = []) =>
  useLookup(() => feesApi.grilles.list(), (grille) => grille.id, grilleLabel, deps);

export function useReductionsMap(deps: unknown[] = []) {
  const { data } = useApi(() => feesApi.grilles.list(), deps);
  return useMemo(() => {
    const map = new Map<string, string>();
    for (const grille of data ?? []) {
      for (const reduction of grille.reductions ?? []) {
        if (reduction.id) map.set(reduction.id, reductionLabel(reduction));
      }
    }
    return map;
  }, [data]);
}

export const useEvaluationsMap = (deps: unknown[] = []) =>
  useLookup(() => assessmentApi.evaluations.list(), (evaluation) => evaluation.idEvaluation, evaluationLabel, deps);

export const useAvisMap = (deps: unknown[] = []) =>
  useLookup(() => feesApi.avis.list(), (avis) => avis.idAvis, avisLabel, deps);

export function usePeriodesMap(deps: unknown[] = []) {
  const { data } = useApi(() => anneeApi.list(), deps);
  return useMemo(() => {
    const map = new Map<string, string>();
    for (const annee of data ?? []) {
      for (const periode of annee.periodes ?? []) map.set(periode.idPeriode, periodeLabel(periode));
    }
    return map;
  }, [data]);
}

export const useGammesMap = (deps: unknown[] = []) =>
  useLookup(() => boulangerieApi.gammes.list(), (gamme) => gamme.code, gammeLabel, deps);

export const useCategoriesMap = (deps: unknown[] = []) =>
  useLookup(() => boulangerieApi.categories.list(), (categorie) => categorie.code, categorieLabel, deps);

export const useProduitsMap = (deps: unknown[] = []) =>
  useLookup(() => boulangerieApi.produits.list(), (produit) => produit.code, produitLabel, deps);

export const useRecettesMap = (deps: unknown[] = []) =>
  useLookup(() => boulangerieApi.recettes.list(), (recette) => recette.idRecette ?? recette.code, recetteLabel, deps);

export const useRecettesCodeMap = (deps: unknown[] = []) =>
  useLookup(() => boulangerieApi.recettes.list(), (recette) => recette.code, recetteLabel, deps);
