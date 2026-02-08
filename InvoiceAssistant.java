package assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import doc.to.form.Invoice;
import service.OpenAiService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Optimized assistant - extracts complete invoice in ONE LLM call
 */
public class InvoiceAssistant {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract complete invoice with all entities from Docling JSON in a single call
     */
    public Invoice extractInvoice(String doclingJson, Boolean useOllama) throws Exception {
        Map<String, Object> doclingMap = objectMapper.readValue(doclingJson, Map.class);
        String simplifiedJson = createSimplifiedJson(doclingMap);
        
        // Single assistant, single LLM call
        IInvoiceExtractor extractor = AiServices
            .builder(IInvoiceExtractor.class)
            .chatModel(OpenAiService.getJsonChatModel(useOllama))
            .build();
        
        return extractor.extractCompleteInvoice(simplifiedJson);
    }
    
    /**
     * Extract complete invoice with all entities from Docling JSON in a single call
     */
    public Invoice extractInvoiceWithModel(String doclingJson, Boolean useOllama, String modelname) throws Exception {
        Map<String, Object> doclingMap = objectMapper.readValue(doclingJson, Map.class);
        String simplifiedJson = createSimplifiedJson(doclingMap);
        
        // Single assistant, single LLM call
        IInvoiceExtractor extractor = AiServices
            .builder(IInvoiceExtractor.class)
            .chatModel(OpenAiService.getJsonChatModelByName(useOllama, modelname))
            .build();
        
        return extractor.extractCompleteInvoice(simplifiedJson);
    }

    /**
     * Extract invoice from OCR-processed document (optimized for scanned/image-based PDFs)
     * Uses enhanced prompting for OCR artifacts and less structured text
     * 
     * @param doclingJson The Docling JSON output (with OCR enabled)
     * @param useOllama Whether to use Ollama or OpenAI
     * @return Extracted Invoice object
     * @throws Exception if extraction fails
     */
    public Invoice extractInvoiceFromOcr(String doclingJson, Boolean useOllama) throws Exception {
        Map<String, Object> doclingMap = objectMapper.readValue(doclingJson, Map.class);
        String simplifiedJson = createSimplifiedJson(doclingMap);
        
        // Use OCR-optimized extractor
        IOcrInvoiceExtractor extractor = AiServices
            .builder(IOcrInvoiceExtractor.class)
            .chatModel(OpenAiService.getJsonChatModel(useOllama))
            .build();
        
        return extractor.extractCompleteInvoiceFromOcr(simplifiedJson);
    }

    /**
     * Extract invoice from OCR-processed document with specific model
     * 
     * @param doclingJson The Docling JSON output (with OCR enabled)
     * @param useOllama Whether to use Ollama or OpenAI
     * @param modelname The specific model to use
     * @return Extracted Invoice object
     * @throws Exception if extraction fails
     */
    public Invoice extractInvoiceFromOcrWithModel(String doclingJson, Boolean useOllama, String modelname) throws Exception {
        Map<String, Object> doclingMap = objectMapper.readValue(doclingJson, Map.class);
        String simplifiedJson = createSimplifiedJson(doclingMap);
        
        // Use OCR-optimized extractor with specific model
        IOcrInvoiceExtractor extractor = AiServices
            .builder(IOcrInvoiceExtractor.class)
            .chatModel(OpenAiService.getJsonChatModelByName(useOllama, modelname))
            .build();
        
        return extractor.extractCompleteInvoiceFromOcr(simplifiedJson);
    }

    public interface IInvoiceExtractor {
        @SystemMessage("""
            You are an expert at extracting complete invoice information from Docling document parsing output.
            
            Input structure:
            - textsWithPosition: Text elements with spatial coordinates (x, y) and labels
            - groups: Key-value areas grouping related texts
            - tableRows: Pipe-separated table content
            
            Spatial extraction rules:
            - Texts with similar y-coordinates (±5 units) are on the same line
            - Values typically appear RIGHT of labels (higher x coordinate)
            - Use group membership to identify related fields
            
            Entity identification:
            - Vendor = invoice issuer (top/header section)
            - Customer = invoice recipient (address block, look for "Herr", "Frau")
            - LineItems = table rows with products/services
            
            Data formatting:
            - Convert German decimals (123,45) to numeric (123.45)
            - Remove currency symbols (€, EUR) from amounts
            - For customer number: look for "Kunden-Nr." label and value on same y-coordinate
            - Bank code = Bankleitzahl (BLZ), Account number = Kontonummer
            
            Important: Return ONLY valid JSON with complete Invoice structure including ALL nested entities.
            Set fields to null if information is not found.
            
            The Invoice must include:
            - Basic fields: invoiceNumber, invoiceDate, dueDate, currency, subtotal, totalAmount
            - vendor: {name, address, phone, email, taxNumber, vatNumber, bankDetails: {bankName, iban, bic, accountNumber, bankCode}}
            - customer: {name, address, email, phone, customerNumber}
            - lineItems: [{position, itemNumber, description, quantity, unit, unitPrice, totalPrice}]
            - tax: {taxRate, taxAmount, taxType}
            - paymentTerms: {paymentMethod, paymentInstructions, discountTerms}
        """)
        @UserMessage("{{json}}")
        Invoice extractCompleteInvoice(@V("json") String simplifiedJson);
    }
    /**
     * OCR-optimized extractor interface with enhanced prompting for scanned documents
     */
    public interface IOcrInvoiceExtractor {
        @SystemMessage("""
            You are an expert at extracting invoice information from OCR-processed documents.
            
            IMPORTANT: OCR-processed documents often contain:
            - Text recognition errors (e.g., '0' vs 'O', '1' vs 'I', '5' vs 'S')
            - Inconsistent spacing and formatting
            - Merged or split words
            - Misaligned table structures
            
            Input structure (from Docling OCR output):
            - textsWithPosition: Text elements with spatial coordinates (x, y) and labels
            - groups: Key-value areas grouping related texts
            - tableRows: Pipe-separated table content (may be imperfect due to OCR)
            
            Enhanced extraction rules for OCR:
            - Use spatial proximity (y-coordinates ±5 units) to associate labels with values
            - Be tolerant of OCR errors in numbers: validate and correct obvious mistakes
            - Look for patterns: dates (DD.MM.YYYY), amounts (###,## €), tax IDs
            - Cross-validate: total should equal subtotal + tax
            - If table extraction is poor, use text blocks to find line items
            
            Entity identification (same as digital PDFs):
            - Vendor = invoice issuer (top/header section)
            - Customer = invoice recipient (look for address block with "Herr", "Frau")
            - LineItems = table rows or structured text blocks with product info
            
            Data normalization:
            - Convert German decimals (123,45) to numeric (123.45)
            - Remove currency symbols (€, EUR) from amounts
            - Correct common OCR errors: O→0 in numbers, I→1 in IDs
            - Validate bank codes (should be numeric)
            - Validate IBANs (DE + 20 characters)
            
            Return ONLY valid JSON with complete Invoice structure including ALL nested entities.
            If a field cannot be reliably extracted due to OCR issues, set it to null.
            
            The Invoice must include all standard fields:
            - Basic: invoiceNumber, invoiceDate, dueDate, currency, subtotal, totalAmount
            - vendor: {name, address, phone, email, taxNumber, vatNumber, bankDetails: {bankName, iban, bic, accountNumber, bankCode}}
            - customer: {name, address, email, phone, customerNumber}
            - lineItems: [{position, itemNumber, description, quantity, unit, unitPrice, totalPrice}]
            - tax: {taxRate, taxAmount, taxType}
            - paymentTerms: {paymentMethod, paymentInstructions, discountTerms}
        """)
        @UserMessage("{{json}}")
        Invoice extractCompleteInvoiceFromOcr(@V("json") String simplifiedJson);
    }


    /**
     * Create simplified JSON from Docling output with spatial information
     */
    @SuppressWarnings("unchecked")
    private String createSimplifiedJson(Map<String, Object> doclingMap) throws Exception {
        SimplifiedInvoiceData simplified = new SimplifiedInvoiceData();
        
        // Extract all text content with spatial information
        List<Map<String, Object>> texts = (List<Map<String, Object>>) doclingMap.get("texts");
        if (texts != null) {
            for (int i = 0; i < texts.size(); i++) {
                Map<String, Object> textObj = texts.get(i);
                String text = (String) textObj.get("text");
                if (text != null && !text.trim().isEmpty()) {
                    TextWithPosition twp = new TextWithPosition();
                    twp.index = i;
                    twp.text = text;
                    twp.label = (String) textObj.get("label");
                    
                    // Extract bounding box for spatial context
                    List<Map<String, Object>> prov = (List<Map<String, Object>>) textObj.get("prov");
                    if (prov != null && !prov.isEmpty()) {
                        Map<String, Object> bbox = (Map<String, Object>) prov.get(0).get("bbox");
                        if (bbox != null) {
                            twp.x = ((Number) bbox.get("l")).doubleValue();
                            twp.y = ((Number) bbox.get("t")).doubleValue();
                        }
                    }
                    
                    simplified.textsWithPosition.add(twp);
                }
            }
        }
        
        // Extract groups for key-value relationships
        List<Map<String, Object>> groups = (List<Map<String, Object>>) doclingMap.get("groups");
        if (groups != null) {
            for (Map<String, Object> group : groups) {
                String label = (String) group.get("label");
                if ("key_value_area".equals(label)) {
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.label = label;
                    
                    List<Map<String, Object>> children = (List<Map<String, Object>>) group.get("children");
                    if (children != null) {
                        for (Map<String, Object> child : children) {
                            String ref = (String) child.get("$ref");
                            if (ref != null && ref.startsWith("#/texts/")) {
                                int idx = Integer.parseInt(ref.substring(8));
                                groupInfo.textIndices.add(idx);
                            }
                        }
                    }
                    simplified.groups.add(groupInfo);
                }
            }
        }
        
        // Extract table data
        List<Map<String, Object>> tables = (List<Map<String, Object>>) doclingMap.get("tables");
        if (tables != null) {
            for (Map<String, Object> table : tables) {
                Map<String, Object> data = (Map<String, Object>) table.get("data");
                if (data != null) {
                    List<List<Map<String, Object>>> grid = (List<List<Map<String, Object>>>) data.get("grid");
                    if (grid != null && !grid.isEmpty()) {
                        for (List<Map<String, Object>> row : grid) {
                            List<String> rowTexts = row.stream()
                                .map(cell -> (String) cell.get("text"))
                                .filter(t -> t != null && !t.trim().isEmpty())
                                .collect(Collectors.toList());
                            if (!rowTexts.isEmpty()) {
                                simplified.tableRows.add(String.join(" | ", rowTexts));
                            }
                        }
                    }
                }
            }
        }
        
        return objectMapper.writeValueAsString(simplified);
    }

    // Helper classes for simplified JSON structure
    private static class SimplifiedInvoiceData {
        public List<TextWithPosition> textsWithPosition = new java.util.ArrayList<>();
        public List<GroupInfo> groups = new java.util.ArrayList<>();
        public List<String> tableRows = new java.util.ArrayList<>();
    }
    
    private static class TextWithPosition {
        public int index;
        public String text;
        public String label;
        public Double x;
        public Double y;
    }
    
    private static class GroupInfo {
        public String label;
        public List<Integer> textIndices = new java.util.ArrayList<>();
    }
}