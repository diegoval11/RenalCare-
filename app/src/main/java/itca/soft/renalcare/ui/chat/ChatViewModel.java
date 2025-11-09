// ChatViewModel.java - COMPLETO
package itca.soft.renalcare.ui.chat;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import itca.soft.renalcare.data.models.ChatMessage;
import itca.soft.renalcare.data.models.ConversacionItem;
import itca.soft.renalcare.data.models.ConversacionesResponse;
import itca.soft.renalcare.data.models.MensajeResponse;
import itca.soft.renalcare.data.repository.ChatRepository;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository repository;
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<ConversacionItem>> conversaciones = new MutableLiveData<>();
    private Integer idConversacionActual = null;

    public ChatViewModel() {
        repository = new ChatRepository();
        messages.setValue(new ArrayList<>());
        isLoading.setValue(false);
        conversaciones.setValue(new ArrayList<>());
    }

    // ========== GETTERS ==========

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<ConversacionItem>> getConversaciones() {
        return conversaciones;
    }

    // ========== ENVIAR MENSAJES ==========

    public void enviarMensaje(int idUsuario, String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) return;

        agregarMensaje(new ChatMessage(mensaje, ChatMessage.VIEW_TYPE_SENT));
        isLoading.setValue(true);

        repository.enviarMensaje(idUsuario, mensaje, idConversacionActual)
                .observeForever(result -> {
                    if (result.getStatus() == ChatRepository.Result.Status.SUCCESS) {
                        MensajeResponse response = result.getData();
                        idConversacionActual = response.getIdConversacion();

                        agregarMensaje(new ChatMessage(
                                response.getMensajeIa(),
                                ChatMessage.VIEW_TYPE_RECEIVED
                        ));

                        isLoading.setValue(false);

                        // Recargar lista de conversaciones
                        cargarConversaciones(idUsuario);
                    } else if (result.getStatus() == ChatRepository.Result.Status.ERROR) {
                        agregarMensaje(new ChatMessage(
                                "Error: " + result.getMessage(),
                                ChatMessage.VIEW_TYPE_RECEIVED
                        ));
                        isLoading.setValue(false);
                    }
                });
    }

    public void enviarMensajeConImagen(int idUsuario, String mensaje, Uri imageUri) {
        String contenido = mensaje != null && !mensaje.trim().isEmpty()
                ? mensaje : "";

        // Agregar mensaje con imagen del usuario
        agregarMensaje(new ChatMessage(contenido, ChatMessage.VIEW_TYPE_SENT, imageUri.toString()));
        isLoading.setValue(true);

        repository.enviarMensajeConImagen(idUsuario, mensaje, idConversacionActual, imageUri)
                .observeForever(result -> {
                    if (result.getStatus() == ChatRepository.Result.Status.SUCCESS) {
                        MensajeResponse response = result.getData();
                        idConversacionActual = response.getIdConversacion();

                        // Respuesta de la IA solo texto (sin imagen)
                        agregarMensaje(new ChatMessage(
                                response.getMensajeIa(),
                                ChatMessage.VIEW_TYPE_RECEIVED
                        ));

                        isLoading.setValue(false);

                        // Recargar lista de conversaciones
                        cargarConversaciones(idUsuario);
                    } else if (result.getStatus() == ChatRepository.Result.Status.ERROR) {
                        agregarMensaje(new ChatMessage(
                                "Error: " + result.getMessage(),
                                ChatMessage.VIEW_TYPE_RECEIVED
                        ));
                        isLoading.setValue(false);
                    }
                });
    }

    // ========== CONVERSACIONES ==========

    public void cargarConversaciones(int idUsuario) {
        repository.obtenerConversaciones(idUsuario)
                .observeForever(result -> {
                    if (result.getStatus() == ChatRepository.Result.Status.SUCCESS) {
                        List<ConversacionesResponse.ConversacionData> data = result.getData();

                        // Convertir a ConversacionItem
                        List<ConversacionItem> items = new ArrayList<>();
                        if (data != null) {
                            for (ConversacionesResponse.ConversacionData conv : data) {
                                items.add(new ConversacionItem(
                                        conv.getIdConversacion(),
                                        conv.getTitulo(),
                                        conv.getFechaCreacion()
                                ));
                            }
                        }

                        conversaciones.setValue(items);
                    }
                });
    }

    public void cargarMensajesDeConversacion(int idUsuario, int idConversacion) {
        idConversacionActual = idConversacion;
        isLoading.setValue(true);

        // Limpiar mensajes actuales
        messages.setValue(new ArrayList<>());

        repository.obtenerConversacion(idUsuario, idConversacion)
                .observeForever(result -> {
                    if (result.getStatus() == ChatRepository.Result.Status.SUCCESS) {
                        ConversacionesResponse.ConversacionData conversacion = result.getData();

                        List<ChatMessage> mensajesCargados = new ArrayList<>();
                        if (conversacion.getMensajes() != null) {
                            for (ConversacionesResponse.MensajeData msg : conversacion.getMensajes()) {
                                int viewType = msg.getRemitente().equals("usuario")
                                        ? ChatMessage.VIEW_TYPE_SENT
                                        : ChatMessage.VIEW_TYPE_RECEIVED;

                                mensajesCargados.add(new ChatMessage(
                                        msg.getContenido(),
                                        viewType
                                ));
                            }
                        }

                        messages.setValue(mensajesCargados);
                        isLoading.setValue(false);
                    } else if (result.getStatus() == ChatRepository.Result.Status.ERROR) {
                        messages.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                    }
                });
    }

    // Sobrecarga para compatibilidad con el fragment
    public void cargarMensajesDeConversacion(int idConversacion) {
        // Usa un ID de usuario por defecto o guárdalo en el ViewModel
        cargarMensajesDeConversacion(1, idConversacion);
    }

    public void nuevaConversacion() {
        idConversacionActual = null;
        messages.setValue(new ArrayList<>());
    }

    // ========== HELPERS ==========

    private void agregarMensaje(ChatMessage mensaje) {
        List<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(mensaje);
            messages.setValue(currentMessages);
        }
    }
}