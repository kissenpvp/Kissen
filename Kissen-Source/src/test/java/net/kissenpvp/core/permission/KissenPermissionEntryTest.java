package net.kissenpvp.core.permission;

import net.kissenpvp.core.Test;
import net.kissenpvp.core.TestData;
import net.kissenpvp.core.time.TemporalMeasureNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

class KissenPermissionEntryTest extends Test {
    private static final TestPermissionEntry testPermissionEntry = new TestPermissionEntry();

    @Contract(pure = true, value = "-> new")
    private static @NotNull Stream<TestData<String, TestPermission>> testMatcherSource() {
        return Stream.of(new TestData<>("minecraft.command.gamemode", testPermission("minecraft.command.gamemode"), true), new TestData<>("minecraft.command.gamemode", testPermission("minecraft.comman?.gamemode"), true), new TestData<>("minecraft.command.gamemode", testPermission("minecraft*gamemode"), true), new TestData<>("minecraft.command.gamemode", testPermission("minec*"), true), new TestData<>("minecraft.command.gamemode", testPermission("minecraft.c?ommand.gamemode"), false), new TestData<>("minecraft.c", testPermission("minecraft.c?ommand.gamemode"), false));
    }

    @Contract(pure = true, value = "_ -> new")
    private static @NotNull TestPermission testPermission(@NotNull String permission) {
        TemporalMeasureNode temporal = new TemporalMeasureNode();
        KissenPermissionNode node = new KissenPermissionNode(permission, testPermissionEntry, true, temporal);
        return new TestPermission(node, testPermissionEntry);
    }

    @ParameterizedTest
    @MethodSource("testMatcherSource")
    void matcher(@NotNull TestData<String, TestPermission> testData) {
        test(Eval.eval(testData.request(), testData.equalsResult(), Validator.equals()), request -> {
            Optional<TestPermission> permission = testPermissionEntry.matcher(request, testData.expected());
            return permission.isPresent();
        });
    }
}
