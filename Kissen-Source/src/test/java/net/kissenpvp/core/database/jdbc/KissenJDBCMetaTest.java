package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.TestData;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

class KissenJDBCMetaTest extends net.kissenpvp.core.Test {

    private static @NotNull Stream<TestData<Consumer<KissenJDBCMeta>, String>> selectQueries() {
        return Stream.of(new TestData<>((meta) -> {
            QuerySelect.RootQuerySelect select = meta.select(Column.TOTAL_ID);
            select.execute();
        }, "SELECT uuid FROM test;"), new TestData<>((meta) -> {
            QuerySelect.RootQuerySelect select = meta.select(Column.TOTAL_ID, Column.KEY);
            select.execute();
        }, "SELECT uuid, identifier FROM test;"), new TestData<>((meta) -> {
            QuerySelect.RootQuerySelect select = meta.select(Column.KEY, Column.TOTAL_ID);
            select.execute();
        }, "SELECT identifier, uuid FROM test;"), new TestData<>((meta) -> {
            QuerySelect.RootQuerySelect select = meta.select(Column.VALUE, Column.TOTAL_ID);
            select.execute();
        }, "SELECT value, type, uuid FROM test;"), new TestData<>((meta) -> {
            QuerySelect select = meta.select(Column.VALUE, Column.TOTAL_ID, Column.KEY, Column.KEY).where(Column.TOTAL_ID, "");
            select.execute();
        }, "SELECT value, type, uuid, identifier, identifier FROM test WHERE uuid REGEXP ?;"), new TestData<>((meta) -> {
            QuerySelect select = meta.select(Column.TOTAL_ID, Column.VALUE).where(Column.KEY, "").and(Column.TOTAL_ID, "");
            select.execute();
        }, "SELECT uuid, value, type FROM test WHERE identifier REGEXP ? AND uuid REGEXP ?;"), new TestData<>((meta) -> {
            QueryUpdate.RootQueryUpdate request = meta.update(new Update(Column.TOTAL_ID, ""));
            request.execute();
        }, "UPDATE test SET uuid = ?;"), new TestData<>((meta) -> {
            QueryUpdate request = meta.update(new Update(Column.TOTAL_ID, "")).where(Column.TOTAL_ID, "");
            request.execute();
        }, "UPDATE test SET uuid = ? WHERE uuid REGEXP ?;"), new TestData<>((meta) -> {
            QueryUpdate request = meta.update(new Update(Column.TOTAL_ID, "")).where(Column.TOTAL_ID, "").or(Column.KEY, "test").and(Column.VALUE, "test");
            request.execute();
        }, "UPDATE test SET uuid = ? WHERE uuid REGEXP ? OR identifier REGEXP ? AND value REGEXP ?;"));
    }

    @ParameterizedTest
    @MethodSource("selectQueries")
    void testQueries(@NotNull TestData<Consumer<KissenJDBCMeta>, String> data) {
        KissenJDBCMetaMock mock = new KissenJDBCMetaMock();
        CompletableFuture<String> result = mock.getResult();
        data.request().accept(mock);
        Assertions.assertEquals(data.expected(), result.join());
    }

}
