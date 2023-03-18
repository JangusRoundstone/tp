package utils.storage;

import java.nio.file.Path;
import model.CardList;
import org.junit.jupiter.api.Test;
import utils.exceptions.StorageLoadFailure;
import utils.storage.json.JsonStorage;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonStorageTest {

    private static final Path TEST_DATA_FOLDER = Path.of("src/test/data/storage");

    /* Test cases */
    private static final Path EMPTY_FILE = TEST_DATA_FOLDER.resolve("empty.json");
    private static final Path MALFORMED_FILE = TEST_DATA_FOLDER.resolve("malformed.json");
    private static final Path VALID_FILE = TEST_DATA_FOLDER.resolve("valid.json");

    @Test
    public void load_emptyFile() {
        Storage storage = new JsonStorage(EMPTY_FILE.toString());
        assertThrows(StorageLoadFailure.class, storage::load);
    }

    @Test
    public void load_malformedFile() {
        Storage storage = new JsonStorage(MALFORMED_FILE.toString());
        assertThrows(StorageLoadFailure.class, storage::load);
    }

    @Test
    public void load_validFile() throws StorageLoadFailure {
        Storage storage = new JsonStorage(VALID_FILE.toString());
        CardList cardList = storage.load();

        assert cardList.size() == 2;
    }
}