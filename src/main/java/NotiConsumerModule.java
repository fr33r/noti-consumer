import configuration.NotiConsumerConfiguration;

/**
 * Defines the abstraction of an {@link ApplicationModule} for the Noti Consumer application.
 *
 * @author Jon Freer
 */
public abstract class NotiConsumerModule implements ApplicationModule {

  private final Environment environment;
  private final NotiConsumerConfiguration configuration;

  /**
   * Constructs a new {@link NotiConsumerModule}.
   *
   * @param configuration The application configuration for the Noti Consumer application.
   * @param environment The application environment for the Noti Consumer application.
   */
  public NotiConsumerModule(NotiConsumerConfiguration configuration, Environment environment) {
    this.environment = environment;
    this.configuration = configuration;
  }

  /**
   * Retrieves the application environment.
   *
   * @return The application environment.
   */
  Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Retrieves the application configuration.
   *
   * @return The application configuration.
   */
  NotiConsumerConfiguration getConfiguration() {
    return this.configuration;
  }

  /** Configures the application module. */
  @Override
  public abstract void configure();
}
