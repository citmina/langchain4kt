package io.github.stream29.langchain4kt.core.dsl

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender

@LangchainDsl
public fun Context.add(block: ContextBuilder.() -> Unit) {
    ContextBuilder(this).apply(block)
}

@LangchainDsl
public fun Context.Companion.of(block: ContextBuilder.() -> Unit): Context =
    Context().also { ContextBuilder(it).apply(block) }

public class ContextBuilder(
    private val context: Context
) {
    public fun systemInstruction(text: String?) {
        context.systemInstruction = text
    }

    public fun MessageSender.chat(text: String) {
        context.history.add(
            Message(
                sender = this,
                content = text
            )
        )
    }
}