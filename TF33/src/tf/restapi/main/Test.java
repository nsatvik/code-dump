package tf.restapi.main;

import tf.restapi.server.Request;
import tf.restapi.server.RequestResponseFactory;
import tf.restapi.server.RequestResponseFactory.RequestType;
import tf.restapi.server.Response;
import tf.restapi.server.TFServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {


  public static void main(String args[])
      throws IOException {
    Request req = RequestResponseFactory.getInstance(RequestType.get_default);
    TFServer server = TFServer.getInstance();
    while (true) {
      System.out.println("handling request : " + req.getType());
      Response resp = server.handleRequest(req);
      req = interpretServerResponse(req.getType(), resp);
    }
  }

  private static Request interpretServerResponse(RequestType lastReqtype, Response resp)
      throws IOException {
    System.out.println(resp.getStatusMessage());
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    String input = null;

    while ((input = br.readLine()) != null) {
      break;
    }
    Request req = null;
    if (lastReqtype == RequestType.get_file_form) {
      req = resp.getRequest(1); // see in handelGetFileForm.
      // set the payload of the request as the filename.
      req.setPayLoad(input);// TODO change to user given value.
    } else {
      // read an int and get the request
      req = resp.getRequest(Integer.parseInt(input));
    }

    return req;
  }
}
