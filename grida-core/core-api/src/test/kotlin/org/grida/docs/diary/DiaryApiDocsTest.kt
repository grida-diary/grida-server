package org.grida.docs.diary

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.wwan13.api.document.snippets.DATE
import io.wwan13.api.document.snippets.DATETIME
import io.wwan13.api.document.snippets.ENUM
import io.wwan13.api.document.snippets.NUMBER
import io.wwan13.api.document.snippets.STRING
import io.wwan13.api.document.snippets.isTypeOf
import io.wwan13.api.document.snippets.whichMeans
import org.grida.docs.ApiDocsTest
import org.grida.domain.diary.Diary
import org.grida.domain.diary.DiaryScope
import org.grida.domain.diary.DiaryService
import org.grida.domain.diaryimage.DiaryImageService
import org.grida.presentation.v1.diary.DiaryController
import org.grida.presentation.v1.diary.dto.DiaryModifyRequest
import org.grida.presentation.v1.diary.dto.DiaryRequest
import org.grida.presentation.v1.diary.dto.DiaryScopeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import java.time.LocalDate

@WebMvcTest(controllers = [DiaryController::class])
class DiaryApiDocsTest(
    private val diaryController: DiaryController
) : ApiDocsTest(
    diaryController,
    "diary"
) {

    @MockkBean
    private lateinit var diaryService: DiaryService

    @MockkBean
    private lateinit var diaryImageService: DiaryImageService

    @Test
    fun `일기 생성 API`() {
        every { diaryService.appendDiary(any()) } returns 1L

        val api = api.post("/api/v1/diary") {
            withBearerToken()
            requestBody(
                DiaryRequest(
                    content = "일기 콘텐츠",
                    targetDate = "2024-01-01",
                    scope = DiaryScope.PUBLIC
                )
            )
        }

        documentFor(api, "append-diary") {
            summary("일기 생성 API")
            requestHeaders(
                "Authorization" whichMeans "인증 토큰"
            )
            requestFields(
                "content" isTypeOf STRING whichMeans "일기 콘텐츠",
                "targetDate" isTypeOf DATETIME whichMeans "일기 날짜",
                "scope" isTypeOf ENUM(DiaryScope::class) whichMeans "일기 공개 범위"
            )
            responseFields(
                "data.id" isTypeOf NUMBER whichMeans "생성된 일기 ID"
            )
        }
    }

    @Test
    fun `일기 읽기 API`() {
        val diary = Diary(
            id = 1,
            userId = 1,
            content = "일기 내용",
            targetDate = LocalDate.of(2024, 2, 22),
            scope = DiaryScope.PUBLIC
        )

        every { diaryService.readDiary(any(), any()) } returns diary
        every { diaryImageService.countRemainImageGenerateAttempt(any()) } returns 2L

        val api = api.get("/api/v1/diary/{diaryId}", 1L) {
            withBearerToken()
        }

        documentFor(api, "read-diary") {
            summary("일기 읽기 API")
            pathParameters(
                "diaryId" whichMeans "읽으려는 일기의 id"
            )
            responseFields(
                "data.content" isTypeOf STRING whichMeans "일기 내용",
                "data.targetDate" isTypeOf DATE whichMeans "대상 날짜",
                "data.scope" isTypeOf ENUM(DiaryScope::class) whichMeans "일기 공개 범위",
                "data.createdAt" isTypeOf DATETIME whichMeans "생성 시간",
                "data.remainAttempt" isTypeOf NUMBER whichMeans "일기 이미지 재생성 가능 횟수",
            )
        }
    }

    @Test
    fun `일기 수정 API`() {
        every { diaryService.modify(any(), any(), any(), any()) } returns 1L

        val api = api.patch("/api/v1/diary/{diaryId}", 1L) {
            withBearerToken()
            requestBody(
                DiaryModifyRequest(
                    content = "수정할 일기 콘텐츠",
                    scope = "PUBLIC"
                )
            )
        }

        documentFor(api, "modify-diary") {
            summary("일기 수정 API")
            requestHeaders(
                "Authorization" whichMeans "인증 토큰"
            )
            requestFields(
                "content" isTypeOf STRING whichMeans "수정할 일기 콘텐츠",
                "scope" isTypeOf ENUM(DiaryScope::class) whichMeans "일기 공개 범위"
            )
            responseFields(
                "data.id" isTypeOf NUMBER whichMeans "수정돤 일기 ID"
            )
        }
    }

    @Test
    fun `일기 공개 범위 수정 API`() {
        every { diaryService.modifyScope(any(), any(), any()) } returns 1L

        val api = api.patch("/api/v1/diary/{diaryId}/scope", 1L) {
            withBearerToken()
            requestBody(
                DiaryScopeRequest(
                    scope = "PUBLIC"
                )
            )
        }

        documentFor(api, "modify-diary-scope") {
            summary("일기 공개 범위 수정 API")
            requestHeaders(
                "Authorization" whichMeans "인증 토큰"
            )
            requestFields(
                "scope" isTypeOf ENUM(DiaryScope::class) whichMeans "일기 공개 범위"
            )
            responseFields(
                "data.id" isTypeOf NUMBER whichMeans "수정돤 일기 ID"
            )
        }
    }
}
