package org.textprocessing.textprocessing;

public class RegexExample {
    private final String regex;
    private final String description;
    private final String example;

    public RegexExample(String regex, String description, String example) {
        this.regex = regex;
        this.description = description;
        this.example = example;
    }

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    public String getExample() {
        return example;
    }

    @Override
    public String toString() {
        return "Regex: " + regex + "\nDescription: " + description + "\nExample: " + example;
    }
}
