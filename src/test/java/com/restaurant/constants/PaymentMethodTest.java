package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMethodTest {

    @ParameterizedTest
    @CsvSource({
            "cash, CASH",
            "CASH, CASH",
            "CaSh, CASH",
            "credit_card, CREDIT_CARD",
            "CREDIT_CARD, CREDIT_CARD",
            "CrEdIt_CaRd, CREDIT_CARD",
            "direct_bank_transfer, DIRECT_BANK_TRANSFER",
            "DIRECT_BANK_TRANSFER, DIRECT_BANK_TRANSFER",
            "DiReCt_BaNk_TrAnSfEr, DIRECT_BANK_TRANSFER",
            "e_wallet, E_WALLET",
            "E_WALLET, E_WALLET",
            "E_WAlLeT, E_WALLET"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        PaymentMethod expected = PaymentMethod.valueOf(expectedName);
        assertEquals(expected, PaymentMethod.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "cashs", "credit card", " direct_bank_transfer", "e_wallet ", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "CASH, cash",
            "CREDIT_CARD, credit card",
            "DIRECT_BANK_TRANSFER, direct bank transfer",
            "E_WALLET, e wallet"
    })
    void getMethod_shouldReturnFormattedString(String name, String expected) {
        PaymentMethod method = PaymentMethod.valueOf(name);
        assertEquals(expected, method.getMethod());
    }

    @ParameterizedTest
    @CsvSource({
            "CASH, cash",
            "CREDIT_CARD, credit card",
            "DIRECT_BANK_TRANSFER, direct bank transfer",
            "E_WALLET, e wallet"
    })
    void toString_shouldReturnSameAsGetMethod(String name, String expected) {
        PaymentMethod method = PaymentMethod.valueOf(name);
        assertEquals(expected, method.toString());
    }

    @Test
    void values_lengthShouldBe4() {
        assertEquals(4, PaymentMethod.values().length);
    }
}
