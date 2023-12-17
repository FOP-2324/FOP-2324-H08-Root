package h08;

import com.google.common.base.Suppliers;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions3;
import org.tudalgo.algoutils.tutor.general.match.BasicReflectionMatchers;
import org.tudalgo.algoutils.tutor.general.match.Matcher;
import org.tudalgo.algoutils.tutor.general.reflections.*;

import java.time.LocalDate;
import java.util.function.Supplier;

public class StudentLinks {

    public static Supplier<TypeLink> createTypeLink(String name) {
        return Suppliers.memoize(() -> Assertions3.assertTypeExists(
            PACKAGE_LINK,
            Matcher.of(type -> type.identifier().equals(name), name)
        ));
    }

    public static Supplier<ConstructorLink> createConstructorLink(TypeLink tl, Matcher<ConstructorLink> matcher) {
        return Suppliers.memoize(() -> Assertions3.assertConstructorExists(tl, matcher));
    }

    public static Supplier<ConstructorLink> createConstructorLink(TypeLink tl, TypeLink... args) {
        return createConstructorLink(tl, BasicReflectionMatchers.sameTypes(args));
    }

    public static final PackageLink PACKAGE_LINK = BasicPackageLink.of("h08");

    public static final Supplier<TypeLink> BAD_TIME_STAMP_EXCEPTION_LINK = createTypeLink("BadTimestampException");

    public static final Supplier<ConstructorLink> BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK = createConstructorLink(
        BAD_TIME_STAMP_EXCEPTION_LINK.get(),
        BasicTypeLink.of(LocalDate.class)
    );

    public static final Supplier<TypeLink> BANK_EXCEPTION_LINK = createTypeLink("BankException");

    public static final Supplier<ConstructorLink> BANK_EXCEPTION_STRING_CONSTRUCTOR_LINK = createConstructorLink(
        BANK_EXCEPTION_LINK.get(),
        BasicTypeLink.of(String.class)
    );

    public static final Supplier<ConstructorLink> BANK_EXCEPTION_LONG_CONSTRUCTOR_LINK = createConstructorLink(
        BANK_EXCEPTION_LINK.get(),
        BasicTypeLink.of(long.class)
    );

    public static final Supplier<TypeLink> TRANSACTION_EXCEPTION_LINK = createTypeLink("TransactionException");

    public static final Supplier<ConstructorLink> TRANSACTION_EXCEPTION_STRING_CONSTRUCTOR_LINK = createConstructorLink(
        TRANSACTION_EXCEPTION_LINK.get(),
        BasicTypeLink.of(String.class),
        BasicTypeLink.of(long.class)
    );

    public static final Supplier<ConstructorLink> TRANSACTION_EXCEPTION_TRANSACTION_CONSTRUCTOR_LINK = createConstructorLink(
        TRANSACTION_EXCEPTION_LINK.get(),
        BasicTypeLink.of(Transaction[].class)
    );





}
