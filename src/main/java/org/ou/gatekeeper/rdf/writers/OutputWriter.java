package org.ou.gatekeeper.rdf.writers;

import java.io.File;
import java.io.IOException;

/**
 *
 * */
public interface OutputWriter {

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