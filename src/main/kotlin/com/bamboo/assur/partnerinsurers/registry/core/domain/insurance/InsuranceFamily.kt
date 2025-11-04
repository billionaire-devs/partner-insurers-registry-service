package com.bamboo.assur.partnerinsurers.registry.core.domain.insurance

/**
 * Represents the main families of insurance, inspired by the CIMA Code and Solvency II standards.
 *
 * Each family groups several insurance branches.
 *
 * @property code Short code identifying the insurance family.
 * @property label Human-readable label for the insurance family.
 * @property description Detailed description of the insurance family.
 */
@Suppress("MaxLineLength")
enum class InsuranceFamily(
    val code: String,
    val label: String,
    val description: String
) {
    PERSONS(
        code = "PER",
        label = "Assurances de personnes",
        description = "Regroupe les assurances liées aux individus, couvrant les risques de santé, vie, décès, et retraite."
    ),
    PROPERTY(
        code = "BIEN",
        label = "Assurances de biens",
        description = "Protège les biens matériels contre divers risques tels que l'incendie, le vol, et les dommages matériels."
    ),
    LIABILITY(
        code = "RESP",
        label = "Assurances de responsabilité",
        description = "Couvre les dommages corporels, matériels ou immatériels causés à des tiers, que ce soit dans le cadre privé ou professionnel."
    ),
    FINANCIAL(
        code = "FIN",
        label = "Assurances financières",
        description = "Couvre les pertes pécuniaires et les risques économiques, incluant le crédit, la caution, et les pertes d’exploitation."
    ),
    TRANSPORT(
        code = "TRANSP",
        label = "Assurances de transport",
        description = "Couvre les risques liés au transport de marchandises ou de personnes, par voie terrestre, maritime ou aérienne."
    ),
    AGRICULTURE(
        code = "AGRI",
        label = "Assurances agricoles",
        description = "Couvre les risques spécifiques à l'agriculture, l'élevage, les récoltes et le matériel agricole."
    ),
    SPECIAL(
        code = "SPEC",
        label = "Assurances spéciales",
        description = "Couvre des risques techniques, environnementaux, cybernétiques, politiques ou autres risques non standards."
    );
}