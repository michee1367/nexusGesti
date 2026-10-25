CREATE SCHEMA IF NOT EXISTS pedagogie;


-- ============================================================
-- AGREGAT : ARBRE PEDAGOGIQUE (niveau > classe)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.niveau (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    cycle VARCHAR(20) NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    niveau_superieur_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.classe (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    niveau_id UUID REFERENCES pedagogie.niveau (id),
    annee_id UUID,
    site_id UUID,
    capacite INTEGER,
    effectif_reel INTEGER NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    salle_attitree_id UUID,
    regime_horaire_code VARCHAR(30),
    PRIMARY KEY (id)
);
CREATE INDEX idx_classe_niveau ON pedagogie.classe (niveau_id);

-- ============================================================
-- AGREGAT : PROMOTION (universite)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.promotion (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    programme_id UUID,
    semestre_id UUID,
    annee_id UUID,
    capacite INTEGER,
    effectif_reel INTEGER NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================
-- UNIVERSITE : PROGRAMME > SEMESTRE > UE > COURS
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.semestre (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    periode_id UUID,
    total_credits_attendu NUMERIC(8,2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.ue (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    semestre_id UUID REFERENCES pedagogie.semestre (id),
    credits NUMERIC(8,2),
    coefficient NUMERIC(6,2),
    volume_horaire NUMERIC(8,2),
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    niveau_acalcul VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX idx_ue_semestre ON pedagogie.ue (semestre_id);

CREATE TABLE IF NOT EXISTS pedagogie.matiere (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    discipline VARCHAR(120),
    type VARCHAR(20) NOT NULL,
    couleur VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.cours_ecue (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    ue_id UUID REFERENCES pedagogie.ue (id),
    classe_id UUID REFERENCES pedagogie.classe (id),
    matiere_id UUID REFERENCES pedagogie.matiere (id),
    type VARCHAR(20) NOT NULL,
    volume_horaire NUMERIC(8,2),
    credits NUMERIC(8,2),
    coefficient NUMERIC(6,2),
    hebdomadaire BOOLEAN NOT NULL DEFAULT FALSE,
    regroupement VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX idx_cours_ue ON pedagogie.cours_ecue (ue_id);
CREATE INDEX idx_cours_classe ON pedagogie.cours_ecue (classe_id);

-- ============================================================
-- SALLES ET EMPLOI DU TEMPS
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.salle (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(30) NOT NULL,
    site_id UUID,
    capacite INTEGER NOT NULL DEFAULT 0,
    equipements JSONB DEFAULT '[]',
    accessible BOOLEAN NOT NULL DEFAULT FALSE,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.creneau (
    id UUID NOT NULL,
    jour VARCHAR(20) NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.regime_horaire (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    jours JSONB NOT NULL DEFAULT '[]',
    creneaux_par_jour INTEGER NOT NULL,
    duree_creneau INTEGER NOT NULL,
    pause_matinale INTEGER,
    pause_meridienne INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.emploi_du_temps (
    id UUID NOT NULL,
    annee_id UUID,
    classe_id UUID,
    version INTEGER NOT NULL DEFAULT 1,
    date_activation TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    publie BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_emploi_classe ON pedagogie.emploi_du_temps (classe_id);

CREATE TABLE IF NOT EXISTS pedagogie.plage_emploi (
    id UUID NOT NULL,
    emploi_id UUID NOT NULL REFERENCES pedagogie.emploi_du_temps (id),
    cours_id UUID,
    creneau_id UUID REFERENCES pedagogie.creneau (id),
    salle_id UUID REFERENCES pedagogie.salle (id),
    enseignant_id UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_plage_emploi ON pedagogie.plage_emploi (emploi_id);

CREATE TABLE IF NOT EXISTS pedagogie.conflit_emploi (
    id UUID NOT NULL,
    emploi_id UUID NOT NULL REFERENCES pedagogie.emploi_du_temps (id),
    type VARCHAR(30) NOT NULL,
    plage_a_id UUID,
    plage_b_id UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_conflit_emploi ON pedagogie.conflit_emploi (emploi_id);


-- ============================================================
-- AGREGAT : ELEVE > DOSSIER > PIECES
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.eleve (
    id UUID NOT NULL,
    matricule VARCHAR(30) NOT NULL,
    nom VARCHAR(120) NOT NULL,
    post_nom VARCHAR(120),
    prenom VARCHAR(120) NOT NULL,
    sexe VARCHAR(20) NOT NULL,
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(120),
    nationalite VARCHAR(60),
    adresse_rue VARCHAR(160),
    adresse_quartier VARCHAR(120),
    adresse_commune VARCHAR(120),
    adresse_ville VARCHAR(120),
    adresse_province VARCHAR(120),
    adresse_pays VARCHAR(120),
    adresse_telephone VARCHAR(40),
    photo VARCHAR(500),
    statut VARCHAR(20) NOT NULL,
    date_inscription DATE NOT NULL,
    date_sortie DATE,
    etablissement_origine VARCHAR(160),
    etablissement_destination VARCHAR(160),
    motif_transfert VARCHAR(255),
    statut_dossier VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.dossier_eleve (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES pedagogie.eleve (id),
    reference VARCHAR(60) NOT NULL,
    date_ouverture DATE NOT NULL,
    statut VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_dossier_eleve ON pedagogie.dossier_eleve (eleve_id);

CREATE TABLE IF NOT EXISTS pedagogie.fichier_joint (
    id UUID NOT NULL,
    dossier_id UUID NOT NULL REFERENCES pedagogie.dossier_eleve (id),
    categorie VARCHAR(40) NOT NULL,
    nom VARCHAR(160) NOT NULL,
    type_mime VARCHAR(120),
    taille BIGINT,
    uri VARCHAR(500),
    date_depot TIMESTAMPTZ NOT NULL,
    depose_par UUID,
    document_officiel BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_fichier_joint_dossier ON pedagogie.fichier_joint (dossier_id);

-- ============================================================
-- AGREGAT : INSCRIPTION (eleve a l annee dans une classe)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.inscription (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES pedagogie.eleve (id),
    annee_id UUID,
    classe_id UUID,
    date_inscription DATE NOT NULL,
    statut VARCHAR(20) NOT NULL,
    redoublant BOOLEAN NOT NULL DEFAULT FALSE,
    nouveau BOOLEAN NOT NULL DEFAULT TRUE,
    ecole_origine VARCHAR(160),
    moyenne_annee_precedente NUMERIC(6,2),
    classement_annee_precedente INTEGER,
    frais_regles BOOLEAN NOT NULL DEFAULT FALSE,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_inscription_eleve ON pedagogie.inscription (eleve_id);

-- ============================================================
-- AGREGAT : CONGES ET SANCTIONS DE L ELEVE
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.conge_etude (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES pedagogie.eleve (id),
    type VARCHAR(20) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    motif VARCHAR(255),
    justificatif_id UUID,
    reprise_prevue DATE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_conge_eleve ON pedagogie.conge_etude (eleve_id);

CREATE TABLE IF NOT EXISTS pedagogie.sanction_disciplinaire (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES pedagogie.eleve (id),
    date_sanction DATE NOT NULL,
    type VARCHAR(30) NOT NULL,
    motif VARCHAR(255),
    duree_jours INTEGER,
    date_fin DATE,
    decide_par UUID,
    instance VARCHAR(160),
    PRIMARY KEY (id)
);
CREATE INDEX idx_sanction_eleve ON pedagogie.sanction_disciplinaire (eleve_id);

-- ============================================================
-- AGREGAT : RESPONSABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.parent (
    id UUID NOT NULL,
    matricule VARCHAR(30),
    nom VARCHAR(120) NOT NULL,
    post_nom VARCHAR(120),
    prenom VARCHAR(120) NOT NULL,
    sexe VARCHAR(20) NOT NULL,
    telephone VARCHAR(40) NOT NULL,
    telephone_secondaire VARCHAR(40),
    email VARCHAR(160),
    adresse_rue VARCHAR(160),
    adresse_quartier VARCHAR(120),
    adresse_commune VARCHAR(120),
    adresse_ville VARCHAR(120),
    adresse_province VARCHAR(120),
    adresse_pays VARCHAR(120),
    adresse_telephone VARCHAR(40),
    profession VARCHAR(120),
    statut VARCHAR(40),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.lien_parentalite (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES pedagogie.eleve (id),
    parent_id UUID NOT NULL REFERENCES pedagogie.parent (id),
    type VARCHAR(30) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    payeur BOOLEAN NOT NULL DEFAULT FALSE,
    receveur_notifications BOOLEAN NOT NULL DEFAULT FALSE,
    date_debut DATE NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_lien_eleve ON pedagogie.lien_parentalite (eleve_id);
CREATE INDEX idx_lien_parent ON pedagogie.lien_parentalite (parent_id);

-- ============================================================
-- AGREGAT : PERSONNEL
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.service_etablissement (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(30) NOT NULL,
    parent_id UUID,
    responsable_id UUID,
    email VARCHAR(160),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.agent (
    id UUID NOT NULL,
    matricule VARCHAR(30) NOT NULL,
    nom VARCHAR(120) NOT NULL,
    post_nom VARCHAR(120),
    prenom VARCHAR(120) NOT NULL,
    sexe VARCHAR(20) NOT NULL,
    date_naissance DATE,
    telephone VARCHAR(40),
    email VARCHAR(160),
    adresse_rue VARCHAR(160),
    adresse_quartier VARCHAR(120),
    adresse_commune VARCHAR(120),
    adresse_ville VARCHAR(120),
    adresse_province VARCHAR(120),
    adresse_pays VARCHAR(120),
    adresse_telephone VARCHAR(40),
    fonction_officielle VARCHAR(120),
    date_recrutement DATE,
    statut VARCHAR(40),
    service_rattachement_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.enseignant (
    id UUID NOT NULL,
    matricule VARCHAR(30) NOT NULL,
    nom VARCHAR(120) NOT NULL,
    post_nom VARCHAR(120),
    prenom VARCHAR(120) NOT NULL,
    sexe VARCHAR(20) NOT NULL,
    date_naissance DATE,
    telephone VARCHAR(40),
    email VARCHAR(160),
    adresse_rue VARCHAR(160),
    adresse_quartier VARCHAR(120),
    adresse_commune VARCHAR(120),
    adresse_ville VARCHAR(120),
    adresse_province VARCHAR(120),
    adresse_pays VARCHAR(120),
    adresse_telephone VARCHAR(40),
    diplome VARCHAR(160),
    diplome_plus VARCHAR(160),
    specialite JSONB NOT NULL DEFAULT '[]',
    date_recrutement DATE,
    statut VARCHAR(20) NOT NULL,
    volume_horaire_hebdo NUMERIC(8,2),
    service_rattachement_id UUID,
    photo VARCHAR(500),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS pedagogie.piece_identite_enseignant (
    id UUID NOT NULL,
    enseignant_id UUID NOT NULL REFERENCES pedagogie.enseignant (id),
    type VARCHAR(60) NOT NULL,
    numero VARCHAR(80) NOT NULL,
    date_delivrance DATE,
    date_expiration DATE,
    pays VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX idx_piece_enseignant ON pedagogie.piece_identite_enseignant (enseignant_id);

-- ============================================================
-- AGREGAT : AFFECTATION ET TITULARITE
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.affectation_enseignant (
    id UUID NOT NULL,
    enseignant_id UUID NOT NULL REFERENCES pedagogie.enseignant (id),
    classe_id UUID,
    matiere_id UUID,
    type VARCHAR(20) NOT NULL,
    titulaire BOOLEAN NOT NULL DEFAULT FALSE,
    volume_horaire_hebdo NUMERIC(8,2),
    date_debut DATE NOT NULL,
    date_fin DATE,
    principale BOOLEAN NOT NULL DEFAULT FALSE,
    remplacement_de UUID,
    motif VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_affectation_enseignant ON pedagogie.affectation_enseignant (enseignant_id);
CREATE INDEX idx_affectation_classe ON pedagogie.affectation_enseignant (classe_id);

CREATE TABLE IF NOT EXISTS pedagogie.titularite (
    id UUID NOT NULL,
    classe_id UUID NOT NULL,
    enseignant_id UUID NOT NULL,
    date_delegation DATE NOT NULL,
    date_retrait DATE,
    type VARCHAR(30),
    motif VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_titularite_classe ON pedagogie.titularite (classe_id);


-- ============================================================
-- AGREGAT : INSCRIPTION A L'UE (universite)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.inscription_ue (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    programme_id UUID,
    promotion_id UUID,
    ue_id UUID NOT NULL,
    annee_id UUID,
    semestre_id UUID,
    statut VARCHAR(30) NOT NULL,
    date_inscription DATE,
    credits_reconnus NUMERIC(8,2),
    moyenne_annee_precedente NUMERIC(6,2),
    moyenne_ue_precedente NUMERIC(6,2),
    option VARCHAR(120),
    motif_refus VARCHAR(255),
    rang_liste_attente INTEGER,
    valide_le TIMESTAMPTZ,
    valide_par UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_inscription_ue_eleve ON pedagogie.inscription_ue (eleve_id);
CREATE INDEX idx_inscription_ue_ue ON pedagogie.inscription_ue (ue_id);

-- ============================================================
-- AGREGAT : GROUPE (TD / TP) ET SES MEMBRES
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.groupe (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    cours_id UUID,
    ue_id UUID,
    classe_id UUID,
    enseignant_id UUID,
    salle_id UUID,
    creneau_id UUID,
    jour VARCHAR(20),
    capacite INTEGER NOT NULL DEFAULT 0,
    effectif INTEGER NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_groupe_classe ON pedagogie.groupe (classe_id);
CREATE INDEX idx_groupe_ue ON pedagogie.groupe (ue_id);

CREATE TABLE IF NOT EXISTS pedagogie.membre_groupe (
    id UUID NOT NULL,
    groupe_id UUID NOT NULL REFERENCES pedagogie.groupe (id),
    eleve_id UUID NOT NULL,
    date_affectation DATE,
    affecte_par UUID,
    rang INTEGER,
    PRIMARY KEY (id)
);
CREATE INDEX idx_membre_groupe_groupe ON pedagogie.membre_groupe (groupe_id);

-- ============================================================
-- AGREGAT : REGLE D'INSCRIPTION (eligibilite)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.regle_inscription (
    id UUID NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    programme_id UUID,
    condition_regle VARCHAR(255),
    credits_min NUMERIC(8,2),
    moyenne_min NUMERIC(6,2),
    creneau_age INTEGER,
    priorite INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);


-- ============================================================
-- AGREGAT : SEANCE (occurrence de cours)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.seance (
    id UUID NOT NULL,
    classe_id UUID,
    cours_id UUID,
    enseignant_id UUID,
    salle_id UUID,
    annee_id UUID,
    periode_id UUID,
    date DATE,
    heure_debut TIME,
    heure_fin TIME,
    volume_horaire_reel NUMERIC(6,2),
    type VARCHAR(20),
    statut VARCHAR(20),
    annulee BOOLEAN NOT NULL DEFAULT FALSE,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_seance_classe ON pedagogie.seance (classe_id);
CREATE INDEX idx_seance_date ON pedagogie.seance (date);

-- ============================================================
-- AGREGAT : FEUILLE DE PRESENCE (une par seance)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.feuille_presence (
    id UUID NOT NULL,
    seance_id UUID,
    classe_id UUID,
    cours_id UUID,
    enseignant_id UUID,
    date DATE,
    statut VARCHAR(20) NOT NULL,
    methode VARCHAR(20),
    ouverte_par UUID,
    ouverte_le TIMESTAMPTZ,
    soumise_le TIMESTAMPTZ,
    validee_par UUID,
    validee_le TIMESTAMPTZ,
    effectif_theorique INTEGER,
    effectif_present INTEGER,
    observations VARCHAR(500),
    PRIMARY KEY (id)
);
CREATE INDEX idx_feuille_seance ON pedagogie.feuille_presence (seance_id);
CREATE INDEX idx_feuille_classe ON pedagogie.feuille_presence (classe_id);
CREATE INDEX idx_feuille_date ON pedagogie.feuille_presence (date);

CREATE TABLE IF NOT EXISTS pedagogie.presence_eleve (
    id UUID NOT NULL,
    feuille_id UUID NOT NULL REFERENCES pedagogie.feuille_presence (id),
    eleve_id UUID NOT NULL,
    statut VARCHAR(30),
    heure_arrivee TIME,
    minutes_retard INTEGER,
    heure_depart TIME,
    depart_anticipe BOOLEAN NOT NULL DEFAULT FALSE,
    type_absence VARCHAR(30),
    justificatif_id UUID,
    saisie_par UUID,
    saisie_le TIMESTAMPTZ,
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    justifiee_le TIMESTAMPTZ,
    modifiee_apres_validation BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_presence_feuille ON pedagogie.presence_eleve (feuille_id);
CREATE INDEX idx_presence_eleve ON pedagogie.presence_eleve (eleve_id);

-- ============================================================
-- AGREGAT : JUSTIFICATIF
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.justificatif (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    type VARCHAR(30),
    date_reference DATE,
    date_depot DATE,
    motif VARCHAR(500),
    fichier_uri VARCHAR(500),
    statut VARCHAR(20) NOT NULL,
    traite_par UUID,
    traite_le TIMESTAMPTZ,
    motif_rejet VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_justificatif_eleve ON pedagogie.justificatif (eleve_id);


-- ============================================================
-- AGREGAT : EVALUATION
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.evaluation (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    type VARCHAR(30) NOT NULL,
    classe_id UUID,
    cours_id UUID,
    ue_id UUID,
    semestre_id UUID,
    matiere_id UUID,
    enseignant_id UUID,
    annee_id UUID,
    periode_id UUID,
    date_evaluation DATE,
    heure_debut TIME,
    duree_minutes INTEGER,
    salle_id UUID,
    statut VARCHAR(20) NOT NULL,
    grille_id UUID,
    grille_version INTEGER,
    bareme_type VARCHAR(30),
    note_max NUMERIC(6,2),
    coefficient_declare NUMERIC(6,2),
    consignes TEXT,
    anonymise BOOLEAN NOT NULL DEFAULT FALSE,
    comptee_dans_moyenne BOOLEAN NOT NULL DEFAULT TRUE,
    declenche_session BOOLEAN NOT NULL DEFAULT FALSE,
    annule_le TIMESTAMPTZ,
    motif_annulation VARCHAR(255),
    cree_le TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_evaluation_classe ON pedagogie.evaluation (classe_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_periode ON pedagogie.evaluation (periode_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_cours ON pedagogie.evaluation (cours_id);

-- ============================================================
-- AGREGAT : FEUILLE DE NOTES (generee par l evaluation)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.feuille_note (
    id UUID NOT NULL,
    evaluation_id UUID NOT NULL,
    statut VARCHAR(20),
    nb_notes_attendues INTEGER NOT NULL DEFAULT 0,
    nb_notes_saisies INTEGER NOT NULL DEFAULT 0,
    nb_absents INTEGER NOT NULL DEFAULT 0,
    ouverte_le TIMESTAMPTZ,
    cloturee_le TIMESTAMPTZ,
    verrouillee BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_feuille_note_evaluation
    ON pedagogie.feuille_note (evaluation_id);

-- ============================================================
-- NOTE (eleve)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.note (
    id UUID NOT NULL,
    evaluation_id UUID NOT NULL,
    feuille_id UUID,
    eleve_id UUID NOT NULL,
    valeur NUMERIC(6,2),
    statut VARCHAR(20) NOT NULL,
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    exclue BOOLEAN NOT NULL DEFAULT FALSE,
    motif_exclusion VARCHAR(255),
    coefficient_reel NUMERIC(6,2),
    source VARCHAR(30),
    saisie_par UUID,
    saisie_le TIMESTAMPTZ,
    approuvee BOOLEAN NOT NULL DEFAULT FALSE,
    motif_contestation VARCHAR(255),
    date_contestation TIMESTAMPTZ,
    remplacee_par UUID,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_note_evaluation ON pedagogie.note (evaluation_id);
CREATE INDEX IF NOT EXISTS idx_note_eleve ON pedagogie.note (eleve_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_note_evaluation_eleve
    ON pedagogie.note (evaluation_id, eleve_id);

-- ============================================================
-- AGREGAT : BULLETIN
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.bulletin (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    eleve_id UUID NOT NULL,
    annee_id UUID,
    periode_id UUID,
    classe_id UUID,
    type VARCHAR(30) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    grille_id UUID,
    grille_version INTEGER,
    grille_date_activation DATE,
    cascade JSONB,
    moyenne_generale NUMERIC(6,2),
    moyenne_periode NUMERIC(6,2),
    moyenne_annuelle NUMERIC(6,2),
    rang INTEGER,
    effectif INTEGER,
    appreciation_generale VARCHAR(500),
    mention_generale VARCHAR(60),
    decision VARCHAR(120),
    nb_absences_justifiees INTEGER NOT NULL DEFAULT 0,
    nb_absences_injustifiees INTEGER NOT NULL DEFAULT 0,
    nb_retards INTEGER NOT NULL DEFAULT 0,
    taux_presence NUMERIC(7,4),
    regle_assiduite VARCHAR(120),
    respecte_regle_assiduite BOOLEAN,
    avertissement_assiduite VARCHAR(500),
    retard_jours INTEGER,
    retard_heures INTEGER,
    genere_le TIMESTAMPTZ,
    publie_le TIMESTAMPTZ,
    genere_par UUID,
    rectifie_le TIMESTAMPTZ,
    motif_rectification VARCHAR(255),
    cree_par UUID,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_bulletin_classe ON pedagogie.bulletin (classe_id);
CREATE INDEX IF NOT EXISTS idx_bulletin_periode ON pedagogie.bulletin (periode_id);
CREATE INDEX IF NOT EXISTS idx_bulletin_eleve ON pedagogie.bulletin (eleve_id);

-- ============================================================
-- LIGNE DE BULLETIN
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.ligne_bulletin (
    id UUID NOT NULL,
    bulletin_id UUID NOT NULL,
    niveau VARCHAR(40) NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    parent_code VARCHAR(60),
    coefficient NUMERIC(8,4) NOT NULL DEFAULT 1,
    poids_normalise NUMERIC(8,4),
    moyenne_periode NUMERIC(6,2),
    moyenne_annuelle NUMERIC(6,2),
    moyenne_ue NUMERIC(6,2),
    moyenne_cours NUMERIC(6,2),
    rang INTEGER,
    effectif INTEGER,
    mention VARCHAR(60),
    appreciation VARCHAR(500),
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    cas_particulier VARCHAR(255),
    ue_id UUID,
    cours_id UUID,
    matiere_id UUID,
    detail JSONB,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_bulletin_bulletin
    ON pedagogie.ligne_bulletin (bulletin_id);

-- ============================================================
-- COUPON DE NOTES (partie perforee du bulletin)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.coupon_notes (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    bulletin_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    annee_id UUID,
    periode_id UUID,
    classe VARCHAR(120),
    date_edition DATE NOT NULL,
    statut VARCHAR(30) NOT NULL,
    note_moyenne NUMERIC(6,2),
    mention VARCHAR(60),
    url_qr VARCHAR(500),
    code_barre VARCHAR(120),
    empreinte VARCHAR(128),
    detache_le TIMESTAMPTZ,
    detache_par UUID,
    signe_par UUID,
    signe_le TIMESTAMPTZ,
    observation VARCHAR(255),
    date_rendu TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_coupon_notes_bulletin
    ON pedagogie.coupon_notes (bulletin_id);


-- ============================================================
-- AGREGAT : SESSION D EXAMEN (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.session_examen (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    type VARCHAR(40) NOT NULL,
    annee_id UUID,
    periode_id UUID,
    classe_id UUID,
    date_ouverture DATE,
    date_cloture DATE,
    statut VARCHAR(30) NOT NULL,
    annee_universitaire BOOLEAN NOT NULL DEFAULT FALSE,
    convocation_emise BOOLEAN NOT NULL DEFAULT FALSE,
    proces_verbal_id UUID,
    grille_deliberation VARCHAR(120),
    nb_candidats_attendus INTEGER,
    nb_candidats_convoques INTEGER,
    motif_annulation VARCHAR(255),
    annule_le TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_session_examen_annee ON pedagogie.session_examen (annee_id);
CREATE INDEX IF NOT EXISTS idx_session_examen_classe ON pedagogie.session_examen (classe_id);

-- ============================================================
-- EPREUVE
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.epreuve (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    matiere_id UUID,
    ue_id UUID,
    type VARCHAR(30) NOT NULL,
    date_epreuve DATE,
    heure_debut TIME,
    duree_minutes INTEGER,
    bareme VARCHAR(60),
    coefficient NUMERIC(6,2),
    statut VARCHAR(20) NOT NULL,
    comptabilise_dans_moyenne BOOLEAN NOT NULL DEFAULT TRUE,
    periode_rattachement UUID,
    evaluation_origine UUID,
    salle_par_defaut UUID,
    consignes TEXT,
    anonymise BOOLEAN NOT NULL DEFAULT FALSE,
    note_eliminatoire NUMERIC(6,2),
    declaree_hors_moyenne BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_epreuve_session ON pedagogie.epreuve (session_id);

-- ============================================================
-- CRENEAU
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.creneau_examen (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    date_creneau DATE,
    type VARCHAR(20) NOT NULL,
    heure_debut TIME,
    heure_fin TIME,
    ordre INTEGER,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_creneau_examen_session ON pedagogie.creneau_examen (session_id);

-- ============================================================
-- CONVOCATION
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.convocation (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    date_convocation DATE,
    statut VARCHAR(20) NOT NULL,
    generee_le TIMESTAMPTZ,
    notifiee_le TIMESTAMPTZ,
    canal VARCHAR(40),
    accuse BOOLEAN NOT NULL DEFAULT FALSE,
    qr_code VARCHAR(200),
    emplacement VARCHAR(200),
    url_telechargement VARCHAR(500),
    signee BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_convocation_session ON pedagogie.convocation (session_id);
CREATE INDEX IF NOT EXISTS idx_convocation_eleve ON pedagogie.convocation (eleve_id);

CREATE TABLE IF NOT EXISTS pedagogie.ligne_convocation (
    id UUID NOT NULL,
    convocation_id UUID NOT NULL,
    epreuve_id UUID,
    date_ligne DATE,
    heure_debut TIME,
    salle VARCHAR(120),
    numero_place VARCHAR(40),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_convocation_conv
    ON pedagogie.ligne_convocation (convocation_id);

-- ============================================================
-- NOTE D EXAMEN
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.note_examen (
    id UUID NOT NULL,
    epreuve_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    valeur NUMERIC(6,2),
    statut VARCHAR(20) NOT NULL,
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    hors_moyenne BOOLEAN NOT NULL DEFAULT FALSE,
    exclue BOOLEAN NOT NULL DEFAULT FALSE,
    motif_exclusion VARCHAR(255),
    periode_imputation UUID,
    correcteur UUID,
    seconde_correction BOOLEAN NOT NULL DEFAULT FALSE,
    coefficient NUMERIC(6,2),
    saisi_le TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_note_examen_epreuve ON pedagogie.note_examen (epreuve_id);
CREATE INDEX IF NOT EXISTS idx_note_examen_eleve ON pedagogie.note_examen (eleve_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_note_examen_epreuve_eleve
    ON pedagogie.note_examen (epreuve_id, eleve_id);

-- ============================================================
-- CORRECTION
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.correction (
    id UUID NOT NULL,
    epreuve_id UUID NOT NULL,
    correcteur UUID,
    date_correction DATE,
    statut VARCHAR(20) NOT NULL,
    nb_notes_corrigees INTEGER NOT NULL DEFAULT 0,
    moyenne_brute NUMERIC(6,2),
    moyenne_retenue NUMERIC(6,2),
    plus_forte_moyenne NUMERIC(6,2),
    plus_faible_moyenne NUMERIC(6,2),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_correction_epreuve ON pedagogie.correction (epreuve_id);

-- ============================================================
-- AGREGAT : DELIBERATION
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.deliberation (
    id UUID NOT NULL,
    session_id UUID,
    reference VARCHAR(60) NOT NULL,
    date_deliberation DATE,
    statut VARCHAR(20) NOT NULL,
    president UUID,
    membres JSONB,
    quorum_atteint BOOLEAN NOT NULL DEFAULT FALSE,
    grille_version INTEGER,
    date_cloture TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_deliberation_session ON pedagogie.deliberation (session_id);

CREATE TABLE IF NOT EXISTS pedagogie.decision (
    id UUID NOT NULL,
    deliberation_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    classe_id UUID,
    moyenne_generale NUMERIC(6,2),
    moyenne_periode NUMERIC(6,2),
    rang INTEGER,
    effectif INTEGER,
    taux_reussite NUMERIC(6,2),
    nb_absences INTEGER,
    decision VARCHAR(20),
    mention VARCHAR(60),
    seuil_applique VARCHAR(120),
    cr VARCHAR(5),
    motif VARCHAR(255),
    date_decision DATE,
    notifie BOOLEAN NOT NULL DEFAULT FALSE,
    date_notification TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_decision_deliberation ON pedagogie.decision (deliberation_id);

CREATE TABLE IF NOT EXISTS pedagogie.decision_ue (
    id UUID NOT NULL,
    deliberation_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    ue_id UUID,
    moyenne_ue NUMERIC(6,2),
    credits NUMERIC(8,2),
    decision VARCHAR(20),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_decision_ue_deliberation ON pedagogie.decision_ue (deliberation_id);

CREATE TABLE IF NOT EXISTS pedagogie.detail_ue (
    id UUID NOT NULL,
    decision_ue_id UUID NOT NULL,
    cours_id UUID,
    moyenne_cours NUMERIC(6,2),
    coefficient NUMERIC(6,2),
    admis BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_detail_ue_decision ON pedagogie.detail_ue (decision_ue_id);

CREATE TABLE IF NOT EXISTS pedagogie.resultat_officiel (
    id UUID NOT NULL,
    decision_id UUID,
    reference VARCHAR(60) NOT NULL,
    publie BOOLEAN NOT NULL DEFAULT FALSE,
    date_publication TIMESTAMPTZ,
    generateur UUID,
    url_document VARCHAR(500),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_resultat_officiel_decision ON pedagogie.resultat_officiel (decision_id);

-- ============================================================
-- RATTRAPAGE
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.rattrapage (
    id UUID NOT NULL,
    session_origine_id UUID,
    session_rattrapage_id UUID,
    eleve_id UUID NOT NULL,
    epreuves_concernees JSONB,
    convocation_id UUID,
    ne_pas_presenter BOOLEAN NOT NULL DEFAULT FALSE,
    motif VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_rattrapage_eleve ON pedagogie.rattrapage (eleve_id);

-- ============================================================
-- AGREGAT : PROCES VERBAL
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.proces_verbal (
    id UUID NOT NULL,
    session_id UUID,
    reference VARCHAR(60) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_redaction DATE,
    redige_par UUID,
    presence_directeur BOOLEAN NOT NULL DEFAULT FALSE,
    observations TEXT,
    nb_absences INTEGER NOT NULL DEFAULT 0,
    nb_incidents INTEGER NOT NULL DEFAULT 0,
    date_cloture TIMESTAMPTZ,
    motif_contestation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_proces_verbal_session ON pedagogie.proces_verbal (session_id);

CREATE TABLE IF NOT EXISTS pedagogie.signature (
    id UUID NOT NULL,
    proces_verbal_id UUID NOT NULL,
    signataire UUID,
    role VARCHAR(60),
    date_signature TIMESTAMPTZ,
    empreinte VARCHAR(128),
    validee BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_signature_pv ON pedagogie.signature (proces_verbal_id);


-- ============================================================
-- AGREGAT : GRILLE TARIFAIRE (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.grille_tarifaire (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    annee UUID,
    profil VARCHAR(40),
    statut VARCHAR(20) NOT NULL,
    devise VARCHAR(10),
    date_activation DATE,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    version INTEGER NOT NULL DEFAULT 1,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_grille_tarifaire_annee ON pedagogie.grille_tarifaire (annee);
CREATE INDEX IF NOT EXISTS idx_grille_tarifaire_profil ON pedagogie.grille_tarifaire (profil);

CREATE TABLE IF NOT EXISTS pedagogie.ligne_grille (
    id UUID NOT NULL,
    grille_id UUID NOT NULL,
    type_frais VARCHAR(30) NOT NULL,
    categorie VARCHAR(20) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_promo NUMERIC(14,2),
    devise VARCHAR(10),
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    periodicite VARCHAR(20) NOT NULL DEFAULT 'PONCTUELLE',
    jour INTEGER,
    nb_echeances INTEGER,
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_grille_grille ON pedagogie.ligne_grille (grille_id);

CREATE TABLE IF NOT EXISTS pedagogie.reduction (
    id UUID NOT NULL,
    grille_id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200),
    type VARCHAR(20) NOT NULL,
    valeur NUMERIC(14,2) NOT NULL DEFAULT 0,
    cumulable BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_reduction_grille ON pedagogie.reduction (grille_id);

CREATE TABLE IF NOT EXISTS pedagogie.condition_reduction (
    id UUID NOT NULL,
    reduction_id UUID NOT NULL,
    critere VARCHAR(60) NOT NULL,
    operateur VARCHAR(10) NOT NULL,
    valeur VARCHAR(120) NOT NULL,
    cumulable BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_condition_reduction_reduction
    ON pedagogie.condition_reduction (reduction_id);

-- ============================================================
-- AGREGAT : AVIS DE PAIEMENT (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.avis_paiement (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    eleve UUID NOT NULL,
    annee UUID,
    classe UUID,
    grille_id UUID,
    date_emission DATE,
    date_echeance_globale DATE,
    statut VARCHAR(30) NOT NULL,
    montant_brut NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_reduction NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_net NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_regle NUMERIC(14,2) NOT NULL DEFAULT 0,
    solde_du NUMERIC(14,2) NOT NULL DEFAULT 0,
    devise VARCHAR(10),
    taux_change NUMERIC(14,6),
    journalier UUID,
    ecriture_generee UUID,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_avis_paiement_eleve ON pedagogie.avis_paiement (eleve);
CREATE INDEX IF NOT EXISTS idx_avis_paiement_annee ON pedagogie.avis_paiement (annee);

CREATE TABLE IF NOT EXISTS pedagogie.ligne_frais (
    id UUID NOT NULL,
    avis_id UUID NOT NULL,
    type_frais VARCHAR(30) NOT NULL,
    libelle VARCHAR(200),
    montant_brut NUMERIC(14,2) NOT NULL DEFAULT 0,
    taux_reduction NUMERIC(6,4),
    montant_reduction NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_net NUMERIC(14,2) NOT NULL DEFAULT 0,
    nb_echeances INTEGER NOT NULL DEFAULT 1,
    annee_reference VARCHAR(20),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_frais_avis ON pedagogie.ligne_frais (avis_id);

CREATE TABLE IF NOT EXISTS pedagogie.echeance (
    id UUID NOT NULL,
    ligne_id UUID NOT NULL,
    numero INTEGER NOT NULL,
    date_echeance DATE,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_regle NUMERIC(14,2) NOT NULL DEFAULT 0,
    statut VARCHAR(30) NOT NULL DEFAULT 'A_VENIR',
    date_reglement DATE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_echeance_ligne ON pedagogie.echeance (ligne_id);

-- ============================================================
-- AGREGAT : PAIEMENT (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.paiement (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    avis_id UUID,
    eleve UUID NOT NULL,
    date DATE,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    moyen VARCHAR(20) NOT NULL,
    compte_debit VARCHAR(120),
    reference_transaction VARCHAR(120),
    id_evenement VARCHAR(120),
    beneficiaire VARCHAR(20) NOT NULL DEFAULT 'ELEVE',
    reference_beneficiaire VARCHAR(120),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    encaisse_par UUID,
    reste_a_rembourser NUMERIC(14,2) NOT NULL DEFAULT 0,
    coupon_genere UUID,
    ecriture_generee UUID,
    motif_annulation VARCHAR(255),
    motif_rejet VARCHAR(255),
    date_validation TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_paiement_eleve ON pedagogie.paiement (eleve);
CREATE INDEX IF NOT EXISTS idx_paiement_avis ON pedagogie.paiement (avis_id);

CREATE TABLE IF NOT EXISTS pedagogie.imputation (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    echeance_id UUID NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    date_imputation DATE,
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_imputation_paiement ON pedagogie.imputation (paiement_id);
CREATE INDEX IF NOT EXISTS idx_imputation_echeance ON pedagogie.imputation (echeance_id);

CREATE TABLE IF NOT EXISTS pedagogie.paiement_mobile_money (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    operateur VARCHAR(60),
    numero_telephone VARCHAR(30),
    reference_transaction VARCHAR(120),
    date_transaction TIMESTAMPTZ,
    statut_transaction VARCHAR(40),
    frais NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_recu NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_mobile_money_paiement
    ON pedagogie.paiement_mobile_money (paiement_id);

CREATE TABLE IF NOT EXISTS pedagogie.paiement_virement (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    banque VARCHAR(120),
    numero_compte VARCHAR(60),
    reference_virement VARCHAR(120),
    date_valeur DATE,
    commission NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_virement_paiement ON pedagogie.paiement_virement (paiement_id);

CREATE TABLE IF NOT EXISTS pedagogie.paiement_especes (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    caja UUID,
    numero_recepisse VARCHAR(60),
    agent UUID,
    contresigne BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_especes_paiement ON pedagogie.paiement_especes (paiement_id);

CREATE TABLE IF NOT EXISTS pedagogie.remboursement (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    motif VARCHAR(255),
    date DATE,
    moyen VARCHAR(20),
    reference_transaction VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_remboursement_paiement ON pedagogie.remboursement (paiement_id);

-- ============================================================
-- AGREGAT : COUPON DE PAIEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.coupon_paiement (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    sequence INTEGER NOT NULL,
    annee UUID NOT NULL,
    eleve UUID NOT NULL,
    paiement UUID,
    avis UUID,
    ligne_frais UUID,
    date_edition DATE,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    libelle VARCHAR(200),
    moyen VARCHAR(20),
    statut VARCHAR(20) NOT NULL DEFAULT 'A_TIRER',
    devise VARCHAR(10),
    mention VARCHAR(200),
    url_qr VARCHAR(500),
    code_barre VARCHAR(120),
    empreinte VARCHAR(128),
    remis_le TIMESTAMPTZ,
    remis_a UUID,
    reference_signature VARCHAR(120),
    annule BOOLEAN NOT NULL DEFAULT FALSE,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_coupon_paiement_eleve ON pedagogie.coupon_paiement (eleve);
CREATE INDEX IF NOT EXISTS idx_coupon_paiement_annee ON pedagogie.coupon_paiement (annee);

-- ============================================================
-- JOURNALIER
-- ============================================================

CREATE TABLE IF NOT EXISTS pedagogie.journalier (
    id UUID NOT NULL,
    date DATE,
    site UUID,
    statut VARCHAR(20) NOT NULL DEFAULT 'OUVERT',
    fond NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_journalier_date ON pedagogie.journalier (date);
CREATE INDEX IF NOT EXISTS idx_journalier_site ON pedagogie.journalier (site);