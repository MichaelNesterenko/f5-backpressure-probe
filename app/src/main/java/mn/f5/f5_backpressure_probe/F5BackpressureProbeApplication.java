package mn.f5.f5_backpressure_probe;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class F5BackpressureProbeApplication {

  @RestController
  static class ProbeEndpoints {
    
    final Logger log = LoggerFactory.getLogger(ProbeEndpoints.class);
    
    volatile boolean healthState = true, payloadState = true;

    @GetMapping("/server-status")
    ResponseEntity<String> serverStatus() {
      log.info("server status check");
      return conditionalResponse(healthState, null);
    }

    @PostMapping("/health-state")
    void setStatus(@RequestBody Map<String, Object> state) {
      switch ((String) state.get("scope")) {
        case "health" : healthState = (Boolean) state.get("value"); break;
        case "payload" : payloadState = (Boolean) state.get("value"); break;
      }
    }

    @GetMapping("/payload")
    ResponseEntity<String> payload() throws UnknownHostException {
      return conditionalResponse(payloadState, InetAddress.getLocalHost().getCanonicalHostName());
    }

    <T> ResponseEntity<T> conditionalResponse(boolean condition, T payload) {
      return ResponseEntity.status(condition ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE).body(condition ? payload : (T) null);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(F5BackpressureProbeApplication.class, args);
  }

}