# **Glossaire Métier		Progiciel Bamboo Assur**

## **Document de Référence du Langage Ubiquitaire**

**Version:** 1.1  
**Date:** 1er août 2025  
**Projet:** Progiciel Bamboo Assur  
**Auteur:** MAKOSSO Loïck Esdras

## 

## **Introduction**

Ce glossaire définit le langage ubiquitaire du domaine de l'assurance pour le projet Bamboo Assur. Il établit un vocabulaire commun entre les experts métier, les développeurs et toutes les parties prenantes du projet. Les termes sont organisés par contexte métier selon les principes du Domain-Driven Design.

---

## **0\. Concepts Fondamentaux du Secteur**

### **Assurance / Insurance**

Opération par laquelle une partie, l'assureur, s'engage moyennant le paiement d'une prime à fournir à une autre partie, l'assuré, une prestation prédéfinie en cas de réalisation d'un risque déterminé. L'assurance constitue un mécanisme de mutualisation des risques permettant de transférer la charge financière d'événements incertains d'un individu vers une collectivité. Elle repose sur trois principes fondamentaux : l'aléa (l'incertitude de l'événement), la mutualisation (le partage du risque entre plusieurs assurés) et la bonne foi (la transparence dans la déclaration des risques).

### **Assureur / Insurer / Insurance Company**

Société ou mutuelle d'assurance dûment agréée par l'autorité de tutelle pour pratiquer les opérations d'assurance. L'assureur assume le risque transféré par les assurés en contrepartie du paiement de primes. Il gère les fonds collectés, constitue les provisions techniques nécessaires pour honorer ses engagements futurs, et verse les indemnités ou prestations prévues aux contrats lors de la survenance des sinistres. L'assureur peut être une société anonyme appartenant à des actionnaires ou une mutuelle appartenant à ses sociétaires. Dans le contexte de Bamboo Assur, ce terme désigne les compagnies d'assurance partenaires qui souscrivent effectivement les risques et assument la responsabilité financière des engagements.

### **Compagnie d'Assurance / Insurance Company**

Organisation juridiquement constituée et réglementairement agréée dont l'activité principale consiste à souscrire et gérer des contrats d'assurance. La compagnie d'assurance dispose d'un capital social ou de fonds mutuels, emploie des équipes techniques et commerciales, et répond à des exigences strictes de solvabilité et de gouvernance. Elle peut opérer sur une ou plusieurs branches d'assurance selon les agréments obtenus auprès de l'autorité de contrôle.

### **Assureur Partenaire / Partner Insurer**

Compagnie d'assurance ayant établi une relation contractuelle avec Bamboo Assur pour distribuer ses produits et services via la plateforme. L'assureur partenaire conserve la responsabilité de la souscription des risques, du provisionnement technique et du règlement des sinistres. Il délègue à Bamboo Assur certaines fonctions de distribution, de gestion administrative et de relation client, tout en restant juridiquement responsable des engagements contractuels envers les assurés. Les assureurs partenaires communiquent avec la plateforme Bamboo Assur via des interfaces API permettant l'échange de données relatives aux contrats, aux primes et aux sinistres.

### **Courtier en Assurance / Insurance Broker**

Intermédiaire d'assurance professionnel qui représente juridiquement les intérêts des clients et non ceux des compagnies d'assurance. Le courtier recherche sur le marché les meilleures solutions d'assurance adaptées aux besoins de ses clients, négocie les conditions et tarifs auprès de plusieurs assureurs, et accompagne ses clients dans la gestion de leurs contrats et le règlement de leurs sinistres. Contrairement à l'agent général qui représente une seule compagnie, le courtier dispose d'une liberté totale dans le choix des assureurs avec lesquels il travaille. Dans le contexte de Bamboo Assur, le courtier utilise la plateforme métier pour gérer l'ensemble du portefeuille de ses clients, accéder aux produits de multiples assureurs partenaires et bénéficier d'outils de pilotage de son activité.

### **Bamboo Assur / Bamboo Assur**

Entité de courtage en assurance exploitant une plateforme digitale intégrée permettant la distribution, la gestion et le suivi de produits d'assurance issus de multiples compagnies partenaires. Bamboo Assur agit en qualité de courtier pour le compte de ses clients, leur donnant accès à un large éventail de solutions d'assurance vie, non-vie et santé. La plateforme Bamboo Assur constitue l'interface technique et opérationnelle entre les assurés, les assureurs partenaires et les différents intermédiaires du marché. Elle ne souscrit pas elle-même les risques mais orchestre l'ensemble du processus d'assurance depuis la simulation jusqu'au règlement des sinistres.

---

## **1\. Acteurs du Système (section enrichie)**

### **Souscripteur / Policyholder**

Personne physique ou morale qui signe le contrat d'assurance et s'engage à payer les primes. Le souscripteur peut être différent de l'assuré ou du bénéficiaire. Il est le cocontractant de l'assureur et dispose de droits spécifiques tels que la résiliation du contrat, la demande de modification des garanties ou la désignation des bénéficiaires en assurance vie. Le souscripteur est juridiquement responsable du respect des obligations contractuelles, notamment le paiement des primes et la déclaration sincère des risques.

### **Assuré / Insured**

Personne dont la vie, les actes ou les biens sont couverts par le contrat d'assurance. En assurance vie, il s'agit de la personne dont le décès ou la survie déclenche le versement des prestations. En assurance dommages, c'est la personne dont les biens sont protégés. En assurance de responsabilité civile, c'est la personne dont la responsabilité est garantie. L'assuré n'est pas nécessairement le souscripteur du contrat, ni le bénéficiaire, ni celui qui paie la cotisation. Il est essentiel de vérifier dans les dispositions particulières du contrat qui est désigné comme assuré, car cette qualité détermine l'application des garanties.

### **Bénéficiaire / Beneficiary**

Personne désignée pour recevoir les prestations prévues au contrat en cas de réalisation du risque assuré. Le terme est particulièrement utilisé en assurance vie pour désigner celui qui recevra le capital ou la rente au décès de l'assuré. Le bénéficiaire peut être désigné nommément, par sa qualité (conjoint, héritiers) ou par une clause type. La désignation peut être modifiée par le souscripteur sauf si elle a été acceptée par le bénéficiaire, ce qui la rend irrévocable. En assurance de responsabilité civile, le bénéficiaire est la victime du dommage qui reçoit l'indemnité.

### **Courtier / Broker**

Intermédiaire d'assurance inscrit au registre du commerce qui représente les clients et agit en leur nom. Le courtier conseille ses clients sur leurs besoins en assurance, recherche et compare les offres du marché, négocie les conditions auprès de multiples assureurs et assiste dans le règlement des sinistres. Il est mandataire de ses clients et engage sa responsabilité professionnelle en cas de faute, d'erreur ou d'omission dans l'exercice de ses fonctions. Le courtier doit disposer d'une garantie financière minimale de cent quinze mille euros et souscrire obligatoirement une assurance de responsabilité civile professionnelle. Sa rémunération provient généralement de commissions versées par les compagnies d'assurance, mais il peut également facturer des honoraires directement à ses clients. Dans l'écosystème Bamboo Assur, le courtier accède à la plateforme métier qui centralise les produits de tous les assureurs partenaires et lui fournit des outils de gestion de portefeuille, de suivi d'activité et de reporting.

### **Agent Général / General Agent**

Représentant exclusif d'une compagnie d'assurance dans un secteur géographique défini, lié par un mandat de représentation. L'agent général engage juridiquement la compagnie qu'il représente dans tous ses actes relevant de son mandat : vente de contrats, encaissement des primes, délivrance des attestations d'assurance et versement des indemnités dans les limites de ses pouvoirs. Contrairement au courtier qui représente les clients, l'agent général représente l'assureur, ce qui modifie la nature de ses obligations professionnelles. Il est rémunéré à la commission sur les affaires apportées et gérées. L'agent général dispose généralement d'une agence locale avec du personnel et assume les charges d'exploitation correspondantes. Dans certaines configurations, Bamboo Assur peut collaborer avec des agents généraux de ses assureurs partenaires pour la distribution locale de produits spécifiques.

### **Sous-Agent / Sub-Agent**

Représentant travaillant sous la responsabilité et l'autorité d'un agent général ou d'un courtier principal. Le sous-agent prospecte, distribue les produits d'assurance et assure un service de proximité auprès des clients sur un territoire donné. Il ne dispose pas de mandat direct de la compagnie d'assurance et agit toujours pour le compte de l'agent ou du courtier qui l'a mandaté. Le sous-agent perçoit généralement une quote-part de la commission versée à l'agent ou au courtier principal. Dans le contexte de Bamboo Assur, les sous-agents accèdent à une application dédiée leur permettant de réaliser des simulations, d'accompagner les souscriptions et de suivre leur activité commerciale.

### **Prestataire de Services Sanitaires / Healthcare Provider**

Établissement de santé, clinique, hôpital, laboratoire d'analyses, centre médical ou professionnel de santé (médecin, infirmier, kinésithérapeute, pharmacien) ayant établi une convention avec Bamboo Assur ou ses assureurs partenaires. Ces prestataires acceptent de dispenser des soins aux assurés selon des conditions tarifaires et des procédures de prise en charge prédéfinies. Le prestataire conventionné peut bénéficier du système de tiers payant, lui permettant d'être réglé directement par l'assureur sans que l'assuré n'ait à avancer les frais. Les prestataires accèdent au portail partenaire de Bamboo Assur pour vérifier les droits des assurés, transmettre les feuilles de soins électroniques et suivre l'état de leurs demandes de remboursement.

---

## **2\. Contrats et Produits d'Assurance**

### **Contrat d'Assurance / Insurance Contract / Policy**

Accord juridique matérialisé par la police d'assurance, établissant les engagements réciproques entre l'assureur et le souscripteur. Le contrat comprend les dispositions générales, les dispositions particulières et les éventuels avenants.

### **Police d'Assurance / Insurance Policy**

Document officiel qui matérialise le contrat d'assurance, signé par l'assureur et le souscripteur, constituant la preuve de l'accord entre les parties.

### **Dispositions Générales / General Terms and Conditions**

Clauses imprimées communes à tous les assurés pour un type de contrat donné, décrivant les garanties offertes et les conditions de validité du contrat.

### **Dispositions Particulières / Special Terms and Conditions**

Clauses personnalisées qui adaptent le contrat à la situation spécifique de chaque assuré, incluant la durée d'engagement, les coordonnées, les garanties choisies et les montants assurés. Elles prévalent sur les dispositions générales.

### **Avenant / Endorsement / Amendment**

Document complémentaire au contrat constatant officiellement les modifications apportées à celui-ci, signé par l'assureur et le souscripteur.

### **Note de Couverture / Cover Note**

Document provisoire constatant l'existence d'une garantie temporaire jusqu'à l'établissement définitif du contrat d'assurance.

### **Agrément / Authorization / License**

Autorisation administrative accordée par l'autorité de tutelle permettant à une compagnie d'assurance d'exercer son activité pour une ou plusieurs branches d'assurance déterminées.

---

## **3\. Branches d'Assurance**

### **Assurance Vie / Life Insurance**

Branche d'assurance dont le versement des prestations dépend de la survie ou du décès de l'assuré. Comprend l'assurance décès, l'assurance vie avec épargne et les produits de capitalisation.

### **Assurance Non-Vie / Non-Life Insurance / General Insurance**

Toute forme d'assurance couvrant les dommages matériels, les sinistres de responsabilité et, dans certains contextes, les accidents du travail. Également désignée sous les termes d'assurance IARD ou assurance dommages.

### **IARD / Property and Casualty Insurance**

Acronyme désignant les assurances Incendie, Accidents et Risques Divers. Couvre les dommages aux biens, la responsabilité civile et divers risques spécifiques.

### **Assurance Santé / Health Insurance**

Branche d'assurance garantissant le remboursement des frais médicaux et éventuellement le versement d'indemnités journalières en cas d'arrêt de travail pour maladie ou accident.

### **Assurance Décès / Death Insurance / Term Life Insurance**

Contrat garantissant le versement d'un capital ou d'une rente aux bénéficiaires désignés en cas de décès de l'assuré pendant la période de couverture.

### **Assurance Vie avec Épargne / Endowment Insurance**

Contrat combinant une garantie décès avec une composante d'épargne permettant la constitution d'un capital versé en cas de survie à l'échéance du contrat.

### **Assurance Responsabilité Civile / Civil Liability Insurance**

Garantie couvrant l'obligation légale de réparer les dommages causés à autrui par le fait, la négligence ou l'imprudence de l'assuré.

### **Assurance Automobile / Motor Insurance**

Contrat couvrant les risques liés à l'utilisation d'un véhicule terrestre à moteur, incluant obligatoirement la responsabilité civile et optionnellement les dommages au véhicule.

---

## **4\. Éléments Financiers**

### **Prime / Premium**

Somme payée par le souscripteur à l'assureur en contrepartie des garanties accordées. La prime peut être unique, périodique, annuelle, semestrielle, trimestrielle ou mensuelle.

### **Prime Pure / Pure Premium / Net Premium**

Élément technique de la prime correspondant au coût moyen statistique du risque couvert, calculé selon la probabilité de survenance et le montant moyen des sinistres.

### **Prime Commerciale / Commercial Premium / Gross Premium**

Prime totale comprenant la prime pure, les chargements de gestion et la marge de l'assureur.

### **Cotisation / Contribution**

Terme souvent utilisé comme synonyme de prime, désignant la somme périodique versée par l'assuré.

### **Chargements / Loadings**

Éléments ajoutés à la prime pure pour couvrir les frais de gestion de l'assureur : frais d'acquisition, d'administration, de gestion des sinistres et rémunération du capital.

### **Commission / Commission**

Rémunération versée aux intermédiaires d'assurance (agents, courtiers) pour leur activité de distribution et de gestion des contrats, généralement calculée en pourcentage des primes encaissées.

### **Franchise / Deductible / Excess**

Montant qui reste à la charge de l'assuré lors du règlement d'un sinistre. La franchise peut être absolue, relative ou proportionnelle selon les modalités définies au contrat.

### **Franchise Absolue / Absolute Deductible**

Montant systématiquement déduit de l'indemnité quel que soit le montant total des dommages.

### **Franchise Relative / Relative Deductible**

Franchise qui disparaît lorsque le montant des dommages dépasse un seuil prédéfini, l'assureur prenant alors en charge l'intégralité des dommages.

### **Capital Assuré / Sum Insured / Coverage Amount**

Montant maximum que l'assureur s'engage à verser en cas de réalisation du risque garanti.

### **Valeur Assurée / Insured Value**

Valeur déclarée des biens assurés servant de base au calcul de la prime et de l'indemnité en cas de sinistre.

### **Plafond de Garantie / Coverage Limit**

Montant maximum d'indemnisation prévu au contrat pour une garantie donnée, au-delà duquel l'assureur n'intervient plus.

---

## **5\. Garanties et Couvertures**

### **Garantie / Coverage / Guarantee**

Engagement pris par l'assureur de couvrir un risque déterminé dans les conditions et limites définies au contrat.

### **Package / Package**

Ensemble de garanties prédéfinies par Bamboo Assur constituant une offre commerciale cohérente adaptée à un besoin client spécifique.

### **Extension de Garantie / Coverage Extension**

Garantie additionnelle souscrite par l'assuré pour élargir le périmètre de couverture du contrat de base, généralement moyennant une surprime.

### **Exclusion / Exclusion**

Événement, situation ou bien expressément non couvert par le contrat d'assurance, mentionné en caractères apparents dans les conditions générales.

### **Risque Couvert / Covered Risk / Insured Peril**

Événement incertain dont la survenance entraîne la mise en jeu des garanties du contrat.

### **Tiers Payant / Third-Party Payment**

Système permettant à l'assuré de ne pas avancer les frais médicaux, l'assureur réglant directement le prestataire de soins. Principalement utilisé en assurance santé.

### **Provision Mathématique / Mathematical Reserve**

En assurance vie, montant représentant la valeur actuarielle des engagements futurs de l'assureur envers les assurés, déduction faite des primes futures à percevoir.

### **Provision pour Sinistres / Claims Reserve**

Montant estimé représentant le coût total des sinistres survenus mais non encore entièrement réglés à la date d'inventaire.

### **Provision pour Primes Non Acquises / Unearned Premium Reserve**

Fraction de prime correspondant à la période de couverture restant à courir après la date de clôture de l'exercice comptable.

---

## **6\. Gestion des Sinistres**

### **Sinistre / Claim / Loss**

Réalisation du risque garanti entraînant l'application des garanties du contrat. Le sinistre doit être déclaré dans les délais contractuels pour donner droit à indemnisation.

### **Déclaration de Sinistre / Claim Declaration / Loss Notice**

Formalité obligatoire par laquelle l'assuré informe l'assureur de la survenance d'un événement garanti, généralement dans un délai de cinq jours ouvrés (deux jours pour un vol).

### **Indemnisation / Indemnification / Compensation**

Processus de réparation du préjudice subi par l'assuré ou la victime, comprenant l'évaluation des dommages et le versement de l'indemnité.

### **Indemnité / Indemnity / Benefit**

Somme versée par l'assureur pour réparer le préjudice subi, conformément aux garanties et limites prévues au contrat.

### **Expert / Loss Adjuster / Claims Adjuster**

Professionnel mandaté pour évaluer la nature, les causes et le montant des dommages consécutifs à un sinistre.

### **Expertise / Loss Assessment / Claims Adjustment**

Opération d'évaluation des dommages réalisée par un expert, pouvant être amiable, contradictoire ou judiciaire selon les circonstances et les enjeux.

### **Règlement de Sinistre / Claim Settlement**

Processus complet de traitement d'un sinistre depuis sa déclaration jusqu'au versement de l'indemnité et à la clôture du dossier.

### **Délai de Carence / Waiting Period / Deferred Period**

Période initiale suivant la prise d'effet du contrat pendant laquelle certaines garanties ne sont pas encore applicables.

### **Préjudice / Damage / Loss / Injury**

Dommage subi par l'assuré ou un tiers, pouvant être corporel, matériel ou immatériel.

### **Dommage Corporel / Bodily Injury / Personal Injury**

Atteinte à l'intégrité physique d'une personne et ses conséquences : frais médicaux, incapacité temporaire, invalidité permanente, préjudice esthétique, souffrances morales.

### **Dommage Matériel / Property Damage / Material Damage**

Destruction, détérioration ou perte affectant des biens meubles ou immeubles.

### **Dommage Immatériel / Consequential Loss / Indirect Damage**

Préjudice financier résultant d'un dommage matériel ou corporel, tel que la privation de jouissance, l'interruption d'activité ou la perte de revenus.

### **Subrogation / Subrogation**

Mécanisme juridique par lequel l'assureur, après avoir indemnisé son assuré, se substitue à lui dans ses droits pour exercer un recours contre le responsable du sinistre.

### **Recours / Recovery / Subrogation Claim**

Action en justice ou réclamation exercée par l'assureur contre le responsable d'un sinistre pour récupérer les sommes versées à son assuré.

---

## **7\. Réassurance**

### **Réassurance / Reinsurance**

Opération par laquelle un assureur (cédante) transfère tout ou partie des risques qu'il a souscrits à un réassureur, moyennant le paiement d'une prime de réassurance.

### **Cédante / Ceding Company**

Compagnie d'assurance qui transfère une partie de ses risques à un réassureur dans le cadre d'un traité ou d'une facultative.

### **Réassureur / Reinsurer**

Société spécialisée qui accepte de prendre en charge tout ou partie des risques cédés par les compagnies d'assurance.

### **Cession / Cession**

Portion du risque transférée par l'assureur direct au réassureur dans le cadre d'un traité ou d'une opération facultative.

### **Plein de Conservation / Retention / Net Retention**

Montant maximum de risque que l'assureur conserve à sa charge pour sa propre gestion, au-delà duquel il cède à la réassurance.

### **Traité de Réassurance / Reinsurance Treaty**

Contrat par lequel le réassureur s'engage à accepter automatiquement les risques définis, et la cédante à les céder systématiquement.

### **Réassurance Proportionnelle / Proportional Reinsurance**

Forme de réassurance où primes et sinistres sont partagés selon une proportion convenue entre cédante et réassureur.

### **Réassurance Non Proportionnelle / Non-Proportional Reinsurance / Excess of Loss**

Réassurance où le réassureur intervient uniquement lorsque les sinistres dépassent un montant prédéfini, indépendamment du partage des primes.

---

## **8\. Gestion des Contrats**

### **Souscription / Underwriting / Subscription**

Processus d'analyse du risque, d'acceptation et de tarification d'une demande d'assurance conduisant à l'émission d'un contrat.

### **Proposition d'Assurance / Proposal Form / Application**

Questionnaire rempli par le futur assuré permettant à l'assureur d'évaluer le risque à garantir et de déterminer les conditions de couverture et la prime correspondante.

### **Prise d'Effet / Inception Date / Effective Date**

Date à partir de laquelle les garanties du contrat deviennent applicables et où commence la période de couverture.

### **Échéance / Renewal Date / Premium Due Date**

Date à laquelle la prime d'assurance doit être payée. L'échéance principale peut différer de la date anniversaire du contrat.

### **Date Anniversaire / Anniversary Date**

Date correspondant à l'anniversaire de la prise d'effet du contrat, importante pour les opérations de renouvellement et de résiliation.

### **Reconduction Tacite / Automatic Renewal / Tacit Renewal**

Renouvellement automatique du contrat à son échéance pour une nouvelle période, sauf résiliation expresse de l'une des parties.

### **Résiliation / Termination / Cancellation**

Cessation définitive et anticipée du contrat d'assurance avant son terme normal, pouvant intervenir à l'initiative de l'assureur ou de l'assuré selon des modalités légales et contractuelles précises.

### **Préavis de Résiliation / Notice Period**

Délai contractuel que l'assuré doit respecter pour informer l'assureur de son intention de résilier, généralement de deux mois pour les contrats des particuliers.

### **Suspension de Garantie / Suspension of Coverage**

Interruption temporaire de la couverture d'assurance, généralement consécutive au non-paiement de la prime, pendant laquelle les sinistres ne sont plus pris en charge.

### **Nullité du Contrat / Nullity / Voidness**

Sanction juridique considérant le contrat comme n'ayant jamais existé, généralement prononcée en cas de fausse déclaration intentionnelle de l'assuré.

### **Déchéance / Forfeiture**

Perte du droit à indemnisation résultant du non-respect des obligations contractuelles par l'assuré, notamment après sinistre.

---

## **9\. Éléments Techniques et Actuariels**

### **Tarification / Premium Rating / Pricing**

Processus de calcul de la prime d'assurance basé sur l'évaluation statistique du risque, les chargements de gestion et la marge technique.

### **Sinistralité / Loss Ratio / Claims Experience**

Rapport entre le montant des sinistres survenus et celui des primes encaissées sur une période donnée, exprimé en pourcentage.

### **Taux de Sinistralité / Loss Ratio**

Indicateur technique mesurant le rapport entre la charge de sinistres et les primes acquises, permettant d'évaluer la rentabilité technique d'un portefeuille.

### **Provision Technique / Technical Reserve / Technical Provision**

Ensemble des engagements évalués par l'assureur au titre des contrats en cours, représentant les sommes nécessaires pour honorer les engagements futurs envers les assurés.

### **Base Technique / Technical Basis**

Ensemble des hypothèses actuarielles (tables de mortalité, taux d'intérêt technique, chargements) servant au calcul des primes et des provisions en assurance vie.

### **Table de Mortalité / Mortality Table**

Outil statistique indiquant pour chaque âge la probabilité de décès, utilisé pour la tarification et le provisionnement en assurance vie.

### **Taux d'Intérêt Technique / Technical Interest Rate**

Taux de rendement minimal garanti aux assurés, utilisé pour actualiser les engagements futurs de l'assureur en assurance vie.

### **Valeur de Rachat / Surrender Value**

Montant que l'assureur verse au souscripteur d'un contrat d'assurance vie qui souhaite mettre fin anticipativement à son contrat et récupérer l'épargne constituée.

### **Valeur de Réduction / Reduced Paid-Up Value**

Valeur du contrat d'assurance vie maintenu en vigueur avec des garanties réduites lorsque le souscripteur cesse de payer les primes.

### **Participation aux Bénéfices / Profit Sharing / Bonus**

Redistribution aux assurés d'une partie des bénéfices techniques et financiers réalisés par l'assureur, particulièrement en assurance vie.

---

## **10\. Opérations et Processus**

### **Préfinancement / Pre-financing**

Mécanisme offert par Bamboo EMF permettant aux assurés de financer le paiement de leurs primes d'assurance selon des modalités de paiement fractionné avantageuses.

### **Paiement Fractionné / Installment Payment / Deferred Payment**

Modalité permettant à l'assuré d'échelonner le paiement de sa prime selon une périodicité journalière, mensuelle, trimestrielle, semestrielle ou annuelle.

### **Prélèvement Automatique / Direct Debit / Automatic Payment**

Mode de paiement par lequel l'assureur prélève automatiquement les primes sur le compte bancaire ou le portefeuille mobile money de l'assuré aux échéances convenues.

### **Interconnexion Bancaire / Banking Integration / Core Banking Integration**

Connexion technique entre le système d'information de Bamboo Assur et les systèmes bancaires partenaires permettant la validation des paiements, la gestion des prélèvements et des échéanciers.

### **Relance Automatisée / Automated Reminder / Payment Reminder**

Processus automatique d'envoi de notifications aux assurés pour leur rappeler les échéances de paiement ou les actions à effectuer.

### **Transfert de Portefeuille / Portfolio Transfer**

Opération par laquelle l'ensemble des contrats d'un assureur est transféré à un autre assureur, généralement dans un contexte de fusion ou de cession d'activité.

### **Migration de Données / Data Migration**

Processus technique de transfert des données d'assurance depuis d'anciens systèmes vers la plateforme Bamboo Assur.

---

## **11\. Aspects Juridiques et Réglementaires**

### **Responsabilité Civile / Civil Liability**

Obligation légale de réparer les dommages causés à autrui du fait de ses actes, de sa négligence, des personnes dont on a la charge ou des choses dont on a la garde.

### **Assurance Obligatoire / Compulsory Insurance / Mandatory Insurance**

Assurance dont la souscription est imposée par la loi pour certaines activités ou situations, notamment la responsabilité civile automobile.

### **Prescription / Prescription / Limitation Period**

Délai légal au-delà duquel l'assuré ou l'assureur ne peut plus exercer ses droits en justice. En assurance, la prescription est généralement de deux ans pour les actions contractuelles et de dix ans pour les actions en responsabilité civile.

### **Mise en Demeure / Formal Notice / Payment Demand**

Lettre recommandée par laquelle l'assureur somme l'assuré de payer la prime impayée sous peine de suspension puis de résiliation du contrat.

### **Autorité de Tutelle / Supervisory Authority / Regulatory Authority**

Organisme officiel chargé du contrôle et de la surveillance des compagnies d'assurance pour garantir leur solvabilité et la protection des assurés.

### **Conformité Réglementaire / Regulatory Compliance**

Respect par la compagnie d'assurance de l'ensemble des obligations légales et réglementaires régissant son activité.

---

## **12\. Système d'Information**

### **Portail Client / Customer Portal**

Interface web et mobile permettant aux assurés de consulter leurs contrats, effectuer des souscriptions, déclarer des sinistres et gérer leurs paiements.

### **Portail Partenaire / Partner Portal**

Interface dédiée aux prestataires de services sanitaires et aux compagnies d'assurance partenaires pour le suivi des demandes, la gestion des remboursements et l'échange de documents.

### **Interface Back-Office / Back-Office Interface**

Application métier destinée aux courtiers et gestionnaires de Bamboo Assur pour la gestion opérationnelle des contrats, sinistres et relations clients.

### **API d'Intégration / Integration API / Web Service**

Interface de programmation permettant l'interconnexion du système Bamboo Assur avec les systèmes d'information des assureurs partenaires, des banques et des prestataires externes.

### **Signature Électronique / Electronic Signature / Digital Signature**

Procédé technique permettant l'authentification et la validation juridique des contrats d'assurance de manière dématérialisée.

### **Authentification QR Code / QR Code Authentication**

Système de validation et d'authentification des utilisateurs et des documents par la génération et la lecture de codes QR sécurisés.

### **Notification Push / Push Notification**

Message automatique envoyé via l'application mobile pour informer l'assuré d'événements importants : échéances, mise à jour de dossier, validation de sinistre.

### **Reporting / Reporting / Analytics**

Ensemble des états statistiques et financiers générés par le système permettant le pilotage de l'activité et l'analyse de performance.

### **KPI / Key Performance Indicator**

Indicateur de performance permettant de mesurer l'efficacité des activités de courtage et la santé du portefeuille d'assurances.

---

## **13\. Concepts DDD et Architecture**

### **Contexte Délimité / Bounded Context**

Périmètre fonctionnel au sein duquel un modèle de domaine particulier est défini et applicable. Bamboo Assur distingue notamment les contextes Vie, Non-Vie et Santé.

### **Agrégat / Aggregate**

Groupe d'objets métier traités comme une unité cohérente pour les modifications de données. Exemples : Contrat, Sinistre, Client.

### **Entité / Entity**

Objet métier possédant une identité unique et une continuité dans le temps. Exemples : Police d'Assurance, Assuré, Sinistre.

### **Objet-Valeur / Value Object**

Objet métier défini uniquement par ses attributs, sans identité propre. Exemples : Adresse, Montant de Prime, Période de Couverture.

### **Événement Métier / Domain Event**

Fait significatif survenu dans le domaine métier nécessitant d'être enregistré et potentiellement de déclencher des actions. Exemples : ContratSouscrit, SinistreDeclaré, PrimePayée.

### **Service Métier / Domain Service**

Opération métier qui ne relève naturellement ni d'une entité ni d'un objet-valeur. Exemples : CalculTarification, EvaluationRisque, ProcessusIndemnisation.

### **Référentiel / Repository**

Mécanisme d'accès aux agrégats permettant de les récupérer et de les persister de manière cohérente.

---

## **14\. Aspects Commerciaux et Distribution**

### **Simulation / Quote Simulation / Premium Calculator**

Outil permettant au prospect d'obtenir une estimation du coût de son assurance selon les garanties souhaitées avant toute souscription.

### **Devis / Quote / Proposal**

Document détaillant les garanties proposées et leur coût, établi suite à une demande de l'intéressé, valable pendant une durée limitée.

### **Souscription en Ligne / Online Subscription / Digital Enrollment**

Processus de souscription entièrement dématérialisé permettant de souscrire un contrat via l'interface web ou mobile.

### **Livraison de Contrat / Policy Delivery**

Processus de remise du contrat d'assurance au souscripteur, pouvant être effectué en version papier ou par téléchargement numérique.

### **Acquisition Client / Customer Acquisition**

Processus d'obtention de nouveaux assurés par les actions commerciales et marketing de Bamboo Assur.

## **15\. Conformité et Sécurité**

### **APDPVP \- L'Autorité pour la Protection des Données Personnelles et de la Vie Privée**

Autorité gabonaise de protection des données personnelles et de la vie privée, dont les exigences doivent être respectées par Bamboo Assur. [Site web](https://www.apdpvp.ga)

### **Protection des Données / Data Protection / Privacy**

Ensemble des mesures techniques et organisationnelles visant à garantir la confidentialité et la sécurité des informations personnelles des assurés.

### **Chiffrement / Encryption**

Technique de sécurisation des données par codage, appliquée notamment pour protéger les informations sensibles et les transactions.

### **SSL / Secure Sockets Layer**

Protocole de sécurisation des échanges sur Internet garantissant la confidentialité des communications entre le client et les serveurs.

### **AES / Advanced Encryption Standard**

Algorithme de chiffrement utilisé pour sécuriser les données sensibles stockées dans le système d'information.

### **MFA / Multi-Factor Authentication**

Système d'authentification forte requérant plusieurs preuves d'identité pour accéder aux espaces sécurisés.

### **OAuth / Open Authorization**

Protocole d'autorisation permettant une authentification sécurisée et la délégation d'accès entre applications.

### **SSO / Single Sign-On**

Système permettant à un utilisateur de s'authentifier une seule fois pour accéder à plusieurs applications interconnectées.

### **Audit Trail / Audit Log / Activity Log**

Journal détaillé de toutes les actions effectuées dans le système, essentiel pour la traçabilité et la conformité réglementaire.

### **Anonymisation / Anonymization**

Processus de suppression ou de transformation des données personnelles pour empêcher l'identification des individus.

## 

## **Annexe A : Relations entre Concepts Clés**

### **Relation Contrat-Parties**

Un Contrat d'Assurance lie juridiquement un Assureur à un Souscripteur. Le Souscripteur peut être différent de l'Assuré, qui est la personne effectivement protégée. En cas de sinistre, c'est le Bénéficiaire désigné qui perçoit l'indemnité.

### **Cycle de Vie d'un Contrat**

Le cycle débute par une Simulation, suivie d'un Devis, puis d'une Souscription formalisée par une Police. Le contrat entre en vigueur à la Prise d'Effet et se renouvelle par Tacite Reconduction sauf Résiliation. Durant sa vie, le contrat peut faire l'objet d'Avenants pour modifications.

### **Processus de Sinistre**

Un Sinistre commence par une Déclaration dans les délais légaux. L'assureur mandate un Expert pour évaluer les dommages. Après Expertise, le montant de l'Indemnité est calculé en tenant compte des Franchises et Plafonds. Le Règlement intervient après validation du dossier.

### **Hiérarchie des Garanties**

Une Branche d'Assurance regroupe plusieurs Garanties formant un Package commercial. Chaque Garantie possède un Capital Assuré, un Plafond et éventuellement une Franchise. Les Exclusions délimitent le périmètre de couverture.

---

## **Annexe B : Schémas de Calcul**

### **Calcul de Prime**

La Prime Commerciale se compose de la Prime Pure additionnée des Chargements et taxes. La Prime Pure résulte du calcul actuariel basé sur la sinistralité historique et les probabilités de réalisation du risque.

### **Calcul d'Indemnité**

L'Indemnité versée correspond au montant des Dommages évalués, plafonné au Capital Assuré, diminué de la Franchise applicable et des éventuelles franchises proportionnelles.

### **Répartition en Réassurance**

Le montant total du risque se répartit entre le Plein de Conservation gardé par l'assureur et la Cession transférée au Réassureur selon les termes du Traité.

## 

## **Notes d'Utilisation**

Ce glossaire constitue le langage de référence pour l'ensemble du projet Bamboo Assur. Tous les documents de spécification, le code source, les interfaces utilisateur et les échanges entre équipes doivent utiliser ces termes de manière cohérente.

Les termes peuvent évoluer avec la compréhension approfondie du domaine métier. Toute modification doit être validée collectivement et documentée dans une nouvelle version du glossaire.

Pour toute ambiguïté ou terme manquant, contacter l'équipe d'architecture métier pour enrichissement du présent document.