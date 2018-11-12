import configuration.Configuration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;

public abstract class Application<T extends Configuration> {

  public abstract String getName();

  public abstract void run(T configuration, Environment environment) throws Exception;

  public abstract Class<T> getConfigurationClass();

  public void run(String args[]) throws Exception {
    if (args.length < 1) {
      System.out.println("Must provide a path to the configuration file!");
      System.exit(1);
    }

    T configuration = this.loadConfiguration(args[0]);
    Environment environment = new Environment();
    this.run(configuration, environment);
  }

  public T loadConfiguration(String path) throws IOException, ConfigurationException {
    DefaultConfigurationFactoryFactory<T> d = new DefaultConfigurationFactoryFactory<T>();
    ConfigurationFactory<T> configurationFactory =
        d.create(this.getConfigurationClass(), null, Jackson.newObjectMapper(), "dw");
    T configuration =
        configurationFactory.build(
            new SubstitutingSourceProvider(
                new FileConfigurationSourceProvider(), new EnvironmentVariableSubstitutor()),
            path);
    return configuration;
  }
}
