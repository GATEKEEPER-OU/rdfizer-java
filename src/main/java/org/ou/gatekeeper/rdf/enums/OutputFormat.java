package org.ou.gatekeeper.rdf.enums;

public enum OutputFormat {

  TURTLE("turtle"),
  NTRIPLES("ntriples"),
  NQUADS("nquads"),
  JSONLD("jsonld"),
  TRIG("trig"),
  TRIX("trix");

  OutputFormat(String s) {
    value = s;
  }

  @Override
  public String toString() {
    return value;
  }

  private String value;

}