package org.ou.gatekeeper.rdf.ontologies;

import org.ou.gatekeeper.rdf.enums.OutputFormat;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public interface Ontology {

  /**
   * @todo description
   */
  String getTemplate();

  /**
   * @todo description
   */
  String getRML();

  /**
   * @todo description
   */
  OutputFormat getFormat();

  /**
   * @todo description
   */
  void setLocalSource(String localSource);

}