package h08.util;

import com.google.common.base.Suppliers;
import h08.Transaction;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions3;
import org.tudalgo.algoutils.tutor.general.match.*;
import org.tudalgo.algoutils.tutor.general.reflections.*;
import org.tudalgo.algoutils.tutor.general.stringify.HTML;

import java.time.LocalDate;
import java.util.function.Supplier;

import static h08.TestConstants.MINIMUM_SIMILARITY;
import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.fail;

public class StudentLinks {

    public static Supplier<TypeLink> createTypeLink(String name) {
        return createTypeLink(name, false);
    }

    public static Supplier<TypeLink> createTypeLink(String name, boolean exact) {
        return Suppliers.memoize(() -> {
            if (exact) {
                try {
                    return Assertions3.assertTypeExists(PACKAGE_LINK, Matcher.of(type -> type.identifier().equals(name), name));
                } catch (AssertionFailedError  e) {
                    return fail("Could not find a class named %s. (Exact match required)".formatted(name));
                }
            }
            try {
                return Assertions3.assertTypeExists(PACKAGE_LINK, similarityMatcher(name));
            } catch (AssertionFailedError e) {
                return fail("Could not find a class named %s. (%d%% similarity match required)".formatted(name, (int) (MINIMUM_SIMILARITY * 100)));
            }
        });
    }

    public static Supplier<ConstructorLink> createConstructorLink(Supplier<TypeLink> tl, Matcher<ConstructorLink> matcher) {
        return Suppliers.memoize(() -> Assertions3.assertConstructorExists(tl.get(), matcher));
    }

    public static Supplier<ConstructorLink> createConstructorLink(Supplier<TypeLink> tl, TypeLink... args) {
        return createConstructorLink(tl, BasicReflectionMatchers.sameTypes(args));
    }

    public static MethodLink createMethodLink(Supplier<TypeLink> tl, String name) {
        return Assertions3.assertMethodExists(tl.get(), Matcher.of(type -> type.identifier().equals(name), name));
    }

    public static Class<?> getClassOfTypeLink(TypeLink tl) throws ClassNotFoundException {
        return Class.forName(PACKAGE_LINK.name() + "." + tl.identifier());
    }


    public static final PackageLink PACKAGE_LINK = BasicPackageLink.of("h08");

    public static final Supplier<TypeLink> BAD_TIME_STAMP_EXCEPTION_LINK = createTypeLink("BadTimestampException");

    public static final Supplier<ConstructorLink> BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK = createConstructorLink(
        BAD_TIME_STAMP_EXCEPTION_LINK,
        BasicTypeLink.of(LocalDate.class)
    );

    public static final Supplier<TypeLink> BANK_EXCEPTION_LINK = createTypeLink("BankException");

    public static final Supplier<ConstructorLink> BANK_EXCEPTION_STRING_CONSTRUCTOR_LINK = createConstructorLink(
        BANK_EXCEPTION_LINK,
        BasicTypeLink.of(String.class)
    );

    public static final Supplier<ConstructorLink> BANK_EXCEPTION_LONG_CONSTRUCTOR_LINK = createConstructorLink(
        BANK_EXCEPTION_LINK,
        BasicTypeLink.of(long.class)
    );

    public static final Supplier<TypeLink> TRANSACTION_EXCEPTION_LINK = createTypeLink("TransactionException");

    public static final Supplier<ConstructorLink> TRANSACTION_EXCEPTION_STRING_CONSTRUCTOR_LINK = createConstructorLink(
        TRANSACTION_EXCEPTION_LINK,
        BasicTypeLink.of(String.class),
        BasicTypeLink.of(long.class)
    );

    public static final Supplier<ConstructorLink> TRANSACTION_EXCEPTION_TRANSACTION_CONSTRUCTOR_LINK = createConstructorLink(
        TRANSACTION_EXCEPTION_LINK,
        BasicTypeLink.of(Transaction[].class)
    );

    public static <T extends Stringifiable> Matcher<T> similarityMatcher(final String string) {
        return new Matcher<>() {

            /**
             * The maximum similarity between the given string and a matched object.
             */
            double maxSimilarity = 0;

            @Override
            public String characteristic() {
                return String.format("at least %.0f%% similar to %s", MINIMUM_SIMILARITY * 100, HTML.tt(string));
            }

            @Override
            public <ST extends T> Match<ST> match(final ST object) {

                return new Match<>() {

                    final double similarity = MatchingUtils.similarity(object.string(), string);

                    {
                        if (matched()) {
                            maxSimilarity = max(maxSimilarity, similarity);
                        }
                    }

                    @Override
                    public boolean matched() {
                        return similarity >= MINIMUM_SIMILARITY;
                    }

                    @Override
                    public ST object() {
                        return object;
                    }

                    @Override
                    public int compareTo(final Match<ST> other) {
                        if (!other.matched()) {
                            return matched() ? 1 : 0;
                        } else if (!matched()) {
                            return -1;
                        }
                        final double otherSimilarity = MatchingUtils.similarity(other.object().string(), string);
                        return Double.compare(similarity, otherSimilarity);
                    }
                };
            }
        };
    }



}
