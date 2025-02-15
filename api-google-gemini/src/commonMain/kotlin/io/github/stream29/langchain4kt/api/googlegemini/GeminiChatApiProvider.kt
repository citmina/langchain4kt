package io.github.stream29.langchain4kt.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.APIController
import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.common.RequestOptions
import dev.shreyaspatil.ai.client.generativeai.common.ResponseStoppedException
import dev.shreyaspatil.ai.client.generativeai.common.client.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.common.server.FinishReason
import dev.shreyaspatil.ai.client.generativeai.common.shared.SafetySetting
import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.core.output.Response
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * [ChatApiProvider] using Google Gemini API with specific parameters.
 *
 * @property modelName the model to use for generation.
 * @property apiKey the API key to use for authentication. Get it at [Google AI Studio](https://aistudio.google.com).
 * @property generationConfig the configuration for generation.
 * @property safetySettings the safety settings for generation.
 * @property timeoutMillis the timeout for the request, null for max.
 * @property apiVersion the API version to use.
 * @property endpoint the API endpoint to use.
 */
public data class GeminiChatApiProvider(
    val modelName: String,
    val apiKey: String,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null,
    val timeoutMillis: Long? = null,
    val apiVersion: String = "v1beta",
    val endpoint: String = "https://generativelanguage.googleapis.com",
) : ChatApiProvider<GenerateContentResponse> {
    private val requestOptions: RequestOptions = RequestOptions(timeoutMillis, apiVersion, endpoint)
    private val controller = APIController(
        apiKey,
        modelName,
        requestOptions,
        "genai-android"
    )

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun generate(context: Context): Response<GenerateContentResponse> {
        try {
            val response = controller.generateContent(
                constructRequest(
                    modelName,
                    context,
                    safetySettings,
                    generationConfig
                )
            )
            val (candidates, _, _) = response
            if (candidates.isNullOrEmpty())
                throw GenerationException("No candidates found in response $response")

            if (candidates.any { it.finishReason != FinishReason.STOP })
                throw ResponseStoppedException(response)

            val responseText = response.text
                ?: throw GenerationException("Response text not found in response $response")
            return Response(responseText, response)
        } catch (e: Throwable) {
            throw GenerationException("Generation failed with context: $context", e)
        }
    }
}

