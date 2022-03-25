package org.ou.gatekeeper.fhir.adapters;

import java.io.File;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public interface FHIRAdapter {

  /**
   * @todo description
   */
  void transform(File dataset, File output);

  /**
   * @todo description
   */
  void transform(File dataset, File output, boolean normalize);

}