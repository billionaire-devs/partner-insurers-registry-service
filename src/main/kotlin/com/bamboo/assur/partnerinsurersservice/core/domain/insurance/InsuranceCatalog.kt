package com.bamboo.assur.partnerinsurersservice.core.domain.insurance

/**
 * A catalog of predefined insurance branches, categorized by their families.
 */
object InsuranceCatalog {

    val branches = setOf(
        // Famille : Assurances de personnes
        // Regroupe les assurances liées aux individus, couvrant les risques de santé, vie, décès, et retraite.
        InsuranceBranch(
            "SANTE",
            "Assurance santé",
            "Remboursement des soins, hospitalisation, prestations médicales.",
            InsuranceFamily.PERSONS
        ),
        InsuranceBranch(
            "VIE",
            "Assurance vie",
            "Épargne et capital en cas de décès ou survie.",
            InsuranceFamily.PERSONS
        ),
        InsuranceBranch(
            "DECES",
            "Décès / Invalidité",
            "Garantie financière en cas de décès ou invalidité.",
            InsuranceFamily.PERSONS
        ),
        InsuranceBranch(
            "RETRAITE",
            "Retraite complémentaire",
            "Constitution d’un capital ou d’une rente à la retraite.",
            InsuranceFamily.PERSONS
        ),
        InsuranceBranch(
            "ACCIDENT",
            "Individuelle accident",
            "Couverture des accidents corporels.",
            InsuranceFamily.PERSONS
        ),
        InsuranceBranch(
            "VOYAGE",
            "Assistance / Voyage",
            "Couverture des risques liés aux déplacements.",
            InsuranceFamily.PERSONS
        ),



        // Famille : Assurances de biens
        // Protège les biens matériels contre divers risques tels que l'incendie, le vol, et les dommages matériels.
        InsuranceBranch(
            "AUTO",
            "Assurance automobile",
            "RC auto, dommages, vol, incendie, tous risques.",
            InsuranceFamily.PROPERTY
        ),
        InsuranceBranch(
            "HABITATION",
            "Assurance habitation",
            "Multirisque habitation, incendie, vol, dégâts des eaux.",
            InsuranceFamily.PROPERTY
        ),
        InsuranceBranch(
            "ENTREPRISE",
            "Multirisque professionnelle",
            "Protection des locaux, machines et équipements.",
            InsuranceFamily.PROPERTY
        ),
        InsuranceBranch(
            "INFORMATIQUE",
            "Assurance informatique",
            "Protection du matériel et des données informatiques.",
            InsuranceFamily.PROPERTY
        ),
        InsuranceBranch(
            "INCENDIE",
            "Assurance incendie",
            "Protection des bâtiments et entrepôts contre le feu.",
            InsuranceFamily.PROPERTY
        ),



        // Famille : Responsabilité
        // Couvre les dommages corporels, matériels ou immatériels causés à des tiers,
        // que ce soit dans le cadre privé ou professionnel.
        InsuranceBranch(
            "RC_AUTO",
            "Responsabilité automobile",
            "Couvre les dommages causés à autrui par un véhicule.",
            InsuranceFamily.LIABILITY
        ),
        InsuranceBranch(
            "RC_PRO",
            "Responsabilité professionnelle",
            "Protège contre les erreurs ou fautes professionnelles.",
            InsuranceFamily.LIABILITY
        ),
        InsuranceBranch(
            "RC_GEN",
            "Responsabilité générale",
            "Couvre les dommages causés dans la vie privée.",
            InsuranceFamily.LIABILITY
        ),



        // Famille : Financières
        // Couvre les pertes pécuniaires et les risques économiques,
        // incluant le crédit, la caution, et les pertes d’exploitation.
        InsuranceBranch(
            "CAUTION",
            "Assurance caution",
            "Garanties financières et engagements de caution.",
            InsuranceFamily.FINANCIAL
        ),
        InsuranceBranch(
            "CREDIT",
            "Assurance crédit",
            "Couvre les impayés et défauts de paiement.",
            InsuranceFamily.FINANCIAL
        ),
        InsuranceBranch(
            "PERTE_EXPLOI",
            "Perte d’exploitation",
            "Couvre les pertes financières après un sinistre.",
            InsuranceFamily.FINANCIAL
        ),



        // Famille : Transports
        // Couvre les risques liés au transport de marchandises ou de personnes,
        // par voie terrestre, maritime ou aérienne.
        InsuranceBranch(
            "MARITIME",
            "Transport maritime",
            "Assure navires, cargaisons, risques maritimes.",
            InsuranceFamily.TRANSPORT
        ),
        InsuranceBranch(
            "AERIEN",
            "Transport aérien",
            "Assure cargaisons et appareils aériens.",
            InsuranceFamily.TRANSPORT
        ),
        InsuranceBranch(
            "TERRESTRE",
            "Transport terrestre",
            "Assure marchandises routières et ferroviaires.",
            InsuranceFamily.TRANSPORT
        ),



        // Famille : Agricole
        // Couvre les risques spécifiques à l'agriculture, l'élevage, les récoltes et le matériel agricole.
        InsuranceBranch(
            "CULTURE",
            "Assurance récoltes",
            "Couvre les pertes de production agricole (grêle, sécheresse).",
            InsuranceFamily.AGRICULTURE
        ),
        InsuranceBranch(
            "ELEVAGE",
            "Assurance bétail",
            "Protège le bétail contre la mortalité et les maladies.",
            InsuranceFamily.AGRICULTURE
        ),
        InsuranceBranch(
            "EQUIPEMENT",
            "Assurance matériel agricole",
            "Couvre les équipements et machines agricoles.",
            InsuranceFamily.AGRICULTURE
        ),



        // Famille : Spéciales
        // Couvre des risques techniques, environnementaux, cybernétiques, politiques ou autres risques non standards.
        InsuranceBranch(
            "CYBER",
            "Assurance cyber",
            "Protection contre les attaques et pertes de données.",
            InsuranceFamily.SPECIAL
        ),
        InsuranceBranch(
            "POLITIQUE",
            "Assurance risques politiques",
            "Couvre instabilité, confiscation, guerre.",
            InsuranceFamily.SPECIAL
        ),
        InsuranceBranch(
            "EVENEMENT",
            "Assurance événementiel",
            "Couvre annulation ou interruption d’événements.",
            InsuranceFamily.SPECIAL
        ),
        InsuranceBranch(
            "ENVIRON",
            "Assurance environnementale",
            "Couvre pollution et dommages écologiques.",
            InsuranceFamily.SPECIAL
        )
    )

    /**
     * Finds an insurance branch by its unique code.
     * The search is case-insensitive.
     *
     * @param code The code of the insurance branch to find (e.g., "AUTO").
     * @return The [InsuranceBranch] if found, otherwise `null`.
     */
    fun findByCode(code: String): InsuranceBranch? =
        branches.find { it.code.equals(code, ignoreCase = true) }

    /**
     * Lists all insurance branches belonging to a specific insurance family.
     *
     * @param family The [InsuranceFamily] to filter by.
     * @return A [List] of [InsuranceBranch] objects that belong to the specified family.
     *         Returns an empty list if no branches are found for the given family.
     */
    fun findByFamily(family: InsuranceFamily): List<InsuranceBranch> =
        branches.filter { it.family == family }
}