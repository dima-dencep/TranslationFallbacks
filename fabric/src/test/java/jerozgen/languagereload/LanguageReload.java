package jerozgen.languagereload;

import org.jetbrains.annotations.NotNull;
import tests.FallbacksTest;

import java.util.LinkedList;

public class LanguageReload {
    public static @NotNull LinkedList<@NotNull String> getLanguages() {
        var list = new LinkedList<String>();
        list.add(FallbacksTest.MANAGER.getSelected());
        return list;
    }
}
