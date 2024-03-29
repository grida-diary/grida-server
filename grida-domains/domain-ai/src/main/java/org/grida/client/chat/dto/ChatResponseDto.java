package org.grida.client.chat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.grida.exception.DomainAiException;
import org.grida.processor.emotionanalysis.EmotionAnalysisResult;

import java.util.List;

import static org.grida.exception.DomainAiErrorCode.CANNOT_PARSING_CHAT_RESPONSE;

@Slf4j
public record ChatResponseDto(
        List<Choice> choices
) {

    public record Choice(
            ChatMessageDto message
    ) {

        private String getMessageContent() {
            return message.content();
        }
    }

    public EmotionAnalysisResult toResult() {
        String chatResult = choices.get(0).getMessageContent();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(chatResult, EmotionAnalysisResult.class);
        } catch (JsonProcessingException e) {
            log.error("Cannot parsing open ai chat result. Result is '{}'", chatResult);
            throw new DomainAiException(CANNOT_PARSING_CHAT_RESPONSE);
        }
    }
}
