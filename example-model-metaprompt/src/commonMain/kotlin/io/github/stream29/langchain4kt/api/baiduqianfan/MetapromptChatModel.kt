package io.github.stream29.langchain4kt.api.baiduqianfan

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.ChatModel
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.Response

class MetapromptChatModel(
    override val context: Context = Context(),
    var apiProvider: ChatApiProvider<*, *>,
    var metapromptTransform: (String) -> String
) : ChatModel<Unit, Unit> {
    override suspend fun chat(message: Message): Response<Message, Unit, Unit> {
        val metaprompt = metapromptTransform(message.content)
        val prompt =
            apiProvider.generate(
                context.copy(
                    history = mutableListOf(
                        Message(
                            MessageSender.User,
                            metaprompt
                        )
                    )
                )
            ).let {
                it as Response.Success<Message, *>
            }.content.content
        val response = apiProvider.generate(
            context.copy().also {
                it.history.add(
                    Message(
                        MessageSender.User,
                        prompt
                    )
                )
            }
        ) as Response.Success
        context.history.add(message)
        context.history.add(Message(MessageSender.Model, response.content.content))
        return Response.Success(response.content, Unit)
    }
}