package org.grida.prompt.base;

import org.grida.exception.DomainImageException;
import org.grida.base.Keywords;
import org.grida.base.Prompt;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Prompt 는")
class PromptTest {

    static class StubPrompt extends Prompt {

        public StubPrompt(String prompt) {
            super(prompt);
        }
    }

    static class StubKeywords extends Keywords {

        public StubKeywords(Map<String, String> keywords) {
            super(keywords);
        }
    }

    @Test
    void 프롬프트를_생성할_수_있다() {
        // given
        String promptFormat = "prompt";

        // when
        Prompt prompt = new StubPrompt(promptFormat);

        // then
        assertThat(prompt).isInstanceOf(Prompt.class);
    }

    @Test
    void 키워드를_이용해_프롬프트를_완성할_수_있다() {
        // given
        String promptFormat = "this application name is #APPLICATION_NAME# made by #AUTHOR_NAME#";
        Prompt prompt = new StubPrompt(promptFormat);

        Keywords keywords = new StubKeywords(
                Map.of("#APPLICATION_NAME#", "grida", "#AUTHOR_NAME#", "wwan13"));

        String expected = "this application name is grida made by wwan13";

        // when
        String result = prompt.create(keywords);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void 동일한_키워드는_모두_변경된다() {
        // given
        String promptFormat = "#NAME# duplicated #NAME#";
        Prompt prompt = new StubPrompt(promptFormat);

        Keywords keywords = new StubKeywords(Map.of("#NAME#", "name"));

        String expected = "name duplicated name";

        // when
        String result = prompt.create(keywords);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void 여러_번_호출해도_결과는_동일하다() {
        // given
        String promptFormat = "this application name is #APPLICATION_NAME# made by #AUTHOR_NAME#";
        Prompt prompt = new StubPrompt(promptFormat);

        Keywords keywords = new StubKeywords(
                Map.of("#APPLICATION_NAME#", "grida", "#AUTHOR_NAME#", "wwan13"));

        // when
        String result1 = prompt.create(keywords);
        String result2 = prompt.create(keywords);

        // then
        assertThat(result1).isEqualTo(result2);
    }

    @Nested
    @DisplayName("다음과 같은 경우에 예외가 발생한다")
    class ExceptionTest {

        @Test
        void 프롬프트에_키워드가_포함되어_있지_않는_경우() {
            // given
            String promptFormat = "this application name is #APPLICATION";
            Prompt prompt = new StubPrompt(promptFormat);

            Keywords keywords = new StubKeywords(
                    Map.of("#AUTHOR#", "wwan13"));

            // when, then
            assertThatThrownBy(() -> prompt.create(keywords))
                    .isInstanceOf(DomainImageException.class);
        }

        @Test
        void 키워드의_앞부분이_동일_하지만_같은_키워드는_경우() {
            // given
            String promptFormat = "this application name is #APPLICATION#";
            Prompt prompt = new StubPrompt(promptFormat);

            Keywords keywords = new StubKeywords(
                    Map.of("#APP#", "grida"));

            // when, then
            assertThatThrownBy(() -> prompt.create(keywords))
                    .isInstanceOf(DomainImageException.class);
        }
    }
}