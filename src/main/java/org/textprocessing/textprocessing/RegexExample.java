package org.textprocessing.textprocessing;

public record RegexExample(String regex, String description, String example) {

    @Override
    public String toString() {
        return "Regex: " + regex + "\nDescription: " + description + "\nExample: " + example;
    }
}
