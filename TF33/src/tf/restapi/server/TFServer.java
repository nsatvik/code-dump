package tf.restapi.server;

import tf.restapi.server.RequestResponseFactory.RequestType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class TFServer {
  private static Set<String> sStopWords;
  static {
    sStopWords = new HashSet<String>();
    File file = new File("data/stop_words.txt");
    Scanner in;
    try {
      in = new Scanner(file);
      String stopWords = in.nextLine();
      sStopWords.addAll(Arrays.asList(stopWords.split(",")));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  private String mCurFileName;
  private Map<String, Integer> mWordCount;
  private List<String> mWords; // words in decreasing order of their occurance.

  private TFServer() {

  }

  public static TFServer getInstance() { // make it singleton ?
    return new TFServer();
  }

  public Response handleRequest(Request req)
      throws IOException {
    Response resp = null;
    switch (req.getType()) {
      case get_default:
        resp = handleDefaultGetRequest();
        break;
      case post_execution:
        handlePostExecution();
        break;
      case get_file_form:
        resp = handleGetFileForm();
        break;
      case post_file:
        resp = handleFileProcessing(req.getPayload());
        break;
      case get_word:
        resp = readNextWord(req.getPayload());
        break;
      default:
        resp = handleDefaultGetRequest();
        break;
    }
    return resp;
  }

  private Response handleDefaultGetRequest() {
    Response resp = new Response();
    resp.setStatusMessage("What would you like to do?\n1 - Quit" + "\n2 - Upload file");

    Map<Integer, Request> reponseLinks = new HashMap<Integer, Request>();
    Request exitReq = new Request(RequestType.valueOf(RequestType.post_execution.name()));
    reponseLinks.put(new Integer(1), exitReq);

    Request uploadReq = new Request(RequestType.valueOf(RequestType.get_file_form.name()));
    reponseLinks.put(new Integer(2), uploadReq);

    resp.setLinks(reponseLinks);
    return resp;
  }

  private Response handleFileProcessing(String fileName)
      throws IOException {
    String inp;
    File file = new File(fileName);
    Scanner in = new Scanner(file);
    List<String> input = new ArrayList<String>();
    while (in.hasNextLine()) {
      inp = in.nextLine();
      String alphaOnly = inp.replaceAll("[^a-zA-Z]+", " ");
      StringTokenizer st = new StringTokenizer(alphaOnly);
      while (st.hasMoreTokens()) {
        String a = st.nextToken();
        if (!(a.equalsIgnoreCase(" ") || a.length() == 1) && !sStopWords.contains(a)) {
          input.add(a.toLowerCase());
        }
      }
    }
    return null;
  }

  private Response readNextWord(String args) {
    return null;
  }

  private Response handleGetFileForm() {
    Response resp = new Response();
    resp.setStatusMessage("Name of file to upload?");

    Map<Integer, Request> reponseLinks = new HashMap<Integer, Request>();
    Request postFileReq = new Request(RequestType.valueOf(RequestType.post_file.name()));
    reponseLinks.put(new Integer(1), postFileReq); // need to read the file name and set the payload
                                                   // of the request in client side.
    resp.setLinks(reponseLinks);
    return resp;
  }

  private void handlePostExecution() {
    System.out.println("Goodbye cruel world...");
    System.exit(1);
  }
}
