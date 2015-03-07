package tf.restapi.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class TFServer {
  private static final String DELIMITER = "<@@@>";

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
  private List<Entry<String, Integer>> mWords; // words in decreasing order of their occurance.
  private int mLastReturnedIndex;

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
    Request exitReq =
        new Request(Request.RequestType.valueOf(Request.RequestType.post_execution.name()));
    reponseLinks.put(new Integer(1), exitReq);

    Request uploadReq =
        new Request(Request.RequestType.valueOf(Request.RequestType.get_file_form.name()));
    reponseLinks.put(new Integer(2), uploadReq);

    resp.setLinks(reponseLinks);
    return resp;
  }

  private Response handleFileProcessing(String fileName)
      throws IOException {
    mCurFileName = fileName;
    mWordCount = new HashMap<String, Integer>();
    String inp;
    File file = new File(fileName);
    Scanner in = new Scanner(file);
    // List<String> input = new ArrayList<String>();
    while (in.hasNextLine()) {
      inp = in.nextLine();
      String alphaOnly = inp.replaceAll("[^a-zA-Z]+", " ");
      StringTokenizer st = new StringTokenizer(alphaOnly);
      while (st.hasMoreTokens()) {
        String a = st.nextToken().toLowerCase();
        if (!(a.equalsIgnoreCase(" ") || a.length() == 1) && !sStopWords.contains(a)) {
          if (mWordCount.containsKey(a)) {
            int count = mWordCount.get(a);
            mWordCount.put(a, count + 1);
          } else {
            mWordCount.put(a, 1);
          }
        }
      }
    }
    mWords = entriesSortedByValues(mWordCount);
    // System.out.println(mWordCount);

    return readNextWord(mCurFileName + DELIMITER + String.valueOf(0));
  }

  private static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(
      Map<K, V> map) {

    List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

    Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
      @Override
      public int compare(Entry<K, V> e1, Entry<K, V> e2) {
        return e2.getValue().compareTo(e1.getValue());
      }
    });

    return sortedEntries;
  }

  private Response readNextWord(String args) {
    String[] params = args.split(DELIMITER);
    if (params.length != 2) {
      System.out.println("Something is not right.");
      return null;
    }
    int index = Integer.parseInt(params[1]);
    Response resp = new Response();
    resp.setStatusMessage(MessageFormat.format(
        "\n#{0}: {1} - {2}\n\nWhat would you like to do?\n1 - Quit" + "\n2 - Upload file"
            + "\n3 - See next most-frequently occurring word", String.valueOf(index + 1), mWords
            .get(index).getKey(), mWords.get(index).getValue()));

    Map<Integer, Request> reponseLinks = new HashMap<Integer, Request>();
    Request exitReq =
        new Request(Request.RequestType.valueOf(Request.RequestType.post_execution.name()));
    reponseLinks.put(new Integer(1), exitReq);

    Request uploadReq =
        new Request(Request.RequestType.valueOf(Request.RequestType.get_file_form.name()));
    reponseLinks.put(new Integer(2), uploadReq);

    Request seeNextWordReq =
        new Request(Request.RequestType.valueOf(Request.RequestType.get_word.name()));
    seeNextWordReq.setPayLoad(mCurFileName + DELIMITER + String.valueOf(index + 1));
    reponseLinks.put(new Integer(3), seeNextWordReq);

    resp.setLinks(reponseLinks);


    return resp;
  }

  private Response handleGetFileForm() {
    Response resp = new Response();
    resp.setStatusMessage("Name of file to upload?");

    Map<Integer, Request> reponseLinks = new HashMap<Integer, Request>();
    Request postFileReq =
        new Request(Request.RequestType.valueOf(Request.RequestType.post_file.name()));
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
