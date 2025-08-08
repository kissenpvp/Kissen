package net.kissenpvp.database;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionProviderTest
{
    @Test
    public void testSchema() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException, IOException
    {
        InternalConnectionProvider internalConnectionProvider = new InternalConnectionProvider();

        internalConnectionProvider.connect("jdbc:mysql://kissen:development@localhost:3306/kissen", false);

        assertTrue(internalConnectionProvider.connection().isPresent());

        Method method = internalConnectionProvider.getClass().getDeclaredMethod("loadSchema");
        method.setAccessible(true);
        String[] sql = (String[]) method.invoke(internalConnectionProvider);
        method.setAccessible(false);

        Connection connection = internalConnectionProvider.connection().get();
        for (String query : sql) { connection.prepareStatement(query).execute(); }
    }
}