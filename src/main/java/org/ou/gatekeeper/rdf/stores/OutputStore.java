package org.ou.gatekeeper.rdf.stores;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public interface OutputStore extends Closeable {

  /**
   * @param content
   */
  public boolean save(File content) throws IOException;

}