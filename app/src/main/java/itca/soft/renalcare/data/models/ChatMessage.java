// ChatMessage.java
package itca.soft.renalcare.data.models;

public class ChatMessage {
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    private String content;
    private int viewType;
    private String imageUrl; // URL de la imagen (si existe)
    private boolean hasImage;

    // Constructor para mensajes de texto
    public ChatMessage(String content, int viewType) {
        this.content = content;
        this.viewType = viewType;
        this.hasImage = false;
        this.imageUrl = null;
    }

    // Constructor para mensajes con imagen
    public ChatMessage(String content, int viewType, String imageUrl) {
        this.content = content;
        this.viewType = viewType;
        this.imageUrl = imageUrl;
        this.hasImage = imageUrl != null && !imageUrl.isEmpty();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.hasImage = imageUrl != null && !imageUrl.isEmpty();
    }

    public boolean hasImage() {
        return hasImage;
    }
}