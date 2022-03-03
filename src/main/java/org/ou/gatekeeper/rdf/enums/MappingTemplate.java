<<<<<<< Updated upstream
package rdf.enums;
=======
package org.ou.gatekeeper.rdf.enums;
>>>>>>> Stashed changes

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