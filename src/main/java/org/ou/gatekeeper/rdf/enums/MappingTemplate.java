package org.ou.gatekeeper.rdf.enums;

public enum MappingTemplate {

  SAMSUNG("samsung-mapping.template.ttl"),
  PUGLIA("puglia-mapping.template.ttl");

  MappingTemplate(String s) {
    value = s;
  }

  @Override
  public String toString() {
    return "mappings/" + value;
  }

  private String value;

}