package net.kissenpvp.base;

import org.jetbrains.annotations.NotNull;
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

    public LoggingOutputStream(@NotNull Logger logger, @NotNull Level level) throws NullPointerException
    {
        Objects.requireNonNull(logger, "Logger cannot be null");
        Objects.requireNonNull(level, "LogLevel cannot be null");

        this.logger = logger;
        this.level = level;

        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public @NotNull Level level()
    {
        return level;
    }

    public void level(@NotNull Level level) throws NullPointerException
    {
        Objects.requireNonNull(level, "LogLevel cannot be null");

        this.level = level;
    }

    public @NotNull Logger logger()
    {
        return logger;
    }

    public void logger(@NotNull Logger logger) throws NullPointerException
    {
        Objects.requireNonNull(logger, "Logger cannot be null");

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
