<<<<<<< Updated upstream
package rdf.strategies;
=======
package org.ou.gatekeeper.rdf.strategies;
>>>>>>> Stashed changes

import java.io.File;
import java.io.IOException;

/**
 *
 * */
public interface OutputSaver {

  /**
   *
   * @param content
   * */
  public void save(File content) throws IOException;

  /**
   *
   * */
  public void close() throws IOException;

}