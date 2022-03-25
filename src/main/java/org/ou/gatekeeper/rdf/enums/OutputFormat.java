package org.ou.gatekeeper.rdf.enums;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
public enum OutputFormat {

  TURTLE("turtle"),
  NTRIPLES("ntriples"),
  NQUADS("nquads"),
  JSONLD("jsonld"),
  TRIG("trig"),
  TRIX("trix");

  private String value;

  OutputFormat(String s) {
    value = s;
  }

  @Override
  public String toString() {
    return value;
  }

}