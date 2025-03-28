package mn.f5.jmeter.report;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.report.core.Sample;
import org.apache.jmeter.report.processor.TimeRateAggregatorFactory;
import org.apache.jmeter.report.processor.graph.AbstractGraphConsumer;
import org.apache.jmeter.report.processor.graph.AbstractOverTimeGraphConsumer;
import org.apache.jmeter.report.processor.graph.AbstractSeriesSelector;
import org.apache.jmeter.report.processor.graph.GroupInfo;
import org.apache.jmeter.report.processor.graph.TimeStampKeysSelector;

public class FrequencyOverTimeGraphConsumer extends AbstractOverTimeGraphConsumer {

    private String sampleKey;

    @Override
    protected TimeStampKeysSelector createTimeStampKeysSelector() {
        var keysSelector = new TimeStampKeysSelector();
        keysSelector.setSelectBeginTime(false);
        return keysSelector;
    }

    @Override
    protected Map<String, GroupInfo> createGroupInfos() {
        return Collections.singletonMap(
            AbstractGraphConsumer.DEFAULT_GROUP,
            new GroupInfo(
                new TimeRateAggregatorFactory(),
                new AbstractSeriesSelector() {
                    @Override
                    public Iterable<String> select(Sample sample) {
                        var value = sample.getData(sampleKey);
                        return StringUtils.isNotBlank(value) ? Collections.singleton(value) : Collections.emptyList();
                    }
                },
                (_, sample) -> sample.isController() ? null : 1d,
                false,
                false
            )
        );
    }

    public void setSampleKey(String sampleKey) {
        this.sampleKey = sampleKey;
    }
    
}
