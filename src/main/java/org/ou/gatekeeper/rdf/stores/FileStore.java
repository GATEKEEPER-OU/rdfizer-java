package org.ou.gatekeeper.rdf.stores;

import java.io.File;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FileStore implements OutputStore {

  private final File output;

  /**
   * @todo description
   */
  public static OutputStore create(File output) {
    return new FileStore(output);
  }

  /**
   * @todo description
   */
  @Deprecated
  public FileStore(File output) {
    this.output = output;
  }

  /**
   * @todo description
   */
  @Override
  public void save(File content) {
    content.renameTo(output);
  }

  /**
   * @todo description
   */
  @Override
  public void close() {
    // DO NOTHING
  }

}