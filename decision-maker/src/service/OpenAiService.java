package service;

import ch.ivyteam.ivy.environment.Ivy;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public final class OpenAiService {
  
  /**
   * Creates and returns an {@link OpenAiChatModel} configured 
   * to produce strict JSON schema-compliant outputs.
   *
   * @return a configured {@link OpenAiChatModel} instance
   */
  public static final OpenAiChatModel getJsonChatModel() {
    return OpenAiChatModel.builder()
        .modelName(OpenAiChatModelName.GPT_4_1_MINI) // Uses the GPT-4.1-mini model
        .temperature(Double.valueOf(0)) // Ensures deterministic output
        .apiKey(Ivy.var().get("OpenAI.ApiKey")) // Retrieves API key from Ivy environment variables
        .supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA) // Requests JSON schema output
        .strictJsonSchema(true) // Enforces strict compliance with the provided JSON schema
        .build();
  }
}
