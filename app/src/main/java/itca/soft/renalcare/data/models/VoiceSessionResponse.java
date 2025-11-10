package itca.soft.renalcare.data.models;

import com.google.gson.annotations.SerializedName;

public class VoiceSessionResponse {
    private String id;
    private String model;

    @SerializedName("expires_at")
    private long expiresAt;

    private String[] modalities;
    private String instructions;
    private String voice;

    @SerializedName("turn_detection")
    private TurnDetection turnDetection;

    @SerializedName("input_audio_format")
    private String inputAudioFormat;

    @SerializedName("output_audio_format")
    private String outputAudioFormat;

    @SerializedName("input_audio_transcription")
    private AudioTranscription inputAudioTranscription;

    private String object;

    @SerializedName("client_secret")
    private ClientSecret clientSecret;

    // Getters
    public String getId() { return id; }
    public String getModel() { return model; }
    public long getExpiresAt() { return expiresAt; }
    public String[] getModalities() { return modalities; }
    public String getInstructions() { return instructions; }
    public String getVoice() { return voice; }
    public ClientSecret getClientSecret() { return clientSecret; }
    public TurnDetection getTurnDetection() { return turnDetection; }

    // Clases internas
    public static class ClientSecret {
        private String value;

        @SerializedName("expires_at")
        private long expiresAt;

        public String getValue() { return value; }
        public long getExpiresAt() { return expiresAt; }
    }

    public static class TurnDetection {
        private String type;
        private double threshold;

        @SerializedName("prefix_padding_ms")
        private int prefixPaddingMs;

        @SerializedName("silence_duration_ms")
        private int silenceDurationMs;

        public String getType() { return type; }
        public double getThreshold() { return threshold; }
        public int getPrefixPaddingMs() { return prefixPaddingMs; }
        public int getSilenceDurationMs() { return silenceDurationMs; }
    }

    public static class AudioTranscription {
        private String model;
        public String getModel() { return model; }
    }
}