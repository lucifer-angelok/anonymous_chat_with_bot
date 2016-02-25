package org.lucifer.abchat.morphology.semantic;

public class GrammaticalBase {
    public final String subject;
    public final String predicate;

    public GrammaticalBase(String noun, String verb) {
        this.subject = noun;
        this.predicate = verb;
    }

    @Override
    public String toString(){
        return subject + " " + predicate;
    }
}
