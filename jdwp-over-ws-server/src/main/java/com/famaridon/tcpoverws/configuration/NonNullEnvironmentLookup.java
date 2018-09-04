package com.famaridon.tcpoverws.configuration;

import org.apache.commons.configuration2.interpol.Lookup;

public class NonNullEnvironmentLookup implements Lookup {

  @Override
  public Object lookup(String key) {
    String value = System.getenv(key);
    if (value == null) {
      value = "";
    }
    return value;
  }
}
