package com.crm.webscrape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreqParser {

    private static Config config;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(new File("freqParse.json"), Config.class);
        } catch (IOException e) {
            System.err.println("Failed to load freqParse.json from CWD. Initializing empty fallback config.");
            config = new Config();
        }
    }

    private static class Config {
        @JsonProperty("numberWords")
        public Map<String, Integer> numberWords = new HashMap<>();

        @JsonProperty("numberPattern")
        public String numberPattern = "";

        @JsonProperty("timeUnitPattern")
        public String timeUnitPattern = "";

        @JsonProperty("patterns")
        public Map<String, String> patterns = new HashMap<>();
    }

    public static class Freq {
        private final Integer times;
        private final Period period;

        public Freq(Integer times, Period period) {
            this.times = times;
            this.period = period;
        }

        public Integer getTimes() {
            return times;
        }

        public Period getPeriod() {
            return period;
        }

        @Override
        public String toString() {
            if (period == null) {
                return times + "x, lifetime";
            }

            StringBuilder result = new StringBuilder(times + "x, ");

            if (period.getYears() != 0) {
                result.append(period.getYears()).append("yr ");
            }
            if (period.getMonths() != 0) {
                result.append(period.getMonths()).append("mo ");
            }
            if (period.getDays() != 0) {
                if (period.getDays() % 7 == 0 && period.getDays() > 0) {
                    result.append(period.getDays() / 7).append("wk ");
                } else {
                    result.append(period.getDays()).append("d ");
                }
            }

            return result.toString().trim();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Freq freq = (Freq) o;
            return Objects.equals(times, freq.times) && Objects.equals(period, freq.period);
        }

        @Override
        public int hashCode() {
            return Objects.hash(times, period);
        }
    }

    public static Freq parse(String freq) {
        List<Freq> results = parseAll(freq);
        return results.isEmpty() ? null : results.get(0);
    }

    public static List<Freq> parseAll(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String input = text.toLowerCase().trim();
        List<Freq> results = new ArrayList<>();

        String numPat = config.numberPattern;
        String timePat = config.timeUnitPattern;

        // 1. Lifetime Pattern
        if (config.patterns.containsKey("lifetime")) {
            String rawRegex = String.format(config.patterns.get("lifetime"), numPat, timePat);
            Pattern pattern = Pattern.compile(rawRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                int count = parseNumber(matcher.group(1));
                results.add(new Freq(count, null));
            }
        }

        // 2. Calendar Year Pattern
        if (config.patterns.containsKey("calendarYear")) {
            String rawRegex = String.format(config.patterns.get("calendarYear"), numPat, timePat);
            Pattern pattern = Pattern.compile(rawRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                int count = parseNumber(matcher.group(1));
                results.add(new Freq(count, Period.ofYears(1)));
            }
        }

        // 3. Explicit "N in N units" Pattern
        if (config.patterns.containsKey("explicitIn")) {
            String rawRegex = String.format(config.patterns.get("explicitIn"), numPat, timePat);
            Pattern pattern = Pattern.compile(rawRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                int count = parseNumber(matcher.group(1));
                int periodVal = parseNumber(matcher.group(2));
                String unit = matcher.group(3);
                results.add(new Freq(count, buildPeriod(periodVal, unit)));
            }
        }

        // 4. General Frequency + Period Pattern
        if (config.patterns.containsKey("general")) {
            String rawRegex = String.format(config.patterns.get("general"), numPat, timePat);
            Pattern pattern = Pattern.compile(rawRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                int count = parseNumber(matcher.group(1));
                int periodVal = parseNumber(matcher.group(2));
                String unit = matcher.group(3);
                results.add(new Freq(count, buildPeriod(periodVal, unit)));
            }
        }

        // Maintain unique results
        List<Freq> uniqueResults = new ArrayList<>();
        for (Freq f : results) {
            if (!uniqueResults.contains(f)) {
                uniqueResults.add(f);
            }
        }

        return uniqueResults;
    }

    private static int parseNumber(String value) {
        String key = value.toLowerCase().trim();
        if (config.numberWords.containsKey(key)) {
            return config.numberWords.get(key);
        }
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static Period buildPeriod(int amount, String unit) {
        String u = unit.toLowerCase();
        if (u.startsWith("year")) {
            return Period.ofYears(amount);
        } else if (u.startsWith("month")) {
            if (amount % 12 == 0) {
                return Period.ofYears(amount / 12);
            }
            return Period.ofMonths(amount);
        } else if (u.startsWith("week")) {
            return Period.ofWeeks(amount);
        } else if (u.startsWith("day")) {
            return Period.ofDays(amount);
        }
        return Period.ZERO;
    }
}
