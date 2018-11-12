import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.collect.ImmutableList;
import configuration.NotiConsumerConfiguration;
import io.dropwizard.metrics.MetricsFactory;
import io.dropwizard.metrics.ReporterFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;
import io.dropwizard.util.Duration;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class MetricsModule extends NotiConsumerModule {

  public MetricsModule(NotiConsumerConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // extract configuration.
    final MetricsFactory metricsFactory = this.getConfiguration().getMetricsFactory();
    final ImmutableList<ReporterFactory> reporterFactories = metricsFactory.getReporters();
    final GraphiteReporterFactory graphiteReporterFactory =
        (GraphiteReporterFactory) reporterFactories.get(0);

    final String host = graphiteReporterFactory.getHost();
    final Integer port = graphiteReporterFactory.getPort();
    final TimeUnit rateUnit = graphiteReporterFactory.getRateUnit();
    final TimeUnit durationUnit = graphiteReporterFactory.getDurationUnit();
    final String prefix = graphiteReporterFactory.getPrefix();
    final Optional<Duration> graphiteFrequency = graphiteReporterFactory.getFrequency();
    final Duration metricsFrequency = metricsFactory.getFrequency();
    final Duration frequency =
        graphiteFrequency.isPresent() ? graphiteFrequency.get() : metricsFrequency;

    // construct reporter - should make sure i have to do this! look for resources.
    final MetricRegistry metricRegistry = new MetricRegistry();
    final Graphite graphite = new Graphite(new InetSocketAddress(host, port));
    final GraphiteReporter reporter =
        GraphiteReporter.forRegistry(metricRegistry)
            .prefixedWith(prefix)
            .convertRatesTo(rateUnit)
            .convertDurationsTo(durationUnit)
            .filter(MetricFilter.ALL)
            .build(graphite);
    reporter.start(frequency.getQuantity(), frequency.getUnit());

    // register metric registry with environment.
    this.getEnvironment().register(MetricRegistry.class, metricRegistry);
  }
}
