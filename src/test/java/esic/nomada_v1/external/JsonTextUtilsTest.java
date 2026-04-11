package esic.nomada_v1.external;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonTextUtilsTest {

    @Test
    void shouldReadObjectBlocksAndFields() {
        String json = "{\"items\":[{\"title\":\"Uno\",\"id\":{\"videoId\":\"abc\"}},{\"title\":\"Dos\"}]}";

        List<String> items = JsonTextUtils.objectBlocks(json, "items");

        assertEquals(2, items.size());
        assertEquals("Uno", JsonTextUtils.stringField(items.get(0), "title"));
        assertEquals("abc", JsonTextUtils.nestedStringField(items.get(0), "id", "videoId"));
    }
}
