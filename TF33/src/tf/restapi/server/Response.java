package tf.restapi.server;

import tf.restapi.server.Request.RequestType;

import java.util.Map;

public class Response {
  private String mStatusMessage;
  private Map<Integer, Request> mLinks;

  public Response() {}

  public void setStatusMessage(String msg) {
    this.mStatusMessage = msg;
  }

  public String getStatusMessage() {
    return mStatusMessage;
  }

  public void setLinks(Map<Integer, Request> links) {
    this.mLinks = links;
  }

  public Request getRequest(int i) {
    if (mLinks != null && mLinks.containsKey(i)) {
      return mLinks.get(i);
    }
    return new Request(RequestType.get_default);
  }
}
