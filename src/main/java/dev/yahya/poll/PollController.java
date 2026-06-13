package dev.yahya.poll;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final Map<String, Poll> polls = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate broker;

    public PollController(SimpMessagingTemplate broker) {
        this.broker = broker;
    }

    @GetMapping
    public Collection<Poll> list() {
        return polls.values();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        String question = String.valueOf(body.getOrDefault("question", "")).trim();
        Object rawOptions = body.get("options");

        if (question.isEmpty() || question.length() > 200) {
            return ResponseEntity.unprocessableEntity().body(error("Question must be 1-200 characters."));
        }
        if (!(rawOptions instanceof List)) {
            return ResponseEntity.unprocessableEntity().body(error("Options must be a list."));
        }

        List<String> options = ((List<?>) rawOptions).stream()
                .map(String::valueOf)
                .map(String::trim)
                .filter(option -> !option.isEmpty())
                .distinct()
                .limit(6)
                .collect(Collectors.toList());

        if (options.size() < 2) {
            return ResponseEntity.unprocessableEntity().body(error("At least two distinct options needed."));
        }

        Poll poll = new Poll(UUID.randomUUID().toString().substring(0, 8), question, options);
        polls.put(poll.getId(), poll);
        broker.convertAndSend("/topic/polls", poll);
        return ResponseEntity.status(HttpStatus.CREATED).body(poll);
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> vote(@PathVariable String id, @RequestBody Map<String, String> body) {
        Poll poll = polls.get(id);
        if (poll == null) {
            return ResponseEntity.notFound().build();
        }
        if (!poll.vote(body.getOrDefault("option", ""))) {
            return ResponseEntity.unprocessableEntity().body(error("Unknown option."));
        }
        broker.convertAndSend("/topic/polls", poll);
        return ResponseEntity.ok(poll);
    }

    private static Map<String, String> error(String message) {
        Map<String, String> payload = new ConcurrentHashMap<>();
        payload.put("error", message);
        return payload;
    }
}
