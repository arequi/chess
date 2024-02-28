package response;

public class ClearResponse {

    /**
     * result message if clear fails
     */
    public String message;
    /**
     * success or fail boolean
     */
    public Boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ClearResponse(String message, boolean success) {
    }

}
