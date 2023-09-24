package net.kissenpvp.core.api.time;

public enum TimeUnit
{
    PERIOD('Y', 'W', 'D'),
    DURATION('H', 'M', 'S');

    private final char[] applicableChars;

    TimeUnit(char... applicableChars) {
        this.applicableChars = applicableChars;
    }

    public boolean appliesTo(final char currentChar) {
        for (final char applicableChar : applicableChars) {
            if (applicableChar == currentChar) {
                return true;
            }
        }
        return false;
    }
}
