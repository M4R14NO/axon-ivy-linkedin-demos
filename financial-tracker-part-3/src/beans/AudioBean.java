package beans;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Base64;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.document.IDocumentService;
import ch.ivyteam.ivy.workflow.document.IDocument;
import ch.ivyteam.ivy.workflow.document.Path;

@ManagedBean(name="audioBean")
@ViewScoped
public class AudioBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String audioBase64;
    private boolean recording = false;
    private boolean audioReady = false;  // NEU
    private byte[] audioBytes;

    public String getAudioBase64() {
        return audioBase64;
    }

    public void setAudioBase64(String audioBase64) {
        this.audioBase64 = audioBase64;
        updateAudioBytes();
    }
    
    public byte[] getAudioBytes() {
        return audioBytes;
    }
    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public boolean isAudioReady() {
        return audioReady;
    }

    public void startRecording() {
        this.recording = true;
        this.audioReady = false;
        this.audioBase64 = null;  // Alte Aufnahme löschen bei neuem Start
        this.audioBytes = null;
        Ivy.log().info("Recording started");
    }

    public void stopRecording() {
        this.recording = false;
        Ivy.log().info("Recording stopped");
    }

    public void setAudioReady() {
        this.audioReady = true;
        updateAudioBytes();
        Ivy.log().info("Audio is ready, bytes length: " + (audioBytes != null ? audioBytes.length : 0));    }
    
    private void updateAudioBytes() {
        if (audioBase64 != null && !audioBase64.isEmpty()) {
            try {
                String base64Data = audioBase64;
                if (base64Data.contains(",")) {
                    base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
                }
                this.audioBytes = Base64.getDecoder().decode(base64Data);
            } catch (Exception e) {
                Ivy.log().error("Error decoding audio: " + e.toString());
                this.audioBytes = null;
            }
        }
    }

    public void saveAudio() {
        if (audioBase64 == null || audioBase64.isEmpty()) {
            Ivy.log().info("No audio data to save.");
            return;
        }

        if (audioBytes == null) {
            updateAudioBytes();
        }

        if (audioBytes == null) {
            Ivy.log().error("Failed to decode audio bytes");
            return;
        }

        IWorkflowContext wf = IWorkflowContext.current();
        if (wf == null) {
            Ivy.log().info("wf == null");
            throw new IllegalStateException("Kein Workflow-Kontext verfügbar.");
        }

        IDocumentService docService = wf.documents();

        String filename = "aufnahme_" + System.currentTimeMillis() + ".webm";
        String documentPath = "audio/" + filename;
        Path path = new Path(documentPath);

        try {
            IDocument doc = docService.add(path);
            try (InputStream in = new ByteArrayInputStream(audioBytes)) {
                doc.write().withContentFrom(in);
            }
            Ivy.log().info("Audio als Dokument gespeichert: " + documentPath);
            
            // Nach erfolgreichem Speichern NUR audioReady zurücksetzen
            this.audioReady = false;
            // audioBase64 NICHT löschen, damit Audio weiter abspielbar bleibt!
            
        } catch (Exception e) {
            Ivy.log().error("Custom Error Msg.: " + e.toString());
        }
    }
}