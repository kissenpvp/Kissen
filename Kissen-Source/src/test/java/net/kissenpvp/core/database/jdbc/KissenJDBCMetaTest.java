package net.kissenpvp.core.database.jdbc;

import net.kissenpvp.core.TestData;
import net.kissenpvp.core.api.database.meta.Meta;
import net.kissenpvp.core.api.database.queryapi.Column;
import net.kissenpvp.core.api.database.queryapi.select.QuerySelect;
import net.kissenpvp.core.api.database.queryapi.update.QueryUpdate;
import net.kissenpvp.core.api.database.queryapi.update.Update;
import net.kissenpvp.core.base.KissenPluginMock;
import net.kissenpvp.core.database.jdbc.query.JDBCSelectQueryExecutor;
import net.kissenpvp.core.database.jdbc.query.JDBCUpdateQueryExecutor;
import net.kissenpvp.core.database.queryapi.KissenQuerySelect;
import net.kissenpvp.core.database.queryapi.KissenQueryUpdate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

class KissenJDBCMetaTest extends net.kissenpvp.core.Test {

    private static @NotNull Stream<TestData<Object, String>> selectInternalQueries() {
        Meta meta = new KissenJDBCMetaMock();
        return Stream.of(new TestData<>(meta.select(Column.TOTAL_ID), "SELECT total_id FROM test WHERE plugin IS NULL;"), new TestData<>(meta.select(Column.TOTAL_ID, Column.KEY), "SELECT total_id, key FROM test WHERE plugin IS NULL;"), new TestData<>(meta.select(Column.VALUE, Column.TOTAL_ID, Column.KEY, Column.KEY).where(Column.TOTAL_ID, ""), "SELECT plugin, type, value, total_id, key, key FROM test WHERE (total_id REGEXP ?) AND plugin IS NULL;"), new TestData<>(meta.select(Column.TOTAL_ID, Column.VALUE).where(Column.KEY, "").and(Column.TOTAL_ID, ""), "SELECT total_id, plugin, type, value FROM test WHERE (key REGEXP ? AND total_id REGEXP ?) AND plugin IS NULL;"));
    }

    private static @NotNull Stream<TestData<Object, String>> selectPluginQueries() {
        Meta meta = new KissenJDBCMetaMock(new KissenPluginMock());
        return Stream.of(new TestData<>(meta.select(Column.TOTAL_ID), "SELECT total_id FROM test WHERE plugin = ?;"), new TestData<>(meta.select(Column.TOTAL_ID, Column.KEY), "SELECT total_id, key FROM test WHERE plugin = ?;"), new TestData<>(meta.select(Column.VALUE, Column.TOTAL_ID, Column.KEY, Column.KEY).where(Column.TOTAL_ID, ""), "SELECT plugin, type, value, total_id, key, key FROM test WHERE (total_id REGEXP ?) AND plugin = ?;"), new TestData<>(meta.select(Column.TOTAL_ID, Column.VALUE).where(Column.KEY, "").and(Column.TOTAL_ID, ""), "SELECT total_id, plugin, type, value FROM test WHERE (key REGEXP ? AND total_id REGEXP ?) AND plugin = ?;"));
    }

    private static @NotNull Stream<TestData<Object, String>> updateInternalQueries() {
        Meta meta = new KissenJDBCMetaMock();
        return Stream.of(
                new TestData<>(meta.update(new Update(Column.TOTAL_ID, "")), "UPDATE test SET total_id = ? WHERE plugin IS NULL;"),
                new TestData<>(meta.update(new Update(Column.TOTAL_ID, ""), new Update(Column.KEY, "")), "UPDATE test SET total_id = ?, key = ? WHERE plugin IS NULL;"),
                new TestData<>(meta.update(new Update(Column.VALUE, ""), new Update(Column.TOTAL_ID, ""), new Update(Column.KEY, ""), new Update(Column.KEY, "")).where(Column.TOTAL_ID, ""), "UPDATE test SET type = ?, value = ?, total_id = ?, key = ?, key = ? WHERE (total_id REGEXP ?) AND plugin IS NULL;"),
                new TestData<>(meta.update(new Update(Column.TOTAL_ID, ""), new Update(Column.VALUE, "")).where(Column.KEY, "").and(Column.TOTAL_ID, ""), "UPDATE test SET total_id = ?, type = ?, value = ? WHERE (key REGEXP ? AND total_id REGEXP ?) AND plugin IS NULL;"));
    }

    private static @NotNull Stream<TestData<Object, String>> updatePluginQueries() {
        Meta meta = new KissenJDBCMetaMock(new KissenPluginMock());
        return Stream.of(
                new TestData<>(meta.update(new Update(Column.TOTAL_ID, "")), "UPDATE test SET total_id = ? WHERE plugin = ?;"),
                new TestData<>(meta.update(new Update(Column.TOTAL_ID, ""),
                        new Update(Column.KEY, "")), "UPDATE test SET total_id = ?, key = ? WHERE plugin = ?;"),
                new TestData<>(meta.update(new Update(Column.VALUE, ""), new Update(Column.TOTAL_ID, ""), new Update(Column.KEY, ""), new Update(Column.KEY, "")).where(Column.TOTAL_ID, ""), "UPDATE test SET type = ?, value = ?, total_id = ?, key = ?, key = ? WHERE (total_id REGEXP ?) AND plugin = ?;"),
                new TestData<>(meta.update(new Update(Column.TOTAL_ID, ""), new Update(Column.VALUE, "")).where(Column.KEY, "").and(Column.TOTAL_ID, ""), "UPDATE test SET total_id = ?, type = ?, value = ? WHERE (key REGEXP ? AND total_id REGEXP ?) AND plugin = ?;"));
    }

    @ParameterizedTest
    @MethodSource("selectInternalQueries")
    void testSelectInternalQueries(@NotNull TestData<Object, String> data) {
        testSelectData(new KissenJDBCMetaMock(), data);
    }

    @ParameterizedTest
    @MethodSource("selectPluginQueries")
    void testSelectPluginQueries(@NotNull TestData<Object, String> data) {
        testSelectData(new KissenJDBCMetaMock(new KissenPluginMock()), data);
    }

    @ParameterizedTest
    @MethodSource("updateInternalQueries")
    void testUpdateInternalQueries(@NotNull TestData<Object, String> data) {
        testUpdateData(new KissenJDBCMetaMock(), data);
    }

    @ParameterizedTest
    @MethodSource("updatePluginQueries")
    void testUpdatePluginQueries(@NotNull TestData<Object, String> data) {
        testUpdateData(new KissenJDBCMetaMock(new KissenPluginMock()), data);
    }

    private void testSelectData(@NotNull KissenJDBCMeta meta, @NotNull TestData<Object, String> data) {
        QuerySelect select = null;

        if (data.request() instanceof KissenQuerySelect.KissenRootQuerySelect rootQueryComponent) {
            select = rootQueryComponent.getQuery();
        } else if (data.request() instanceof QuerySelect query) {
            select = query;
        } else {
            Assumptions.abort("Request is not a known type of query");
        }

        JDBCSelectQueryExecutor executor = new JDBCSelectQueryExecutor(select, meta);
        test(Eval.eval(select, data.expected(), Validator.equals()), request -> executor.constructSQL(new ArrayList<>()));
    }

    private void testUpdateData(@NotNull KissenJDBCMeta meta, @NotNull TestData<Object, String> data) {
        QueryUpdate select = null;

        if (data.request() instanceof KissenQueryUpdate.KissenRootQueryUpdate rootQueryComponent) {
            select = rootQueryComponent.getQuery();
        } else if (data.request() instanceof QueryUpdate query) {
            select = query;
        } else {
            Assumptions.abort("Request is not a known type of query");
        }

        JDBCUpdateQueryExecutor executor = new JDBCUpdateQueryExecutor(select, meta);
        test(Eval.eval(select, data.expected(), Validator.equals()), request -> executor.constructSQL(new ArrayList<>(), new ArrayList<>()));
    }

}
