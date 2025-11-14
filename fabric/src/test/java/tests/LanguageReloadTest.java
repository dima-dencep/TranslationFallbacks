package tests;

import jerozgen.languagereload.LanguageReload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redlance.dima_dencep.mods.translationfallbacks.compat.LanguageReloadCompat;

public class LanguageReloadTest {
    @Test
    public void test() {
        Assertions.assertTrue(LanguageReloadCompat.isAvailable());
        Assertions.assertEquals(LanguageReloadCompat.getLanguages(), LanguageReload.getLanguages());
    }
}
