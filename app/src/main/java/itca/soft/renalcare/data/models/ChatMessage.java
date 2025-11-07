package itca.soft.renalcare.data.models;

public class ChatMessage {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    private String message;
    private int viewType;

    public ChatMessage(String message, int viewType) {
        this.message = message;
        this.viewType = viewType;
    }

    public String getMessage() {
        return message;
    }
    public int getViewType() {
        return viewType;
    }
}