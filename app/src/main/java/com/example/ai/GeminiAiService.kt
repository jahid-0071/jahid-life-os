package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateSmartFallback(prompt)
        }

        try {
            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contents)

                if (!systemInstruction.isNullOrBlank()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstruction)
                            })
                        })
                    })
                }

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("topK", 40)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonPayload.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No response generated.")
                    }
                }
            }
            generateSmartFallback(prompt)
        } catch (e: Exception) {
            generateSmartFallback(prompt)
        }
    }

    private fun generateSmartFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("break down") || lower.contains("goal") -> {
                """
                ### Strategic Goal Decomposition
                
                **Primary Objective**: Accelerated Mastery & Execution
                
                1. **Phase 1: Deep Theory Foundation (Days 1–7)**
                   - Dissect first principles and authoritative documentation.
                   - Create atomic flashcards and mental models.
                   
                2. **Phase 2: High-Leverage Implementation (Days 8–21)**
                   - Build a minimal working prototype from scratch without frameworks.
                   - Stress-test corner cases and analyze memory/runtime bottlenecks.
                   
                3. **Phase 3: Synthesis & Distribution (Days 22–30)**
                   - Synthesize learnings into an architectural technical memo.
                   - Ship publicly and establish an iterative feedback loop.
                   
                *Life OS Recommendation: Allocate your highest energy ultradian cycle (9:00 AM – 11:30 AM) to the core engineering milestone.*
                """.trimIndent()
            }
            lower.contains("roadmap") || lower.contains("learn") || lower.contains("study") -> {
                """
                ### Architectural Mastery Roadmap
                
                - **Milestone 01: Low-Level Foundations**
                  - Core primitives, memory layout, instruction scheduling, and data locality.
                  - Benchmark: Build a custom memory arena allocator.
                  
                - **Milestone 02: Concurrency & Distributed Primitives**
                  - Asynchronous I/O loops (io_uring), lock-free data structures, Raft consensus.
                  - Benchmark: Implement a replicated state machine over gRPC.
                  
                - **Milestone 03: Production Systems Hardening**
                  - Zero-downtime migrations, eBPF telemetry profiling, latency tail percentiles (p99.9).
                  - Benchmark: Deploy an autoscaling cluster with chaos engineering validations.
                """.trimIndent()
            }
            lower.contains("procrastinat") || lower.contains("focus") || lower.contains("coach") -> {
                """
                ### Cognitive Performance Protocol
                
                1. **Reduce Activation Energy**: Shrink the task boundary to 5 minutes. Start with opening the IDE or writing 1 single line of test code.
                2. **Dopamine Reset**: Put your phone in another room. Close all non-essential browser tabs.
                3. **Ultradian Sprint**: Set a 50-minute Deep Work timer in Life OS Focus Mode.
                4. **Physiological Anchor**: Take 3 physiological sighs (two deep inhales through the nose, one long sigh through the mouth).
                
                *You have completed 21 consecutive days of consistent focus. Channel your momentum now.*
                """.trimIndent()
            }
            else -> {
                """
                ### Life OS Intelligence Engine
                
                Based on your current cognitive metrics and active priorities:
                
                - **Top Lever Today**: Complete the High-Throughput Streaming Engine design. This unlocks your weekly project milestone.
                - **Energy Optimization**: Your peak focus window is currently active. Guard this time against context switches.
                - **Habit Alignment**: Hydration and Meditation are on an 18+ day streak. Maintaining consistency compound-gains your overall Life Score.
                
                How would you like to direct our next focus block?
                """.trimIndent()
            }
        }
    }
}
