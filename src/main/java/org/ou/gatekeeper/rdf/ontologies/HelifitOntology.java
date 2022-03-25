package org.ou.gatekeeper.rdf.ontologies;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class HelifitOntology implements Ontology {

  /**
   * Relative path of the resource samsung where are located all mapping parts
   */
  public static final String MAPPING_DIR = "mappings/helifit";

  /**
   * @todo description
   */
  public static Ontology create(OutputFormat format) {
    return new HelifitOntology(format, "", null, false);
  }

  /**
   * @todo description
   */
  public static Ontology create(OutputFormat format, String localSource) {
    return new HelifitOntology(format, localSource, null, false);
  }

  /**
   * @todo description
   */
  public static Ontology create(
    OutputFormat format, String localSource, String[] partsToInclude, boolean trimBaseUrl
  ) {
    return new HelifitOntology(format, localSource, partsToInclude, trimBaseUrl);
  }

  /**
   * @todo description
   */
  public String getTemplate() {
    if (template == null) {
      try {
        template = composeTemplate();
      } catch (IOException e) {
        // @todo print a message?
        e.printStackTrace();
      }
    }
    return template;
  }
//  private static String template; // @todo optimize

  /**
   * @todo description
   */
  public String getRML () {
    // @todo optimize
//    if (mapping == null) {
      // @todo check if source is initialized of throw an exception
      String theTemplate = getTemplate();
      mapping = theTemplate.replace("__RML_SRC__", localSource);
      if (trimBaseUrl) {
        mapping = mapping.replace("www.samsung.com/health", "_");
      }
//    }
    return mapping;
  }

  /**
   * @todo description
   */
  public OutputFormat getFormat() {
    return format;
  }

  /**
   * @todo description
   */
  public void setLocalSource(String localSource) {
    this.localSource = localSource;
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private String localSource;

  /**
   * @todo description
   */
  private OutputFormat format;

  /**
   * DO NOT access directly to this property.
   * Use lazy loading method {@link HelifitOntology#getRML()}
   */
  private String mapping;

  /**
   * DO NOT access directly to this property.
   * Use lazy loading method {@link HelifitOntology#getTemplate()}
   */
  private String template;

  /**
   * @todo description
   */
  private String[] partsToInclude;

  /**
   * @todo description
   */
  private boolean trimBaseUrl;

  /**
   * @todo description
   */
  protected HelifitOntology(OutputFormat format, String localSource, String[] partsToInclude, boolean trimBaseUrl) {
    this.format = format;
    this.localSource = localSource;
    this.partsToInclude = partsToInclude;
    this.trimBaseUrl = trimBaseUrl;
  }

  //--------------------------------------------------------------------------//
  // Private methods
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private String composeTemplate() throws IOException {
    StringBuilder content = new StringBuilder();
    ArrayList<File> parts = collectTemplateParts();
    for (File part : parts) {
      // @todo refactory compose method
      //  - compose ALL parts
      //  - compose by whitelist
      //  - compose by blacklist
      if (this.partsToInclude == null || StringUtils.startsWithAny(part.getName(), this.partsToInclude)) {
//        System.out.println("---> composeTemplate().partname: " + part.getName()); // DEBUG
        String partContent = FileUtils.readFileToString(part, StandardCharsets.UTF_8);
        content.append(partContent + "\n\n");
      }
    }
    return content.toString();
  }

  /**
   * @todo description
   */
  private ArrayList<File> collectTemplateParts() {
    ArrayList<File> parts = new ArrayList<>();
    String[] exts = {"ttl"};
    Iterator<File> ttls = ResourceUtils.getResourceFiles(MAPPING_DIR, exts);
    while (ttls.hasNext()) {
      File ttl = ttls.next();
      parts.add(ttl);
    }
    parts.sort(Comparator.comparing(File::getName));
    return parts;
  }

}