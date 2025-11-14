// 📁 data/network/WebSocketService.java
package itca.soft.renalcare.data.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import itca.soft.renalcare.data.models.Mensaje;
import io.socket.client.IO;
import io.socket.client.Socket;

public class WebSocketService {
    private static WebSocketService instance;
    private static final String TAG = "WebSocketService";
    private static final String SERVER_URL = "http://192.168.1.163:3000"; // ⚠️ Cambiar IP

    private Socket socket;
    private ChatWebSocketListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ChatWebSocketListener {
        void onConnect();
        void onDisconnect();
        void onNewMessage(Mensaje mensaje);
        void onMessageEdited(Mensaje mensaje);
        void onMessageDeleted(String idMensaje);
        void onUserJoined(String idUsuario);
        void onUserTyping(String idUsuario, String nombreUsuario);
        void onUserStoppedTyping(String idUsuario);
        void onMessageRead(String idMensaje, String idUsuario);
        void onError(String error);
    }

    private WebSocketService() {
        initSocket();
    }

    public static synchronized WebSocketService getInstance() {
        if (instance == null) {
            instance = new WebSocketService();
        }
        return instance;
    }

    private void initSocket() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.reconnectionAttempts = Integer.MAX_VALUE;
            opts.reconnectionDelay = 1000;

            socket = IO.socket(SERVER_URL, opts);

            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d(TAG, "✅ Conectado al servidor Socket.IO");
                if (listener != null) mainHandler.post(listener::onConnect);
            });

            socket.on("newMessage", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        Mensaje mensaje = parseMensaje((JSONObject) args[0]);
                        if (listener != null)
                            mainHandler.post(() -> listener.onNewMessage(mensaje));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parseando mensaje", e);
                    }
                }
            });

            socket.on("messageEdited", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        Mensaje mensaje = parseMensaje((JSONObject) args[0]);
                        if (listener != null)
                            mainHandler.post(() -> listener.onMessageEdited(mensaje));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parseando mensaje editado", e);
                    }
                }
            });

            socket.on("messageDeleted", args -> {
                if (args.length > 0) {
                    String idMensaje = args[0].toString();
                    if (listener != null)
                        mainHandler.post(() -> listener.onMessageDeleted(idMensaje));
                }
            });

            socket.on("userJoined", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        String idUsuario = ((JSONObject) args[0]).optString("id_usuario", "");
                        if (listener != null)
                            mainHandler.post(() -> listener.onUserJoined(idUsuario));
                    } catch (Exception e) {
                        Log.e(TAG, "Error en userJoined", e);
                    }
                }
            });

            socket.on("userTyping", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject json = (JSONObject) args[0];
                        String idUsuario = json.optString("id_usuario", "");
                        String nombreUsuario = json.optString("nombre_usuario", "");
                        if (listener != null)
                            mainHandler.post(() -> listener.onUserTyping(idUsuario, nombreUsuario));
                    } catch (Exception e) {
                        Log.e(TAG, "Error en userTyping", e);
                    }
                }
            });

            socket.on("userStoppedTyping", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        String idUsuario = ((JSONObject) args[0]).optString("id_usuario", "");
                        if (listener != null)
                            mainHandler.post(() -> listener.onUserStoppedTyping(idUsuario));
                    } catch (Exception e) {
                        Log.e(TAG, "Error en userStoppedTyping", e);
                    }
                }
            });

            socket.on("messageRead", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject json = (JSONObject) args[0];
                        String idMensaje = json.optString("id_mensaje", "");
                        String idUsuario = json.optString("id_usuario", "");
                        if (listener != null)
                            mainHandler.post(() -> listener.onMessageRead(idMensaje, idUsuario));
                    } catch (Exception e) {
                        Log.e(TAG, "Error en messageRead", e);
                    }
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.d(TAG, "❌ Desconectado del servidor");
                if (listener != null) mainHandler.post(listener::onDisconnect);
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                String error = args.length > 0 ? args[0].toString() : "Error desconocido";
                Log.e(TAG, "❌ Error de conexión: " + error);
                if (listener != null)
                    mainHandler.post(() -> listener.onError(error));
            });

        } catch (Exception e) {
            Log.e(TAG, "Error inicializando Socket.IO", e);
        }
    }

    public void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
            Log.d(TAG, "🔗 Intentando conectar...");
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            Log.d(TAG, "🔌 Desconectando...");
        }
    }

    public void registerUser(String idUsuario) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_usuario", idUsuario);
                socket.emit("registerUser", data);
                Log.d(TAG, "📝 Usuario registrado: " + idUsuario);
            } catch (JSONException e) {
                Log.e(TAG, "Error registrando usuario", e);
            }
        }
    }

    public void joinRoom(String idConversacion, String idUsuario) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_conversacion", idConversacion);
                data.put("id_usuario", idUsuario);
                socket.emit("joinRoom", data);
                Log.d(TAG, "📍 Uniéndose a la conversación: " + idConversacion);
            } catch (JSONException e) {
                Log.e(TAG, "Error uniéndose a sala", e);
            }
        }
    }

    public void sendMessage(String idConversacion, String idEmisor, String contenido, String nombreEmisor) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_conversacion", idConversacion);
                data.put("id_emisor", idEmisor);
                data.put("contenido", contenido);
                data.put("tipo", "texto");
                data.put("nombre_emisor", nombreEmisor);
                socket.emit("sendMessage", data);
                Log.d(TAG, " Mensaje enviado");
            } catch (JSONException e) {
                Log.e(TAG, "Error enviando mensaje", e);
            }
        }
    }

    public void editMessage(String idMensaje, String idConversacion, String idEmisor, String contenido) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_mensaje", idMensaje);
                data.put("id_conversacion", idConversacion);
                data.put("id_emisor", idEmisor);
                data.put("contenido", contenido);
                data.put("tipo", "texto");
                socket.emit("editMessage", data);
                Log.d(TAG, "✏️ Mensaje editado");
            } catch (JSONException e) {
                Log.e(TAG, "Error editando mensaje", e);
            }
        }
    }

    public void deleteMessage(String idMensaje, String idConversacion) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_mensaje", idMensaje);
                data.put("id_conversacion", idConversacion);
                socket.emit("deleteMessage", data);
                Log.d(TAG, "🗑️ Mensaje marcado para eliminar");
            } catch (JSONException e) {
                Log.e(TAG, "Error eliminando mensaje", e);
            }
        }
    }

    public void notifyTyping(String idConversacion, String idUsuario, String nombreUsuario) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_conversacion", idConversacion);
                data.put("id_usuario", idUsuario);
                data.put("nombre_usuario", nombreUsuario);
                socket.emit("typing", data);
            } catch (JSONException e) {
                Log.e(TAG, "Error notificando typing", e);
            }
        }
    }

    public void notifyStopTyping(String idConversacion, String idUsuario) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_conversacion", idConversacion);
                data.put("id_usuario", idUsuario);
                socket.emit("stopTyping", data);
            } catch (JSONException e) {
                Log.e(TAG, "Error notificando stop typing", e);
            }
        }
    }

    public void markMessageAsRead(String idConversacion, String idUsuario, String idMensaje) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("id_conversacion", idConversacion);
                data.put("id_usuario", idUsuario);
                data.put("id_mensaje", idMensaje);
                socket.emit("markAsRead", data);
            } catch (JSONException e) {
                Log.e(TAG, "Error marcando como leído", e);
            }
        }
    }

    public void setListener(ChatWebSocketListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    private Mensaje parseMensaje(JSONObject json) throws JSONException {
        Mensaje mensaje = new Mensaje(
                json.optString("id_mensaje", ""),
                json.optString("id_conversacion", ""),
                json.optString("id_emisor", ""),
                json.optString("contenido", ""),
                json.optString("tipo", "texto"),
                json.optString("fecha_envio", "")
        );
        mensaje.setNombre_emisor(json.optString("nombre_emisor", "Usuario"));
        return mensaje;
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }
}