package org.ou.gatekeeper.rdf.stores;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class BlazegraphStore implements OutputStore {

  private String postUrl;
  private CloseableHttpClient httpClient;

  /**
   * @param endpoint
   */
  public static OutputStore create(String endpoint) {
    return new BlazegraphStore(endpoint);
  }

  /**
   * @param endpoint
   */
  protected BlazegraphStore(String endpoint) {
    httpClient = HttpClients.createDefault();
    postUrl = "http://" + endpoint + "/blazegraph/sparql";
  }

  /**
   * @param content
   * @throws IOException
   */
  public boolean save(File content) throws IOException {
    makePost(postUrl, content);
    Files.delete(content.toPath());
    return true; // @todo improve this
  }

  /**
   * @throws IOException
   */
  public void close() throws IOException {
    httpClient.close();
  }

  /**
   * @param postUrl
   * @param content
   * @throws IOException
   */
  private void makePost(String postUrl, File content) throws IOException {
    // Set request headers
    InputStreamEntity reqEntity = new InputStreamEntity(
      new FileInputStream(content),
      -1,
      ContentType.TEXT_PLAIN
    );
    reqEntity.setChunked(true);

    // Init post request
    HttpPost httpPost = new HttpPost(postUrl);
    httpPost.setEntity(reqEntity);

//    System.out.println("Executing request: " + httpPost.getRequestLine()); // DEBUG
    CloseableHttpResponse response = httpClient.execute(httpPost);

//    System.out.println(response.getStatusLine()); // DEBUG
//    System.out.println(EntityUtils.toString(response.getEntity())); // DEBUG
    response.close();
  }

}