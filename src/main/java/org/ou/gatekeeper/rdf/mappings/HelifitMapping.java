package org.ou.gatekeeper.rdf.mappings;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.commons.ResourceUtils;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class HelifitMapping implements RMLMapping {

  /**
   * @todo description
   */
  public static RMLMapping create() {
    return new HelifitMapping(
      null,
      "",
      null,
      false
    );
  }

  /**
   * @todo description
   */
  public static RMLMapping create(OutputFormat format) {
    return new HelifitMapping(
      format,
      "",
      null,
      false
    );
  }

  /**
   * @todo description
   */
  public static RMLMapping create(
    OutputFormat format,
    String[] partsToInclude,
    boolean trimBaseUrl
  ) {
    // @todo check if running in a jar and throw an exception.
    //       This method in design for test only.
    // Example:
    //   WinProcessor.class.getResource("repository").toString(); returns
    //     file:/root/app/repository
    //     jar:/root/app.jar!/repository
    return new HelifitMapping(
      format,
      "",
      partsToInclude,
      trimBaseUrl
    );
  }

  /**
   * @todo description
   */
  public String getTemplate() {
    if (template == null) {
      if (partsToInclude == null) {
        template = getTemplateFromBundle();
      } else {
        template = composeTemplate();
      }
    }
    return template;
  }

  /**
   * @todo description
   */
  public String getRML () {
    // @todo if localSource == null -> exception
    if (mappingDirtyBit) {
      mapping = getTemplate().replace("__RML_SRC__", localSource);
      if (trimBaseUrl) {
//        mapping = mapping.replace("semweb.mmlab.be/ns/rml", "__rml");
//        mapping = mapping.replace("www.w3.org/ns/r2rml", "__rr");
//        mapping = mapping.replace("semweb.mmlab.be/ns/ql", "__ql");
        mapping = mapping.replace("www.w3.org/2001/XMLSchema", "__xsd");
        mapping = mapping.replace("purl.obolibrary.org/obo", "__doid");
//        mapping = mapping.replace("", "__ho");
        mapping = mapping.replace("opensource.samsung.com/projects/helifit", "__base");
      }
      mappingDirtyBit = false;
    }
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
    mappingDirtyBit = true;
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
   * Use lazy loading method {@link HelifitMapping#getRML()}
   */
  private String mapping;

  /**
   * DO NOT access directly to this property.
   * Use lazy loading method {@link HelifitMapping#getTemplate()}
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
  private boolean mappingDirtyBit;

  /**
   * @todo description
   */
  private static final String TEMPLATE_PATH = "mappings/helifit/helifit.template.ttl"; // @note DO NOT use Path.of()
                                                                                       //

  private static final Logger LOGGER = LoggerFactory.getLogger(HelifitMapping.class);

  /**
   * @todo description
   */
  protected HelifitMapping(
    OutputFormat format,
    String localSource,
    String[] partsToInclude,
    boolean trimBaseUrl
  ) {
    this.format = format;
    this.localSource = localSource;
    this.partsToInclude = partsToInclude;
    this.trimBaseUrl = trimBaseUrl;
    mappingDirtyBit = true;
  }

  //--------------------------------------------------------------------------//
  // Private methods
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private String getTemplateFromBundle() {
    try (InputStream stream = ResourceUtils.getResourceAsStream(TEMPLATE_PATH)) {
      return IOUtils.toString(stream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOGGER.error("helifit.template.ttl not found");
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @todo description
   */
  private String composeTemplate()  {
    StringBuilder content = new StringBuilder();
    List<File> parts = collectTemplateParts();
    for (File part : parts) {
      // @todo refactory compose method
      //  - compose ALL parts
      //  - compose by whitelist
      //  - compose by blacklist
      if ( this.partsToInclude == null
        || StringUtils.startsWithAny(part.getName(), this.partsToInclude)
      ) {
        String partContent = ResourceUtils.readFileToString(part);
        content.append(partContent + "\n\n");
      }
    }
    return content.toString();
  }

  /**
   * @todo description
   */
  private List<File> collectTemplateParts() {
    String[] exts = {"ttl"};
    Path ontologyPartsPath = Path.of("mappings", "helifit", "parts");
    Collection<File> parts = ResourceUtils.getResourceFiles(ontologyPartsPath, exts);
    List<File> ttls = new ArrayList<>(parts);
    ttls.sort(Comparator.comparing(File::getName));
    return ttls;
  }

}