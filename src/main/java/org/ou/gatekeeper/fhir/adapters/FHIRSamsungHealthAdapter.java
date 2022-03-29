package org.ou.gatekeeper.fhir.adapters;

import org.ou.gatekeeper.fhir.adapters.helpers.FHIRNormalizer;

import java.io.File;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FHIRSamsungHealthAdapter implements FHIRAdapter {

  /**
   * @todo description
   */
  public static FHIRAdapter create() {
    return new FHIRSamsungHealthAdapter();
  }

  /**
   * @todo description
   */
  public void transform(File dataset, File output) {
    transform(dataset, output, false);
  }

  /**
   * @todo description
   */
  public void transform(File dataset, File output, boolean normalize) {
    if (normalize) {
      FHIRNormalizer.normalize(dataset, output);
    }
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  protected FHIRSamsungHealthAdapter() {}

}