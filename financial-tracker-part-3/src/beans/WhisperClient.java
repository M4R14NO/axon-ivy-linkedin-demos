package beans;

import java.io.ByteArrayInputStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import ch.ivyteam.ivy.environment.Ivy;

/**
 * Client für Whisper ASR Webservice
 */
public class WhisperClient {
    
	private static final String DEFAULT_WHISPER_URL = Ivy.var().get("Whisper.asrBaseUrl");
    
    /**
     * Transkribiert Audio-Daten mit Whisper ASR
     * 
     * @param audioBytes Die Audio-Daten als byte array
     * @param whisperUrl Die URL des Whisper Servers (z.B. "http://localhost:9000")
     * @param language Sprache (z.B. "de", "en" oder null für Auto-Detection)
     * @return Transkribierter Text oder Fehlermeldung
     */
    public static String transcribe(byte[] audioBytes, String whisperUrl, String language) {
        if (audioBytes == null || audioBytes.length == 0) {
            Ivy.log().warn("Keine Audio-Daten zum Transkribieren vorhanden");
            return "Fehler: Keine Audio-Daten verfügbar";
        }
        
        if (whisperUrl == null || whisperUrl.isEmpty()) {
            whisperUrl = DEFAULT_WHISPER_URL;
        }
        
        Client client = null;
        FormDataMultiPart multiPart = null;
        ByteArrayInputStream inputStream = null;
        
        try {
            Ivy.log().info("Sende " + audioBytes.length + " bytes an Whisper ASR: " + whisperUrl);
            
            // 1. InputStream aus byte[] erstellen
            inputStream = new ByteArrayInputStream(audioBytes);
            
            // 2. Jersey Client erstellen mit MultiPart Support
            client = ClientBuilder.newClient();
            client.register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
            
            // 3. MultiPart Form erstellen
            multiPart = new FormDataMultiPart();
            
            // 4. Audio-Daten als Stream hinzufügen
            StreamDataBodyPart streamPart = new StreamDataBodyPart(
                "audio_file", 
                inputStream,
                "audio.webm",
                MediaType.APPLICATION_OCTET_STREAM_TYPE
            );
            
            multiPart.bodyPart(streamPart);
            
            // 5. REST-Call ausführen
            var request = client.target(whisperUrl)
                .path("/asr")
                .queryParam("task", "transcribe")
                .queryParam("output", "txt")  // "txt", "json", "srt", "vtt", "tsv"
                .queryParam("encode", "true");
            
            // Language nur setzen wenn angegeben
            if (language != null && !language.isEmpty()) {
                request = request.queryParam("language", language);
            }
            
            String response = request
                .request()
                .post(
                    Entity.entity(multiPart, multiPart.getMediaType()), 
                    String.class
                );
            
            Ivy.log().info("Whisper Response erfolgreich empfangen");
            return response;
            
        } catch (Exception e) {
            Ivy.log().error("Fehler beim Whisper REST-Call: " + e.getMessage(), e);
            return "Fehler: " + e.getMessage();
        } finally {
            // Aufräumen
            try {
                if (multiPart != null) multiPart.close();
                if (client != null) client.close();
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                Ivy.log().warn("Fehler beim Schließen der Ressourcen: " + e.getMessage());
            }
        }
    }
    
    /**
     * Transkribiert Audio-Daten mit Standard-URL und Auto-Detection der Sprache
     * 
     * @param audioBytes Die Audio-Daten als byte array
     * @return Transkribierter Text oder Fehlermeldung
     */
    public static String transcribe(byte[] audioBytes) {
        return transcribe(audioBytes, DEFAULT_WHISPER_URL, null);
    }
    
    /**
     * Transkribiert Audio-Daten mit Standard-URL
     * 
     * @param audioBytes Die Audio-Daten als byte array
     * @param language Sprache (z.B. "de", "en" oder null für Auto-Detection)
     * @return Transkribierter Text oder Fehlermeldung
     */
    public static String transcribe(byte[] audioBytes, String language) {
        return transcribe(audioBytes, DEFAULT_WHISPER_URL, language);
    }
    
    /**
     * Erkennt die Sprache der Audio-Daten
     * 
     * @param audioBytes Die Audio-Daten als byte array
     * @param whisperUrl Die URL des Whisper Servers
     * @return Erkannte Sprache als JSON oder Fehlermeldung
     */
    public static String detectLanguage(byte[] audioBytes, String whisperUrl) {
        if (audioBytes == null || audioBytes.length == 0) {
            Ivy.log().warn("Keine Audio-Daten zur Spracherkennung vorhanden");
            return "Fehler: Keine Audio-Daten verfügbar";
        }
        
        if (whisperUrl == null || whisperUrl.isEmpty()) {
            whisperUrl = DEFAULT_WHISPER_URL;
        }
        
        Client client = null;
        FormDataMultiPart multiPart = null;
        ByteArrayInputStream inputStream = null;
        
        try {
            Ivy.log().info("Erkenne Sprache von " + audioBytes.length + " bytes");
            
            inputStream = new ByteArrayInputStream(audioBytes);
            client = ClientBuilder.newClient();
            client.register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
            multiPart = new FormDataMultiPart();
            
            StreamDataBodyPart streamPart = new StreamDataBodyPart(
                "audio_file", 
                inputStream,
                "audio.webm",
                MediaType.APPLICATION_OCTET_STREAM_TYPE
            );
            
            multiPart.bodyPart(streamPart);
            
            String response = client.target(whisperUrl)
                .path("/detect-language")
                .queryParam("encode", "true")
                .request()
                .post(
                    Entity.entity(multiPart, multiPart.getMediaType()), 
                    String.class
                );
            
            Ivy.log().info("Spracherkennung erfolgreich");
            return response;
            
        } catch (Exception e) {
            Ivy.log().error("Fehler bei der Spracherkennung: " + e.getMessage(), e);
            return "Fehler: " + e.getMessage();
        } finally {
            try {
                if (multiPart != null) multiPart.close();
                if (client != null) client.close();
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                Ivy.log().warn("Fehler beim Schließen der Ressourcen: " + e.getMessage());
            }
        }
    }
}