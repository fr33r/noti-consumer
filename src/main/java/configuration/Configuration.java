package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.metrics.MetricsFactory;

/**
 * Adapted from
 * https://github.com/dropwizard/dropwizard/blob/master/dropwizard-core/src/main/java/io/dropwizard/Configuration.java.
 */
public class Configuration {

  private LoggingFactory logging;
  private MetricsFactory metrics;

  @JsonProperty("logging")
  public synchronized LoggingFactory getLoggingFactory() {
    if (this.logging == null) {
      // lazy load.
      this.logging = new DefaultLoggingFactory();
    }
    return this.logging;
  }

  @JsonProperty("logging")
  public synchronized void setLoggingFactory(LoggingFactory loggingFactory) {
    this.logging = loggingFactory;
  }

  @JsonProperty("metrics")
  public MetricsFactory getMetricsFactory() {
    return this.metrics;
  }

  @JsonProperty("metrics")
  public void setMetricsFactory(MetricsFactory metricsFactory) {
    this.metrics = metricsFactory;
  }
}
