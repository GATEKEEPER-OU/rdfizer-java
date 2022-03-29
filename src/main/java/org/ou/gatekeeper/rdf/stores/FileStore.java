package org.ou.gatekeeper.rdf.stores;

import java.io.File;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FileStore implements OutputStore {

  /**
   * @todo description
   */
  public static OutputStore create(File output) {
    return new FileStore(output);
  }

  /**
   * @todo description
   */
  public boolean save(File content) {
    return content.renameTo(output);
  }

  /**
   * @todo description
   */
  public void close() {
    // DO NOTHING
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private final File output;

  /**
   * @todo description
   */
  protected FileStore(File output) {
    this.output = output;
  }

}