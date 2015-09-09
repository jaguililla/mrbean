package co.there4.mrbean;

public final class GenerateEqualsAndHashCode extends GenerateCompoundAction {
    public GenerateEqualsAndHashCode () {
        super ("Generate equals and hashCode", "Select fields for equals and hashCode",
            new GenerateEquals (),
            new GenerateHashCode ());
    }
}
