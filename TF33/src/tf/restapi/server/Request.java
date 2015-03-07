package tf.restapi.server;

import tf.restapi.server.RequestResponseFactory.RequestType;

public class Request {
  private RequestType mReqType;
  private String mPayload; // payload contains the necessary args for server to process.

  public Request(RequestType type) {
    this.mReqType = type;
  }

  public RequestType getType() {
    return mReqType;
  }

  public void setPayLoad(String payload) {
    this.mPayload = payload;
  }

  public String getPayload() {
    return mPayload;
  }
}
