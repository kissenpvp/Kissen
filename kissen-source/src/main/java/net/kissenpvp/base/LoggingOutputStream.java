package net.kissenpvp.base;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class LoggingOutputStream extends OutputStream
{
    private final ByteArrayOutputStream byteArrayOutputStream;
    private Logger logger;
    private Level level;

    public LoggingOutputStream(@NonNull Logger logger, @NonNull Level level) throws NullPointerException
    {
        Preconditions.checkNotNull(logger, "Logger cannot be null");
        Preconditions.checkNotNull(level, "LogLevel cannot be null");

        this.logger = logger;
        this.level = level;

        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public @NonNull Level level()
    {
        return level;
    }

    public void level(@NonNull Level level) throws NullPointerException
    {
        Preconditions.checkNotNull(level, "LogLevel cannot be null");

        this.level = level;
    }

    public @NonNull Logger logger()
    {
        return logger;
    }

    public void logger(@NonNull Logger logger) throws NullPointerException
    {
        Preconditions.checkNotNull(logger, "Logger cannot be null");

        this.logger = logger;
    }

    @Override public void write(int b) throws IOException
    {
        if (b != '\n')
        {
            byteArrayOutputStream.write(b);
            return;
        }

        String line = byteArrayOutputStream.toString();
        byteArrayOutputStream.reset();
        logger.atLevel(level).log(line);
    }
}
