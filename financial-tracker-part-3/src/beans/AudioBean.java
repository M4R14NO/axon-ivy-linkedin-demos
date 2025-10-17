package beans;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Base64;

import javax.faces.bean.ManagedBean;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.document.IDocumentService;
import ch.ivyteam.ivy.workflow.document.IDocument;
import ch.ivyteam.ivy.workflow.document.Path;

@ManagedBean(name="audioBean")
public class AudioBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String audioBase64;

    public String getAudioBase64() {
        return audioBase64;
    }

    public void setAudioBase64(String audioBase64) {
        this.audioBase64 = audioBase64;
    }

    public void saveAudio() {
        if (audioBase64 == null || audioBase64.isEmpty()) {
        	Ivy.log().info("No audio data to save.");
            //Ivy("⚠️ No audio data to save.");
            return;
        }

        // Entferne data-URL-Prefix, falls vorhanden
        String base64Data = audioBase64;
        if (base64Data.contains(",")) {
            base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
        }

        byte[] audioBytes;
        try {
            audioBytes = Base64.getDecoder().decode(base64Data);
            Ivy.log().info("Audio Bytes: " + audioBytes.toString());
        } catch (IllegalArgumentException e) {
        	Ivy.log().error("Custom Error Msg.: " + e.toString());
            //e.printStackTrace();
            return;
        }

        // Workflow-Kontext holen
        IWorkflowContext wf = IWorkflowContext.current();
        if (wf == null) {
        	Ivy.log().info("wf == null");
            // ggf. Fehlerbehandlung, wenn außerhalb des Workflow-Kontexts
            throw new IllegalStateException("Kein Workflow-Kontext verfügbar.");
        }

        IDocumentService docService = wf.documents();

        // Wähle einen Pfad für das Dokument – z. B. „audio/aufnahmeXYZ.webm“
        // Du könntest evtl. einen Zeitstempel oder UUID verwenden, um Konflikte zu vermeiden
        String filename = "aufnahme_" + System.currentTimeMillis() + ".webm";
        String documentPath = "audio/" + filename;
        Path path = new Path(documentPath);

        try {
            // Document hinzufügen
            IDocument doc = docService.add(path);

            // Inhalt schreiben
            try (InputStream in = new ByteArrayInputStream(audioBytes)) {
                doc.write().withContentFrom(in);
            }

            Ivy.log().info("Audio als Dokument gespeichert: " + documentPath);
        } catch (Exception e) {
            // z. B. Persistenz-Fehler oder Schreibrechte fehlen
        	Ivy.log().error("Custom Error Msg.: " + e.toString());
            //e.printStackTrace();
        }
    }
}
