package tf.restapi.server;

public class Request {
  public enum RequestType {
    get_default, get_file_form, post_file, get_word, post_execution
  };

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
