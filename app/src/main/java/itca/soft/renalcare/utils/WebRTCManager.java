// WebRTCManager.java
package itca.soft.renalcare.utils;

import android.content.Context;
import android.util.Log;
import org.webrtc.*;
import java.io.IOException;
import okhttp3.*;

public class WebRTCManager {
    private static final String TAG = "WebRTCManager";
    private static final String OPENAI_REALTIME_URL =
            "https://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview";

    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private AudioTrack localAudioTrack;
    private Context context;
    private String sessionToken;
    private OkHttpClient httpClient;

    public interface VoiceConnectionCallback {
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    private VoiceConnectionCallback callback;

    public WebRTCManager(Context context) {
        this.context = context;
        this.httpClient = new OkHttpClient();
        initializePeerConnectionFactory();
    }

    private void initializePeerConnectionFactory() {
        // Inicializar PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions initOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(initOptions);

        // Crear PeerConnectionFactory
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();
    }

    public void startVoiceSession(String token, VoiceConnectionCallback callback) {
        this.sessionToken = token;
        this.callback = callback;

        try {
            // 1. Crear PeerConnection
            createPeerConnection();

            // 2. Agregar audio local
            addLocalAudioTrack();

            // 3. Crear oferta SDP
            createOffer();

        } catch (Exception e) {
            Log.e(TAG, "Error iniciando sesión de voz", e);
            if (callback != null) {
                callback.onError("Error: " + e.getMessage());
            }
        }
    }

    private void createPeerConnection() {
        // Configuración de servidores ICE (no necesarios para OpenAI)
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(new java.util.ArrayList<>());

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState state) {
                Log.d(TAG, "Signaling state: " + state);
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState state) {
                Log.d(TAG, "ICE connection state: " + state);
                if (state == PeerConnection.IceConnectionState.CONNECTED) {
                    if (callback != null) callback.onConnected();
                } else if (state == PeerConnection.IceConnectionState.DISCONNECTED) {
                    if (callback != null) callback.onDisconnected();
                }
            }

            @Override
            public void onIceConnectionReceivingChange(boolean receiving) {}

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState state) {
                Log.d(TAG, "ICE gathering state: " + state);
            }

            @Override
            public void onIceCandidate(IceCandidate candidate) {
                Log.d(TAG, "ICE candidate: " + candidate.sdp);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] candidates) {}

            @Override
            public void onAddStream(MediaStream stream) {
                Log.d(TAG, "Stream added");
                // Aquí se recibe el audio de OpenAI
                if (stream.audioTracks.size() > 0) {
                    AudioTrack remoteAudioTrack = stream.audioTracks.get(0);
                    remoteAudioTrack.setEnabled(true);
                }
            }

            @Override
            public void onRemoveStream(MediaStream stream) {}

            @Override
            public void onDataChannel(DataChannel dataChannel) {}

            @Override
            public void onRenegotiationNeeded() {}

            @Override
            public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {}
        };

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, pcObserver);
    }

    private void addLocalAudioTrack() {
        // Crear fuente de audio
        AudioSource audioSource = peerConnectionFactory.createAudioSource(
                new MediaConstraints());

        // Crear track de audio
        localAudioTrack = peerConnectionFactory.createAudioTrack("audio_track", audioSource);

        // Agregar al PeerConnection
        peerConnection.addTrack(localAudioTrack,
                java.util.Collections.singletonList("stream_id"));
    }

    private void createOffer() {
        MediaConstraints constraints = new MediaConstraints();

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "Offer created");
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sd) {}

                    @Override
                    public void onSetSuccess() {
                        Log.d(TAG, "Local description set");
                        // Enviar oferta a OpenAI
                        sendOfferToOpenAI(sessionDescription.description);
                    }

                    @Override
                    public void onCreateFailure(String error) {}

                    @Override
                    public void onSetFailure(String error) {
                        Log.e(TAG, "Set local description failed: " + error);
                    }
                }, sessionDescription);
            }

            @Override
            public void onSetSuccess() {}

            @Override
            public void onCreateFailure(String error) {
                Log.e(TAG, "Create offer failed: " + error);
                if (callback != null) callback.onError("Error creando oferta");
            }

            @Override
            public void onSetFailure(String error) {}
        }, constraints);
    }

    private void sendOfferToOpenAI(String offerSdp) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/sdp"), offerSdp);

        Request request = new Request.Builder()
                .url(OPENAI_REALTIME_URL)
                .addHeader("Authorization", "Bearer " + sessionToken)
                .addHeader("Content-Type", "application/sdp")
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error enviando oferta", e);
                if (callback != null) callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String answerSdp = response.body().string();
                    Log.d(TAG, "Answer received");

                    SessionDescription answer = new SessionDescription(
                            SessionDescription.Type.ANSWER, answerSdp);

                    peerConnection.setRemoteDescription(new SdpObserver() {
                        @Override
                        public void onCreateSuccess(SessionDescription sd) {}

                        @Override
                        public void onSetSuccess() {
                            Log.d(TAG, "Remote description set - Conectado!");
                        }

                        @Override
                        public void onCreateFailure(String error) {}

                        @Override
                        public void onSetFailure(String error) {
                            Log.e(TAG, "Set remote description failed: " + error);
                        }
                    }, answer);
                } else {
                    Log.e(TAG, "OpenAI error: " + response.code());
                    if (callback != null)
                        callback.onError("Error del servidor: " + response.code());
                }
            }
        });
    }

    public void stopVoiceSession() {
        if (localAudioTrack != null) {
            localAudioTrack.dispose();
            localAudioTrack = null;
        }

        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }

        if (callback != null) {
            callback.onDisconnected();
        }
    }

    public void dispose() {
        stopVoiceSession();
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }
    }
}