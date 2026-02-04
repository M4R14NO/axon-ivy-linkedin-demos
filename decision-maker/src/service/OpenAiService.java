package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public final class OpenAiService {

	/**
	 * Creates and returns an {@link OpenAiChatModel} configured to produce strict
	 * JSON schema-compliant outputs.
	 *
	 * @return a configured {@link OpenAiChatModel} instance
	 */
	public static final OpenAiChatModel getJsonChatModel(Boolean useOllama) {
		if (useOllama) {
			return OpenAiChatModel.builder().baseUrl(Ivy.var().get("Ollama.baseUrl")) // "Ollama.baseUrl,
																						// LMStudio.baseUrl"
					.modelName("gemma3:4b") // "qwen3:30b, qwen3-coder:30b, gemma3:4b"
					.supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA) // Requests JSON schema output
					.strictJsonSchema(true) // Enforces strict compliance with the provided JSON schema
					.build();
		} else {
			return OpenAiChatModel.builder().modelName(OpenAiChatModelName.GPT_4_1_MINI).temperature(Double.valueOf(0))
					.apiKey(Ivy.var().get("OpenAI.ApiKey"))
					.supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA) // Requests JSON schema output
					.strictJsonSchema(true) // Enforces strict compliance with the provided JSON schema
					.build();
		}
	}
	
	public static final OpenAiChatModel getJsonChatModelByName(Boolean useOllama, String modelname) {
		if (useOllama) {
			return OpenAiChatModel.builder().baseUrl(Ivy.var().get("Ollama.baseUrl")) // "Ollama.baseUrl,
																						// LMStudio.baseUrl"
					.modelName(modelname) // "qwen3:30b, qwen3-coder:30b, gemma3:4b"
					.supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA) // Requests JSON schema output
					.strictJsonSchema(true) // Enforces strict compliance with the provided JSON schema
					.build();
		} else {
			return OpenAiChatModel.builder().modelName(OpenAiChatModelName.GPT_4_1_MINI).temperature(Double.valueOf(0))
					.apiKey(Ivy.var().get("OpenAI.ApiKey"))
					.supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA) // Requests JSON schema output
					.strictJsonSchema(true) // Enforces strict compliance with the provided JSON schema
					.build();
		}
	}

	public static final OpenAiChatModel getJsonChatModelFromEnvVar() {
		return OpenAiChatModel.builder().modelName(OpenAiChatModelName.GPT_4_1_MINI).temperature(Double.valueOf(0))
				.apiKey(Ivy.var().get("OpenAI.ApiKey")).supportedCapabilities(Capability.RESPONSE_FORMAT_JSON_SCHEMA) // Requests
																														// JSON
																														// schema
																														// output
				.strictJsonSchema(true) // Enforces strict compliance with the provided JSON schema
				.build();
	}

	/**
	 * Lists all available models from the Ollama server.
	 * Filters out embedding models.
	 * 
	 * @return List of model names available on the Ollama server (excluding embedding models).
	 *         Returns empty list if the server is not reachable or if an error occurs.
	 */
	public static java.util.List<String> listOllamaModels() {
	  String baseUrl = Ivy.var().get("Ollama.baseUrl");
	  
	  // Remove /v1 suffix if present, as Ollama's tags endpoint is at the root
	  if (baseUrl.endsWith("/v1")) {
	    baseUrl = baseUrl.substring(0, baseUrl.length() - 3);
	  }
	  
	  String listEndpoint = baseUrl + "/api/tags";
	  
	  try {
	    Ivy.log().info("Requesting Ollama models from: {0}", listEndpoint);
	    
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	        .uri(URI.create(listEndpoint))
	        .GET()
	        .build();
	    
	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	    
	    if (response.statusCode() != 200) {
	      Ivy.log().error("Failed to list models. Status: {0}, Body: {1}", 
	                      response.statusCode(), response.body());
	      return new java.util.ArrayList<>();
	    }
	    
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.body());
	    JsonNode models = root.get("models");
	    
	    java.util.List<String> modelNames = new java.util.ArrayList<>();
	    if (models != null && models.isArray()) {
	      for (JsonNode model : models) {
	        String name = model.get("name").asText();
	        
	        // Filter out embedding models
	        String nameLower = name.toLowerCase();
	        if (!nameLower.contains("embedding") && !nameLower.contains("embed")) {
	          modelNames.add(name);
	        }
	      }
	    }
	    
	    return modelNames;
	    
	  }  catch (java.net.ConnectException e) {
		  Ivy.log().warn("Ollama server not reachable at {0}. Is the server running? Type 'ipconfig getifaddr en0' in the terminal of your host machine to find out the IP address.", listEndpoint, e);
		  return new java.util.ArrayList<>();
		} catch (java.nio.channels.ClosedChannelException e) {
		  Ivy.log().warn("Connection to Ollama server at {0} was closed unexpectedly", listEndpoint, e);
		  return new java.util.ArrayList<>();
		} catch (java.net.http.HttpTimeoutException e) {
		  Ivy.log().warn("Timeout connecting to Ollama server at {0}", listEndpoint, e);
		  return new java.util.ArrayList<>();
		} catch (Exception e) {
		  String errorDetails = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
		  Ivy.log().warn("Failed to list Ollama models from {0}: {1}", listEndpoint, errorDetails, e);
		  return new java.util.ArrayList<>();
		}
	}

}
