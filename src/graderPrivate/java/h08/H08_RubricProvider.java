package h08;

import h08.transformer.SystemNanoTimeTransformer;
import org.sourcegrade.jagr.api.rubric.*;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class H08_RubricProvider implements RubricProvider {

    @SafeVarargs
    private static Criterion createCriterion(String shortDescription, Callable<Method>... methodReferences) {

        return createCriterion(shortDescription, 1, Arrays.stream(methodReferences).map(JUnitTestRef::ofMethod).toArray(JUnitTestRef[]::new));
    }

    private static Criterion createCriterion(String shortDescription, int maxPoints, JUnitTestRef... testReferences) {

        if (testReferences.length == 0) {
            return Criterion.builder()
                .shortDescription(shortDescription)
                .maxPoints(1)
                .build();
        }

        Grader.TestAwareBuilder graderBuilder = Grader.testAwareBuilder();

        for (JUnitTestRef reference : testReferences) {
            graderBuilder.requirePass(reference);
        }

        return Criterion.builder()
            .shortDescription(shortDescription)
            .minPoints(0)
            .grader(graderBuilder
                .pointsFailedMin()
                .pointsPassedMax()
                .build())
            .maxPoints(maxPoints)
            .build();
    }

    private static Criterion createParentCriterion(String task, String shortDescription, Criterion... children) {
        return Criterion.builder()
            .shortDescription("H" + task + " | " + shortDescription)
            .addChildCriteria(children)
            .build();
    }

    public static final Criterion H1_1 = createCriterion("Der Konstruktor des Records [[[Customer]]] funktioniert vollständig korrekt.",
        () -> H1_Test.class.getDeclaredMethod("test"));

    public static final Criterion H1 = createParentCriterion("1", "Ich möchte ein Kunde sein!",
        H1_1);

    public static final Criterion H2_1_1 = createCriterion("Die Methode [[[isIbanAlreadyUsed(long)]]] der Klasse [[[Bank]]] funktioniert korrekt, wenn die IBAN verwendet wird.",
        () -> H2_1_Test.class.getDeclaredMethod("testContains", JsonParameterSet.class));

    public static final Criterion H2_1_2 = createCriterion("Die Methode [[[isIbanAlreadyUsed(long)]]] der Klasse [[[Bank]]] funktioniert korrekt, wenn die IBAN nicht verwendet wird.",
        () -> H2_1_Test.class.getDeclaredMethod("testContainsNot", JsonParameterSet.class));

    public static final Criterion H2_1 = createParentCriterion("2.1", "Ist die IBAN schon in Verwendung?",
        H2_1_1, H2_1_2);

    public static final Criterion H2_2_1 = createCriterion("Die Methode [[[generateIban(Customer, long)]]] der Klasse [[[Bank]]] gibt die korrekte IBAN zurück.",
        () -> H2_2_Test.class.getDeclaredMethod("testGenerateIbanUnusedIban", JsonParameterSet.class),
        () -> H2_2_Test.class.getDeclaredMethod("testGenerateIbanUsedIban", JsonParameterSet.class));

    public static final Criterion H2_2_2 = createCriterion("Die Methode [[[add(Customer)]]] der Klasse [[[Bank]]] fügt erstellt korrekt einen neuen Account und fügt diesen korrekt der Bank hinzu.",
        () -> H2_2_Test.class.getDeclaredMethod("testAccountCreation", JsonParameterSet.class));

    public static final Criterion H2_2_3 = createCriterion("Die Methode [[[add(Customer)]]] der Klasse [[[Bank]]] funktioniert korrekt, wenn die Bank bereits voll ist.",
        () -> H2_2_Test.class.getDeclaredMethod("testException", JsonParameterSet.class));

    public static final Criterion H2_2 = createParentCriterion("2.2", "Ich möchte gerne einen Konto eröffnen!",
        H2_2_1, H2_2_2, H2_2_3);

    public static final Criterion H2_3_1 = createCriterion("Die Methode [[[remove(long)]]] der Klasse [[[Bank]]] funktioniert korrekt, wenn eine gültige, verwendete IBAN übergeben wird.",
        () -> H2_3_Test.class.getDeclaredMethod("testNormal", JsonParameterSet.class));

    public static final Criterion H2_3_2 = createCriterion("Die Methode [[[remove(long)]]] der Klasse [[[Bank]]] funktioniert korrekt, wenn eine ungültige oder nicht verwendete IBAN übergeben wird.",
        () -> H2_3_Test.class.getDeclaredMethod("testException", JsonParameterSet.class));

    public static final Criterion H2_3 = createParentCriterion("2.3", "Kundenkonto entfernen",
        H2_3_1, H2_3_2);

    public static final Criterion H2_4_1 = createCriterion("Die Methode [[[withdraw(long, double)]]] der Klasse [[[Bank]]] funktioniert vollständig korrekt.",
        () -> H2_4_Test.class.getDeclaredMethod("testWithdrawNormal", JsonParameterSet.class),
        () -> H2_4_Test.class.getDeclaredMethod("testWithdrawException", JsonParameterSet.class));

    public static final Criterion H2_4_2 = createCriterion("Die Methode [[[deposit(long, double)]]] der Klasse [[[Bank]]] funktioniert vollständig korrekt.",
        () -> H2_4_Test.class.getDeclaredMethod("testDepositNormal", JsonParameterSet.class),
        () -> H2_4_Test.class.getDeclaredMethod("testDepositException", JsonParameterSet.class));

    public static final Criterion H2_4 = createParentCriterion("2.4", "Geld abheben und einzahlen",
        H2_4_1, H2_4_2);

    public static final Criterion H2 = createParentCriterion("2", "Bank und Kunde", H2_1, H2_2, H2_3, H2_4);

    public static final Criterion H3_1 = createCriterion("Die Klasse [[[BadTimestampException]]] ist vollständig korrekt.",
        () -> H3_Test.class.getDeclaredMethod("testBadTimestampExceptionDeclaration"),
        () -> H3_Test.class.getDeclaredMethod("testBadTimestampConstructor"));

    public static final Criterion H3_2 = createCriterion("Die Klasse [[[BankException]]] ist vollständig korrekt.",
        () -> H3_Test.class.getDeclaredMethod("testBankExceptionDeclaration"),
        () -> H3_Test.class.getDeclaredMethod("testBankExceptionStringConstructor"),
        () -> H3_Test.class.getDeclaredMethod("testBankExceptionLongConstructor"));

    public static final Criterion H3_3 = createCriterion("Der Konstruktor [[[TransactionException(String, long)]]] ist vollständig korrekt.",
        () -> H3_Test.class.getDeclaredMethod("testTransactionExceptionStringConstructorDeclaration"),
        () -> H3_Test.class.getDeclaredMethod("testTransactionExceptionStringConstructor"));

    public static final Criterion H3_4 = createCriterion("Der Konstruktor [[[TransactionException(Transaction[])]]] ist vollständig korrekt.",
        () -> H3_Test.class.getDeclaredMethod("testTransactionExceptionTransactionConstructorDeclaration"),
        () -> H3_Test.class.getDeclaredMethod("testTransactionExceptionTransactionConstructor", JsonParameterSet.class));

    public static final Criterion H3 = createParentCriterion("3.1", "Eigene Fehlermeldungen",
        H3_1, H3_2, H3_3, H3_4);

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H08")
        .addChildCriteria(H1, H2, H3)
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }

    @Override
    public void configure(RubricConfiguration configuration) {
        configuration.addTransformer(new SystemNanoTimeTransformer());
    }
}
