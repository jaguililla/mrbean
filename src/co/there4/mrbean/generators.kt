package co.there4.mrbean

class GenerateEquals : GenerateAction("Generate equals", "Select fields for equals", "equals")
class GenerateHashCode : GenerateAction("Generate hashCode", "Select fields for hashCode", "hashCode")
class GenerateToString : GenerateAction("Generate toString", "Select fields for toString", "toString")
class GenerateSet : GenerateManyAction("Generate set", "Select fields to generate set", "set")
class GenerateGet : GenerateManyAction("Generate get", "Select fields to generate get", "get")
class GenerateWith : GenerateManyAction("Generate with", "Select fields to generate with", "with")

class GenerateEqualsAndHashCode : GenerateCompoundAction(
    "Generate equals and hashCode",
    "Select fields for equals and hashCode",
    GenerateEquals(),
    GenerateHashCode()
)

/**
 * TODO Generate constructor with final fields (use default IntelliJ action).
 */
class GenerateBean : GenerateCompoundAction(
    "Generate bean",
    "Select fields for bean",
    GenerateEqualsAndHashCode(),
    GenerateToString(),
    GenerateWith()
)

/**
 * TODO Generate constructor with final fields (use default IntelliJ action).
 */
class GenerateImmutableBean : GenerateCompoundAction(
    "Generate bean",
    "Select fields for bean",
    GenerateEqualsAndHashCode(),
    GenerateToString(),
    GenerateWith()
)

