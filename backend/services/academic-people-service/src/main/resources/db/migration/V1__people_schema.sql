CREATE SCHEMA IF NOT EXISTS academic_people;

-- ============================================================
-- AGREGAT : ELEVE > DOSSIER > PIECES
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_people.eleve (
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

CREATE TABLE IF NOT EXISTS academic_people.dossier_eleve (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES academic_people.eleve (id),
    reference VARCHAR(60) NOT NULL,
    date_ouverture DATE NOT NULL,
    statut VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_dossier_eleve ON academic_people.dossier_eleve (eleve_id);

CREATE TABLE IF NOT EXISTS academic_people.fichier_joint (
    id UUID NOT NULL,
    dossier_id UUID NOT NULL REFERENCES academic_people.dossier_eleve (id),
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
CREATE INDEX idx_fichier_joint_dossier ON academic_people.fichier_joint (dossier_id);

-- ============================================================
-- AGREGAT : INSCRIPTION (eleve a l annee dans une classe)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_people.inscription (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES academic_people.eleve (id),
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
CREATE INDEX idx_inscription_eleve ON academic_people.inscription (eleve_id);

-- ============================================================
-- AGREGAT : CONGES ET SANCTIONS DE L ELEVE
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_people.conge_etude (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES academic_people.eleve (id),
    type VARCHAR(20) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    motif VARCHAR(255),
    justificatif_id UUID,
    reprise_prevue DATE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_conge_eleve ON academic_people.conge_etude (eleve_id);

CREATE TABLE IF NOT EXISTS academic_people.sanction_disciplinaire (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES academic_people.eleve (id),
    date_sanction DATE NOT NULL,
    type VARCHAR(30) NOT NULL,
    motif VARCHAR(255),
    duree_jours INTEGER,
    date_fin DATE,
    decide_par UUID,
    instance VARCHAR(160),
    PRIMARY KEY (id)
);
CREATE INDEX idx_sanction_eleve ON academic_people.sanction_disciplinaire (eleve_id);

-- ============================================================
-- AGREGAT : RESPONSABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_people.parent (
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

CREATE TABLE IF NOT EXISTS academic_people.lien_parentalite (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL REFERENCES academic_people.eleve (id),
    parent_id UUID NOT NULL REFERENCES academic_people.parent (id),
    type VARCHAR(30) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    payeur BOOLEAN NOT NULL DEFAULT FALSE,
    receveur_notifications BOOLEAN NOT NULL DEFAULT FALSE,
    date_debut DATE NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_lien_eleve ON academic_people.lien_parentalite (eleve_id);
CREATE INDEX idx_lien_parent ON academic_people.lien_parentalite (parent_id);

-- ============================================================
-- AGREGAT : PERSONNEL
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_people.service_etablissement (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(30) NOT NULL,
    parent_id UUID,
    responsable_id UUID,
    email VARCHAR(160),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_people.agent (
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

CREATE TABLE IF NOT EXISTS academic_people.enseignant (
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

CREATE TABLE IF NOT EXISTS academic_people.piece_identite_enseignant (
    id UUID NOT NULL,
    enseignant_id UUID NOT NULL REFERENCES academic_people.enseignant (id),
    type VARCHAR(60) NOT NULL,
    numero VARCHAR(80) NOT NULL,
    date_delivrance DATE,
    date_expiration DATE,
    pays VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX idx_piece_enseignant ON academic_people.piece_identite_enseignant (enseignant_id);

-- ============================================================
-- AGREGAT : AFFECTATION ET TITULARITE
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_people.affectation_enseignant (
    id UUID NOT NULL,
    enseignant_id UUID NOT NULL REFERENCES academic_people.enseignant (id),
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
CREATE INDEX idx_affectation_enseignant ON academic_people.affectation_enseignant (enseignant_id);
CREATE INDEX idx_affectation_classe ON academic_people.affectation_enseignant (classe_id);

CREATE TABLE IF NOT EXISTS academic_people.titularite (
    id UUID NOT NULL,
    classe_id UUID NOT NULL,
    enseignant_id UUID NOT NULL,
    date_delegation DATE NOT NULL,
    date_retrait DATE,
    type VARCHAR(30),
    motif VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_titularite_classe ON academic_people.titularite (classe_id);
