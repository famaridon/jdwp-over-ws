package com.famaridon.tcpoverws.cdi;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;

public interface ConfigurationService {

  ImmutableHierarchicalConfiguration getConfiguration();
}
