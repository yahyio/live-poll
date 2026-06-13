package dev.yahya.poll;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Poll {

    private final String id;
    private final String question;
    private final Map<String, AtomicInteger> votes = new LinkedHashMap<>();

    public Poll(String id, String question, List<String> options) {
        this.id = id;
        this.question = question;
        for (String option : options) {
            votes.put(option, new AtomicInteger());
        }
    }

    public boolean vote(String option) {
        AtomicInteger counter = votes.get(option);
        if (counter == null) {
            return false;
        }
        counter.incrementAndGet();
        return true;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public Map<String, Integer> getResults() {
        Map<String, Integer> results = new LinkedHashMap<>();
        votes.forEach((option, count) -> results.put(option, count.get()));
        return results;
    }

    public int getTotalVotes() {
        return votes.values().stream().mapToInt(AtomicInteger::get).sum();
    }
}
