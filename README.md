<h1 align="center">OuR_BaTiMa</h1>
-- Table Utilisateur
CREATE TABLE Utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    role ENUM('Artisan', 'Constructeur', 'GestionnaireStock', 'Admin', 'Client') DEFAULT 'Client',
    adresse TEXT,
    mot_de_passe VARCHAR(255) NOT NULL,
    statut ENUM('actif', 'inactif', 'en_attente') DEFAULT 'en_attente',
    isConfirmed BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB;

-- Tables spécialisées
CREATE TABLE Artisan (
user_id INT PRIMARY KEY,
specialite VARCHAR(100) NOT NULL,
salaire_heure DECIMAL(10,2) NOT NULL,
FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Constructeur (
user_id INT PRIMARY KEY,
specialite VARCHAR(100) NOT NULL,
salaire_heure DECIMAL(10,2) NOT NULL,
FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE GestionnaireStock (
user_id INT PRIMARY KEY,
FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Admin (
user_id INT PRIMARY KEY,
FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Client (
user_id INT PRIMARY KEY,
FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Projet (créée avant Taches)
CREATE TABLE Projet (
Id_projet INT PRIMARY KEY AUTO_INCREMENT,
Id_equipe INT,
type VARCHAR(20) NOT NULL,
styleArch VARCHAR(20),
budget DECIMAL(15,3),
etat VARCHAR(20),
dateCreation DATE NOT NULL,
FOREIGN KEY (Id_equipe) REFERENCES Equipe(id) -- Référence corrigée
) ENGINE=InnoDB;

-- Table Equipe
CREATE TABLE Equipe (
id INT PRIMARY KEY AUTO_INCREMENT,
nom VARCHAR(100) NOT NULL,
constructeur_id INT NOT NULL,
gestionnaire_stock_id INT NOT NULL,
FOREIGN KEY (constructeur_id) REFERENCES Constructeur(user_id),
FOREIGN KEY (gestionnaire_stock_id) REFERENCES GestionnaireStock(user_id)
) ENGINE=InnoDB;

-- Table de liaison Equipe-Artisan
CREATE TABLE Equipe_Artisan (
equipe_id INT NOT NULL,
artisan_id INT NOT NULL,
PRIMARY KEY (equipe_id, artisan_id),
FOREIGN KEY (equipe_id) REFERENCES Equipe(id) ON DELETE CASCADE,
FOREIGN KEY (artisan_id) REFERENCES Artisan(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Taches (avec références corrigées)
CREATE TABLE Taches (
id_tache INT PRIMARY KEY AUTO_INCREMENT,
id_projet INT,
constructeur_id INT, -- Nom de colonne uniformisé
artisan_id INT,      -- Nom de colonne uniformisé
titre VARCHAR(255) NOT NULL,
description TEXT,
statut ENUM('En attente', 'En cours', 'Terminée') DEFAULT 'En attente',
date_debut DATE NOT NULL,
date_fin DATE NOT NULL,
FOREIGN KEY (id_projet) REFERENCES Projet(Id_projet) ON DELETE CASCADE,
FOREIGN KEY (constructeur_id) REFERENCES Constructeur(user_id) ON DELETE CASCADE,
FOREIGN KEY (artisan_id) REFERENCES Artisan(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Plannification
CREATE TABLE Plannification (
id_plannification INT PRIMARY KEY AUTO_INCREMENT,
id_tache INT,
priorite ENUM('Haute', 'Moyenne', 'Basse') DEFAULT 'Moyenne',
date_planifiee DATE NOT NULL,
heure_debut TIME,
heure_fin TIME,
remarques TEXT,
statut ENUM('Planifié', 'En cours', 'Terminé') DEFAULT 'Planifié',
FOREIGN KEY (id_tache) REFERENCES Taches(id_tache) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table EtapeProjet
CREATE TABLE EtapeProjet (
Id_etapeProjet INT PRIMARY KEY AUTO_INCREMENT,
Id_projet INT,
nomEtape VARCHAR(50) NOT NULL,
description TEXT NOT NULL,
dateDebut DATE,
dateFin DATE,
statut ENUM('En attente', 'En cours', 'Finie') DEFAULT 'En attente',
montant DECIMAL(15,3),
FOREIGN KEY (Id_projet) REFERENCES Projet(Id_projet) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Terrain
CREATE TABLE Terrain (
Id_terrain INT PRIMARY KEY AUTO_INCREMENT,
Id_projet INT,
emplacement VARCHAR(100) NOT NULL,
caracteristiques TEXT NOT NULL,
superficie DECIMAL(10,2),
detailsGeo VARCHAR(100),
FOREIGN KEY (Id_projet) REFERENCES Projet(Id_projet) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Visite
CREATE TABLE Visite (
Id_visite INT PRIMARY KEY AUTO_INCREMENT,
Id_projet INT,
dateVisite DATE NOT NULL,
observations VARCHAR(200),
FOREIGN KEY (Id_projet) REFERENCES Projet(Id_projet) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Rapport
CREATE TABLE Rapport (
id INT PRIMARY KEY AUTO_INCREMENT,
Id_etapeProjet INT,
titre VARCHAR(100) NOT NULL,
contenu TEXT NOT NULL,
dateCreation DATE NOT NULL,
FOREIGN KEY (Id_etapeProjet) REFERENCES EtapeProjet(Id_etapeProjet) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Reclamation (déclaration unique)
CREATE TABLE Reclamation (
id INT PRIMARY KEY AUTO_INCREMENT,
description VARCHAR(255) NOT NULL,
statut ENUM('En attente', 'Traitée', 'Rejetée') DEFAULT 'En attente',
date DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table Reponse (déclaration unique et corrigée)
CREATE TABLE Reponse (
id INT PRIMARY KEY AUTO_INCREMENT,
reclamation_id INT NOT NULL,
description VARCHAR(255) NOT NULL,
statut ENUM('Envoyée', 'En attente') DEFAULT 'En attente',
date DATETIME DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (reclamation_id) REFERENCES Reclamation(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table `messaging_accounts`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `messaging_accounts` (
`user_id` INT NOT NULL PRIMARY KEY,
`role_specific_id` INT NULL COMMENT 'ID spécifique au rôle (artisan_id, constructeur_id, etc.)',
`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
INDEX `idx_role_specific` (`role_specific_id` ASC),
CONSTRAINT `fk_user_id`
FOREIGN KEY (`user_id`)
REFERENCES `Utilisateur` (`id`)
ON DELETE CASCADE
ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table `notifications`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `notifications` (
`notification_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`user_id` INT NOT NULL,
`message` TEXT NOT NULL,
`is_read` TINYINT(1) NOT NULL DEFAULT 0,
`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`read_at` TIMESTAMP NULL,
INDEX `fk_notification_user_idx` (`user_id` ASC),
CONSTRAINT `fk_notification_user`
FOREIGN KEY (`user_id`)
REFERENCES `Utilisateur` (`id`)
ON DELETE CASCADE
ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table `direct_messages` (Optionnel pour la messagerie privée)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `direct_messages` (
`message_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`sender_id` INT NOT NULL,
`receiver_id` INT NOT NULL,
`content` TEXT NOT NULL,
`sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
`is_read` TINYINT(1) NOT NULL DEFAULT 0,
INDEX `fk_dm_sender_idx` (`sender_id` ASC),
INDEX `fk_dm_receiver_idx` (`receiver_id` ASC),
CONSTRAINT `fk_dm_sender`
FOREIGN KEY (`sender_id`)
REFERENCES `Utilisateur` (`id`)
ON DELETE CASCADE
ON UPDATE CASCADE,
CONSTRAINT `fk_dm_receiver`
FOREIGN KEY (`receiver_id`)
REFERENCES `Utilisateur` (`id`)
ON DELETE CASCADE
ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Index supplémentaires pour les performances
-- --------------------------------------------------------
CREATE INDEX idx_notif_created ON notifications(created_at DESC);
CREATE INDEX idx_messages_sent ON direct_messages(sent_at DESC);

CREATE TABLE Conversation (
id INT PRIMARY KEY AUTO_INCREMENT,
equipe_id INT NOT NULL,
nom VARCHAR(255) NOT NULL,
date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (equipe_id) REFERENCES Equipe(id)
);

CREATE TABLE Conversation_Membre (
conversation_id INT,
utilisateur_id INT,
PRIMARY KEY (conversation_id, utilisateur_id),
FOREIGN KEY (conversation_id) REFERENCES Conversation(id),
FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id)
);

CREATE TABLE Message (
id INT PRIMARY KEY AUTO_INCREMENT,
conversation_id INT,
expediteur_id INT,
contenu TEXT,
date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
lu BOOLEAN DEFAULT false,
FOREIGN KEY (conversation_id) REFERENCES Conversation(id),
FOREIGN KEY (expediteur_id) REFERENCES Utilisateur(id)
);
>pip install agora-token-builder
