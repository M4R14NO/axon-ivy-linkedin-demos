package service;

import ch.ivyteam.ivy.environment.Ivy;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

/**
 * Service class to interact with an OpenAI chat model.
 * It encapsulates the model configuration and provides a method to generate
 * JSON-structured responses based on a given JSON schema and user message.
 */
public class OpenAiService {
  
  /**
   * The OpenAI chat model instance used to send chat requests and receive responses.
   */
  private OpenAiChatModel model;

  public OpenAiChatModel getModel() {
    return model;
  }

  public void setModel(OpenAiChatModel model) {
    this.model = model;
  }

  /**
   * Default constructor that initializes the OpenAI chat model with preset parameters:
   * - Uses GPT-4_1_MINI model variant.
   * - Sets temperature to 0 for deterministic responses.
   * - Uses API key retrieved from environment or config via Ivy.var().
   * - Specifies that the model supports JSON schema response format.
   * - Enables strict JSON schema validation on responses.
   */
  public OpenAiService() {
    this.model = OpenAiChatModel.builder()
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .temperature(Double.valueOf(0))
        .apiKey(Ivy.var().get("OpenAI.ApiKey"))
        .supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true).build();
  }

  /**
   * Generates a response from the OpenAI model formatted as JSON following
   * the provided JSON schema, based on the input message.
   *
   * @param jsonSchema the JSON schema that the response should conform to
   * @param message the input message or prompt to send to the model
   * @return the model's response text formatted according to the JSON schema
   */
  public String generateJson(JsonSchema jsonSchema, String message) {
    // Build the desired response format with JSON type and schema
    ResponseFormat responseFormat = ResponseFormat.builder()
        .type(ResponseFormatType.JSON)
        .jsonSchema(jsonSchema)
        .build();

    // Create the chat request with the user message and response format
    ChatRequest chatRequest = ChatRequest.builder()
        .responseFormat(responseFormat)
        .messages(UserMessage.from(message))
        .build();

    ChatResponse result = model.chat(chatRequest);
    return result.aiMessage().text();
  }
}