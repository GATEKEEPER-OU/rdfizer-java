package org.ou.gatekeeper.rdf.writers;

import java.io.File;

public class FileWriter implements OutputWriter {

  public FileWriter(File output) {
    this.output = output;
  }

  @Override
  public void save(File content) {
    content.renameTo(output);
  }

  @Override
  public void close() {
    // DO NOTHING
  }

  private final File output;

}