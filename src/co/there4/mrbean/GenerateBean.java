package co.there4.mrbean;

/**
 * TODO Generate constructor with final fields (use default IntelliJ action).
 */
public final class GenerateBean extends GenerateCompoundAction {
    public GenerateBean () {
        super ("Generate bean", "Select fields for bean",
            new GenerateEqualsAndHashCode (),
            new GenerateToString (),
            new GenerateWith ());
    }
}
