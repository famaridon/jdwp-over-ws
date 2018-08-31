package com.famaridon.tcpoverws.cdi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class DefaultConfigurationService implements ConfigurationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigurationService.class);
  private static final String JDWP_OVER_WS_CONF_ENV_NAME = "JDWP_OVER_WS_CONF";
  private static final String CONFIGURATION_FILE_NAME = "jdwp-over-ws.yaml";

  private ImmutableHierarchicalConfiguration configuration;

  @PostConstruct
  public void init() {

    try {
      NodeCombiner combiner = new MergeCombiner();
      CombinedConfiguration combinedConfiguration = new CombinedConfiguration(combiner);

      // check if JDWP_OVER_WS_CONF is set and use it
      this.loadEnvironmentVariableConfigurationFile(combinedConfiguration);
      // search jdwp-over-ws.yaml in working directory
      this.loadWorkingDirectoryConfigurationFile(combinedConfiguration);
      // search jdwp-over-ws.yaml in classpath
      this.loadClasspathVariableConfigurationFile(combinedConfiguration);
      // load jdwp-over-ws-default.yaml
      this.loadDefaultVariableConfigurationFile(combinedConfiguration);

      this.configuration = ConfigurationUtils.unmodifiableConfiguration(combinedConfiguration);
    }catch (ConfigurationException e ) {
      throw new IllegalStateException("Configuration can't be loaded",e);
    }

  }

  private void loadEnvironmentVariableConfigurationFile(
      CombinedConfiguration combinedConfiguration)
      throws ConfigurationException {

    String configurationFileEnv = System.getenv(JDWP_OVER_WS_CONF_ENV_NAME);
    if (StringUtils.isNotBlank(configurationFileEnv)) {
      Path configurationFilePath = Paths.get(configurationFileEnv);
      if (Files.exists(configurationFilePath)) {
        this.loadConfigurationFile(combinedConfiguration, configurationFilePath);
      } else {
        LOGGER.warn("Environment variable {} is set but file {} is not found.",
            JDWP_OVER_WS_CONF_ENV_NAME, configurationFilePath);
      }
    }
  }

  private void loadWorkingDirectoryConfigurationFile(CombinedConfiguration combinedConfiguration)
      throws ConfigurationException {
    String workingDirectory = System.getProperty("user.dir");
    if (StringUtils.isNotBlank(workingDirectory)) { // working directory can be null
      Path configurationFilePath = Paths.get(workingDirectory, CONFIGURATION_FILE_NAME);
      if (Files.exists(configurationFilePath)) {
        this.loadConfigurationFile(combinedConfiguration, configurationFilePath);
      }
    }
  }

  private void loadClasspathVariableConfigurationFile(CombinedConfiguration combinedConfiguration)
      throws ConfigurationException {
    URL classpathFile = DefaultConfigurationService.class.getResource("/jdwp-over-ws.yaml");
    if (classpathFile != null) {
      this.loadConfigurationURL(combinedConfiguration, classpathFile);
    }
  }

  private void loadDefaultVariableConfigurationFile(CombinedConfiguration combinedConfiguration)
      throws ConfigurationException {

    URL defaultFile = DefaultConfigurationService.class.getResource("/jdwp-over-ws-default.yaml");
    defaultFile = Validate.notNull(defaultFile, "Default configuration is missing!");
    this.loadConfigurationURL(combinedConfiguration, defaultFile);
  }

  private void loadConfigurationURL(CombinedConfiguration combinedConfiguration,
      URL configurationURL)
      throws ConfigurationException {
    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<YAMLConfiguration> defaultConfiguration =
        new FileBasedConfigurationBuilder<>(YAMLConfiguration.class)
            .configure(params.fileBased().setURL(configurationURL));
    combinedConfiguration.addConfiguration(defaultConfiguration.getConfiguration());
  }

  private void loadConfigurationFile(CombinedConfiguration combinedConfiguration,
      Path configurationFilePath)
      throws ConfigurationException {
    Parameters params = new Parameters();
    FileBasedConfigurationBuilder<YAMLConfiguration> envConfiguration =
        new FileBasedConfigurationBuilder<>(YAMLConfiguration.class)
            .configure(params.fileBased().setFile(configurationFilePath.toFile()));
    combinedConfiguration.addConfiguration(envConfiguration.getConfiguration());
  }

  @Override
  public ImmutableHierarchicalConfiguration getConfiguration() {
    return this.configuration;
  }
}
