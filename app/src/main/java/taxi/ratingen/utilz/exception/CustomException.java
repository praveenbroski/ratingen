package taxi.ratingen.utilz.exception;

public class CustomException extends Exception {

    private int code;

    //   private String exception;

    /**
     * @param code      Api error code
     * @param exception is a exception messages.
     */
    public CustomException(int code, String exception) {
        super(exception);
        this.code = code;
        // this.exception 0= exception;
    }

    //UnUsed methods
    public CustomException(int code, Throwable throwable) {
        this.code = code;
        //   this.exception = throwable.getMessage();
    }

    /* public CustomException(int code, BaseResponse response){
         this.code = code;
         this.exception = response.errorMessage();
     }
 */
    //Its a default constructor.
    public CustomException() {
    }

    /**
     * @return the status code.
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code un used variable.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /*public String getException() {
        return exception;
    }*/

    /*public void setException(String exception) {
        this.exception = exception;
    }*/
}
