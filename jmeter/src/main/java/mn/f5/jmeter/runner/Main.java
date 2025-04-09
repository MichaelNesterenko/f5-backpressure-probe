package mn.f5.jmeter.runner;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, GenerationException, ConfigurationException {
        final var resultFile = new File(".jmeter-report-raw.jtl");
        final var reportDir = new File("./jmeter-report-rendered");
        final var jmeterHome = new File(Main.class.getClassLoader().getResource("jmeter-home").toURI());

        JMeterUtils.setJMeterHome(jmeterHome.getAbsolutePath());
        JMeterUtils.loadJMeterProperties(jmeterHome.getAbsolutePath() + "/bin/jmeter.properties");
        JMeterUtils.initLocale();

        JMeterUtils.setProperty("bp_probe_host", "httpd");
        JMeterUtils.setProperty("bp_probe_port", "80");

        JMeterUtils.setProperty("bp_health_host", "app-1");
        JMeterUtils.setProperty("bp_health_scope", "health");
        JMeterUtils.setProperty("bp_health_port", "8080");

        JMeterUtils.setProperty("bp_probe_payload_thread_count", "5");
        JMeterUtils.setProperty("bp_probe_payload_ramp_time_seconds", "30");
        JMeterUtils.setProperty("bp_probe_payload_coold_down_time_ms", "60");
        JMeterUtils.setProperty("bp_probe_payload_request_count", "6500");

        JMeterUtils.setProperty("bp_health_toggle_count", "3");
        JMeterUtils.setProperty("bp_health_start_delay_seconds", "600");
        JMeterUtils.setProperty("bp_health_toggle_delay_ms", "300000");

        JMeterUtils.setProperty(JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY, reportDir.getAbsolutePath());

        FileUtils.deleteQuietly(resultFile);
        var jmeter = new StandardJMeterEngine();
        jmeter.configure(
            tap(
                SaveService.loadTree(new File(Main.class.getClassLoader().getResource("jmeter.jmx").toURI())),
                tree -> tree.add(tree.getArray()[0], tap(new ResultCollector(new Summariser()), rc -> rc.setFilename(resultFile.getAbsolutePath())))
            )
        );
        jmeter.run();

        FileUtils.deleteDirectory(reportDir);
        new ReportGenerator(resultFile.getAbsolutePath(), null).generate();
    }

    static <T> T tap(T in, Consumer<T> customizer) {
        customizer.accept(in);
        return in;
    }
}
