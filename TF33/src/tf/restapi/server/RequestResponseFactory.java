package tf.restapi.server;

public class RequestResponseFactory {


  public enum RequestType {
    get_default, get_file_form, post_file, get_word, post_execution
  };
  public enum ResponseType {
    get_default_resp, get_file_form_resp, post_file_resp, get_word_resp, post_execution_resp
  };

  private static Request sDefaultReq = new Request(RequestType.get_default);
  private static Request sGetFileFormReq = new Request(RequestType.get_file_form);
  private static Request sPostFileReq = new Request(RequestType.post_file);
  private static Request sGetWordReq = new Request(RequestType.get_word);
  private static Request sPostExecutionReq = new Request(RequestType.post_execution);

  public static Request getInstance(RequestType type) {
    switch (type) {
      case get_default:
        return sDefaultReq;
      case get_file_form:
        return sGetFileFormReq;
      case post_file:
        return sPostFileReq; // may be return a new instance.
      case get_word:
        return sGetWordReq;
      case post_execution:
        return sPostExecutionReq;
      default:
        return sDefaultReq;
    }
  }
}
