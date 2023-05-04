package h08;

import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;

public class H08_RubricProvider implements RubricProvider {

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H08")
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}
