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
        return createCriterion(shortDescription, 0, 1, methodReferences);
    }

    @SafeVarargs
    private static Criterion createCriterion(String shortDescription, int minPoints, int maxPoints, Callable<Method>... methodReferences) {
        return createCriterion(shortDescription, minPoints, maxPoints, Arrays.stream(methodReferences).map(JUnitTestRef::ofMethod).toArray(JUnitTestRef[]::new));
    }

    private static Criterion createCriterion(String shortDescription, int minPoints, int maxPoints, JUnitTestRef... testReferences) {

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
            .minPoints(minPoints)
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
            .minPoints(0)
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

    public static final Criterion H2_2_2 = createCriterion("Die Methode [[[add(Customer)]]] der Klasse [[[Bank]]] erstellt korrekt einen neuen Account und fügt diesen korrekt der Bank hinzu.",
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

    public static final Criterion H3 = createParentCriterion("3", "Eigene Fehlermeldungen",
        H3_1, H3_2, H3_3, H3_4);

    public static final Criterion H4_1_1 = createCriterion("Der Konstruktor der Klasse [[[Account]]] behandelt ungültige Eingaben korrekt.",
        () -> H4_1_Test.class.getDeclaredMethod("testAgeRestriction", JsonParameterSet.class),
        () -> H4_1_Test.class.getDeclaredMethod("testAsserts"));

    public static final Criterion H4_1 = createParentCriterion("4.1", "Kundenvalidierung",
        H4_1_1);

    public static final Criterion H4_2_1 = createCriterion("Der Konstruktor der Klasse [[[Transaction]]] behandelt ungültige Eingaben korrekt.",
        () -> H4_2_Test.class.getDeclaredMethod("testDateRestriction", JsonParameterSet.class),
        () -> H4_2_Test.class.getDeclaredMethod("testAsserts"));

    public static final Criterion H4_2 = createParentCriterion("4.1", "Transaktionsvalidierung",
        H4_2_1);

    public static final Criterion H4 = createParentCriterion("4", "Validierung von Daten",
        H4_1, H4_2);

    public static final Criterion H5_1_1 = createCriterion("Die Methode [[[TransactionHistory#add(Transaction)]]] funktioniert korrekt wenn die Transaktionsnummer bereits in der Historie vorhanden ist.",
        () -> H5_1_Test.class.getDeclaredMethod("testException", JsonParameterSet.class));

    public static final Criterion H5_1_2 = createCriterion("Die Methode [[[TransactionHistory#add(Transaction)]]] erhöht korrekt die size der Historie.",
        () -> H5_1_Test.class.getDeclaredMethod("testSize", JsonParameterSet.class));

    public static final Criterion H5_1_3 = createCriterion("Die Methode [[[TransactionHistory#add(Transaction)]]] funktioniert für einfache Fälle vollständig korrekt.",
        () -> H5_1_Test.class.getDeclaredMethod("testSimple", JsonParameterSet.class));

    public static final Criterion H5_1_4 = createCriterion("Die Methode [[[TransactionHistory#add(Transaction)]]] funktioniert für komplexe Fälle vollständig korrekt.",
        () -> H5_1_Test.class.getDeclaredMethod("testComplex", JsonParameterSet.class));

    public static final Criterion H5_1 = createParentCriterion("5.1", "Transaktionen in die Historie aufnehmen",
        H5_1_1, H5_1_2, H5_1_3, H5_1_4);

    public static final Criterion H5_2_1 = createCriterion("Die Methode [[[TransactionHistory#update(Transaction)]]] funktioniert korrekt wenn die Transaktionsnummer in der Historie vorhanden ist.",
        () -> H5_2_Test.class.getDeclaredMethod("testNormal", JsonParameterSet.class));

    public static final Criterion H5_2_2 = createCriterion("Die Methode [[[TransactionHistory#update(Transaction)]]] funktioniert korrekt wenn die Transaktionsnummer nicht in der Historie vorhanden ist.",
        () -> H5_2_Test.class.getDeclaredMethod("testException", JsonParameterSet.class));

    public static final Criterion H5_2 = createParentCriterion("5.2", "Transaktion aktualisieren",
        H5_2_1, H5_2_2);

    public static final Criterion H5_3_1 = createCriterion("Die Methode [[[Bank#transfer]]] funktioniert korrekt wenn der Sender oder Empfänger nicht gefunden werden kann.",
        () -> H5_3_Test.class.getDeclaredMethod("testInvalidSender", JsonParameterSet.class),
        () -> H5_3_Test.class.getDeclaredMethod("testInvalidReceiver", JsonParameterSet.class),
        () -> H5_3_Test.class.getDeclaredMethod("testInvalidBic", JsonParameterSet.class));

    public static final Criterion H5_3_2 = createCriterion("Die Methode [[[Bank#transfer]]] gibt als Status CANCELLED zurück, wenn bei [[[withdraw]]] oder [[[deposit]]] eine Exception geworfen wird.",
        () -> H5_3_Test.class.getDeclaredMethod("testWithdrawExceptionReturnStatus", JsonParameterSet.class),
        () -> H5_3_Test.class.getDeclaredMethod("testDepositExceptionReturnStatus", JsonParameterSet.class));

    public static final Criterion H5_3_3 = createCriterion("Die Methode [[[Bank#transfer]]] aktualisiert die Transaktion in der Historie korrekt, wenn bei [[[withdraw]]] oder [[[deposit]]] eine Exception geworfen wird.",
        () -> H5_3_Test.class.getDeclaredMethod("testWithdrawExceptionHistory", JsonParameterSet.class),
        () -> H5_3_Test.class.getDeclaredMethod("testDepositExceptionHistory", JsonParameterSet.class));

    public static final Criterion H5_3_4 = createCriterion("Die Methode [[[Bank#transfer]]] gibt als Status CLOSED zurück, wenn bei [[[withdraw]]] und [[[deposit]]] keine Exception geworfen wird.",
        () -> H5_3_Test.class.getDeclaredMethod("testSuccessReturnStatus", JsonParameterSet.class));

    public static final Criterion H5_3_5 = createCriterion("Die Methode [[[Bank#transfer]]] aktualisiert die Transaktion in der Historie korrekt, wenn bei [[[withdraw]]] und [[[deposit]]] keine Exception geworfen wird.",
        () -> H5_3_Test.class.getDeclaredMethod("testSuccessHistory", JsonParameterSet.class));

    public static final Criterion H5_3_6 = createCriterion("Die throws Klausel der Methode [[[Bank#transfer]]] ist leer (außer optionale RuntimeExceptions).", -1, 0,
        () -> H5_3_Test.class.getDeclaredMethod("testThrowsClause"));

    public static final Criterion H5_3 = createParentCriterion("5.3", "Überweisung tätigen",
        H5_3_1, H5_3_2, H5_3_3, H5_3_4, H5_3_5, H5_3_6);

    public static final Criterion H5_4_1 = createCriterion("Die Methode [[[Bank#checkOpenTransactions()]]] gibt die korrekte Anzahl an Transaktionen zurück.",
        () -> H5_4_Test.class.getDeclaredMethod("testReturnArraySize", JsonParameterSet.class));

    public static final Criterion H5_4_2 = createCriterion("Die Methode [[[Bank#checkOpenTransactions()]]] setzt den Status von offenen Transaktionen, die älter als 2 Wochen sind auf cancelled.",
        () -> H5_4_Test.class.getDeclaredMethod("testOldTransactionsCancelled", JsonParameterSet.class));

    public static final Criterion H5_4_3 = createCriterion("Die Methode [[[Bank#checkOpenTransactions()]]] führt offene Transaktionen, die zwischen 2 und 4 Wochen alt sind, erneut durch.",
        () -> H5_4_Test.class.getDeclaredMethod("testOldTransactionsTransferredAgain", JsonParameterSet.class));

    public static final Criterion H5_4_4 = createCriterion("Die Methode [[[Bank#checkOpenTransactions()]]] gibt die korrekten Transaktionen zurück.",
        () -> H5_4_Test.class.getDeclaredMethod("testReturnArrayContent", JsonParameterSet.class));

    public static final Criterion H5_4_5 = createCriterion("Die Methode [[[Bank#checkOpenTransactions()]]] wirft eine Exception, wenn es offene Transaktionen gibt, die älter als 4 Wochen sind.",
        () -> H5_4_Test.class.getDeclaredMethod("testOldTransactionsExceptionThrown", JsonParameterSet.class));

    public static final Criterion H5_4 = createParentCriterion("5.4", "Offene Überweisungen",
        H5_4_1, H5_4_2, H5_4_3, H5_4_4, H5_4_5);

    public static final Criterion H5 = createParentCriterion("5", "Überweisungen",
        H5_1, H5_2, H5_3, H5_4);

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H08")
        .addChildCriteria(H1, H2, H3, H4, H5)
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
