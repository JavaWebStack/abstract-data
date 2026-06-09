package org.javawebstack.abstractdata.mapper;

import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.mapper.annotation.DateFormat;
import org.javawebstack.abstractdata.mapper.exception.MapperException;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class JavaTimeMapperTest {

    @Test
    public void testIsoRoundTripTopLevel() {
        assertRoundTrip(LocalDate.of(2026, 6, 9), LocalDate.class, "2026-06-09");
        assertRoundTrip(LocalDateTime.of(2026, 6, 9, 15, 30, 45), LocalDateTime.class, "2026-06-09T15:30:45");
        assertRoundTrip(LocalTime.of(15, 30, 45), LocalTime.class, "15:30:45");
        assertRoundTrip(Instant.parse("2026-06-09T13:50:45Z"), Instant.class, "2026-06-09T13:50:45Z");
        assertRoundTrip(OffsetDateTime.of(2026, 6, 9, 15, 30, 45, 0, ZoneOffset.ofHours(2)), OffsetDateTime.class, "2026-06-09T15:30:45+02:00");
        assertRoundTrip(ZonedDateTime.of(2026, 6, 9, 15, 30, 45, 0, ZoneId.of("Europe/Berlin")), ZonedDateTime.class, "2026-06-09T15:30:45+02:00[Europe/Berlin]");
        assertRoundTrip(OffsetTime.of(15, 30, 45, 0, ZoneOffset.ofHours(2)), OffsetTime.class, "15:30:45+02:00");
        assertRoundTrip(Year.of(2026), Year.class, "2026");
        assertRoundTrip(YearMonth.of(2026, 6), YearMonth.class, "2026-06");
        assertRoundTrip(MonthDay.of(6, 9), MonthDay.class, "--06-09");
    }

    private <T> void assertRoundTrip(T value, Class<T> type, String expected) {
        Mapper mapper = new Mapper();
        AbstractElement element = assertDoesNotThrow(() -> mapper.map(value));
        assertTrue(element.isString(), type.getSimpleName() + " should serialize to a string");
        assertEquals(expected, element.string());
        assertEquals(value, assertDoesNotThrow(() -> mapper.map(element, type)));
    }

    static class EpochHolder {
        @DateFormat(epoch = true)
        Instant seconds;
        @DateFormat(epoch = true, millis = true)
        Instant millis;
    }

    @Test
    public void testEpochInstant() {
        EpochHolder holder = new EpochHolder();
        holder.seconds = Instant.ofEpochSecond(1781013045);
        holder.millis = Instant.ofEpochMilli(1781013045123L);

        AbstractElement element = assertDoesNotThrow(() -> new Mapper().map(holder));
        assertEquals(1781013045L, element.object().get("seconds").number().longValue());
        assertEquals(1781013045123L, element.object().get("millis").number().longValue());

        EpochHolder parsed = assertDoesNotThrow(() -> new Mapper().map(element, EpochHolder.class));
        assertEquals(Instant.ofEpochSecond(1781013045), parsed.seconds);
        assertEquals(Instant.ofEpochMilli(1781013045123L), parsed.millis);
    }

    static class PatternHolder {
        @DateFormat("dd.MM.yyyy")
        LocalDate day;
        @DateFormat("yyyy-MM-dd HH:mm:ss")
        LocalDateTime moment;
    }

    @Test
    public void testCustomPattern() {
        PatternHolder holder = new PatternHolder();
        holder.day = LocalDate.of(2026, 6, 9);
        holder.moment = LocalDateTime.of(2026, 6, 9, 15, 30, 45);

        AbstractElement element = assertDoesNotThrow(() -> new Mapper().map(holder));
        assertEquals("09.06.2026", element.object().get("day").string());
        assertEquals("2026-06-09 15:30:45", element.object().get("moment").string());

        PatternHolder parsed = assertDoesNotThrow(() -> new Mapper().map(element, PatternHolder.class));
        assertEquals(holder.day, parsed.day);
        assertEquals(holder.moment, parsed.moment);
    }

    static class BadEpochHolder {
        @DateFormat(epoch = true)
        LocalDate day = LocalDate.of(2026, 6, 9);
    }

    @Test
    public void testEpochRejectedForNonInstant() {
        MapperException e = assertThrows(MapperException.class, () -> new Mapper().map(new BadEpochHolder()));
        assertTrue(e.getMessage().contains("epoch"));
    }

    @Test
    public void testMalformedValueThrowsMapperException() {
        assertThrows(MapperException.class, () -> new Mapper().map(new org.javawebstack.abstractdata.AbstractPrimitive("not-a-date"), LocalDate.class));
    }
}
