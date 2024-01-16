package h08.util;

import com.google.common.base.Suppliers;
import h08.Transaction;
import org.opentest4j.AssertionFailedError;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions3;
import org.tudalgo.algoutils.tutor.general.match.*;
import org.tudalgo.algoutils.tutor.general.reflections.*;
import org.tudalgo.algoutils.tutor.general.stringify.HTML;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
                    return fail("Could not find a class named %s in package h08. (Exact match required)".formatted(name));
                }
            }
            try {

                return Assertions3.assertTypeExists(PACKAGE_LINK, similarityMatcher(name));
            } catch (AssertionFailedError e) {

                for (PackageLink fallbackPackage : FALLBACK_PACKAGES) {
                    try {
                        return Assertions3.assertTypeExists(fallbackPackage, similarityMatcher(name));
                    } catch (AssertionFailedError ignored) {
                    }
                }

                return fail(classNotFoundMessage(name));
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

    public static Class<?> getClassOfTypeLink(TypeLink tl) {
        try {
            return Class.forName(PACKAGE_LINK.name() + "." + tl.identifier());
        } catch (ClassNotFoundException e) {
            for (PackageLink fallbackPackage : FALLBACK_PACKAGES) {
                try {
                    return Class.forName(fallbackPackage.name() + "." + tl.identifier());
                } catch (ClassNotFoundException ignored) {
                }
            }
            return fail(classNotFoundMessage(tl.identifier()));
        }
    }

    private static String classNotFoundMessage(String name) {
        return "Could not find a class named %s. (%d%% similarity match required. Searched in package: %s and %s)"
            .formatted(name, (int) (MINIMUM_SIMILARITY * 100), PACKAGE_LINK.name(), FALLBACK_PACKAGES.stream().map(PackageLink::name).toList());
    }


    public static final PackageLink PACKAGE_LINK = BasicPackageLink.of("h08");

    private static final List<String> TEST_PACKAGE_NAMES = List.of(
        "h08.implementations",
        "h08.transformer",
        "h08.util",
        "h08.util.comment"
    );

    public static final List<PackageLink> FALLBACK_PACKAGES = Arrays.stream(Package.getPackages())
        .map(Package::getName)
        .filter(name -> name.startsWith("h08"))
        .filter(name -> !TEST_PACKAGE_NAMES.contains(name))
        .map(BasicPackageLink::of)
        .map(PackageLink.class::cast)
        .toList();

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
