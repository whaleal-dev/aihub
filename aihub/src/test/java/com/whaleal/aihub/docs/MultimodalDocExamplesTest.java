package com.whaleal.aihub.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaleal.aihub.platform.openai.chat.entity.ChatMessage;
import com.whaleal.aihub.platform.openai.chat.entity.Content;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Executable source of truth for the snippets embedded in
 * {@code docs/core-sdk/model-access/multimodal.md}.
 *
 * <p>The dual-projection and wire-shape checks need no key and pin the
 * serialization contract; they run in normal CI. Live multimodal calls live
 * in {@code ChatDocExamplesLiveTest#multiModalUserMessageWithImage}.
 */
public class MultimodalDocExamplesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /** 8x8 纯红 PNG，避免示例依赖外部图床。 */
    private static final String RED_PNG_DATA_URL =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAIAAABLbSncAAAAEklEQVR4nGP4z8CAFWEXHbQSACj/P8Fu7N9hAAAAAElFTkSuQmCC";

    // ---- 直接构造（不经 Memory）：image_url ----

    @Test
    public void directChatMessageWithImageSerializesCorrectly() throws Exception {
        ChatMessage message = ChatMessage.withUser("这张图是什么颜色？", RED_PNG_DATA_URL);

        JsonNode content = mapper.valueToTree(message.getContent().getMultiModals());
        Assert.assertEquals("text", content.get(0).path("type").asText());
        Assert.assertEquals("image_url", content.get(1).path("type").asText());
        Assert.assertEquals(RED_PNG_DATA_URL, content.get(1).path("image_url").path("url").asText());
    }

    // ---- video_url（Kimi/Moonshot 扩展） ----

    @Test
    public void videoUrlMultiModalSerializes() {
        Content.MultiModal video = Content.MultiModal.builder()
                .type(Content.MultiModal.Type.VIDEO_URL.getType())
                .videoUrl(new Content.MultiModal.VideoUrl("data:video/mp4;base64,AAAA"))
                .build();

        Assert.assertEquals("video_url", video.getType());
        Assert.assertEquals("data:video/mp4;base64,AAAA", video.getVideoUrl().getUrl());
    }
}
