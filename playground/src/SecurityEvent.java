
import java.time.Instant;

record SecurityEvent(String rule, String priority, String namespace, String pod, Instant time){}