import java.util.List;
import java.time.Instant;

public class Main {
    public static void main(String[] args){
        List<SecurityEvent> listOfEvents = List.of(
            new SecurityEvent("idk", "INFO", "idk", "idk", Instant.now()),
            new SecurityEvent("idk", "CRITICAL", "idk", "idk", Instant.now()),
            new SecurityEvent("idk", "NORMAL", "idk", "idk", Instant.now())
        );

        listOfEvents.stream()
            .filter(e -> e.priority().equals("CRITICAL"))
            .forEach(e -> System.out.println(e));

    }
}
