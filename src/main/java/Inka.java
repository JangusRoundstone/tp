import model.CardList;
import model.DeckList;
import model.Memory;
import model.TagList;
import utils.UserInterface;
import utils.command.Command;
import utils.exceptions.InkaException;
import utils.parser.Parser;
import utils.storage.Storage;
import utils.storage.json.JsonStorage;

public class Inka {

    private final UserInterface ui;
    private final Parser parser;
    private Storage storage;

    private Memory memory;

    private CardList cardList;
    private TagList tagList;
    private DeckList deckList;

    public Inka(String filePath) {
        storage = new JsonStorage(filePath);
        ui = new UserInterface();
        parser = new Parser();
        memory = loadSaveFile();
        deckList = memory.getDeckList();
        cardList = memory.getCardList();
        tagList = memory.getTagList();
    }

    public static void main(String[] args) {
        new Inka("savedata.json").run();
    }

    /**
     * Attempts to load from save file
     *
     * @return CardList containing all saved cards
     */
    private Memory loadSaveFile() {
        // Notify user of no previously saved file
        if (!storage.saveFileExists()) {
            ui.printNoSaveFile();
            return new Memory();
        }

        // File exists; try to load from it
        Memory memory = new Memory();
        try {
            memory = storage.load();
            ui.printLoadSuccess();
        } catch (InkaException e) {
            ui.printException(e);
        }

        return memory;
    }

    public void run() {
        ui.printGreeting();

        while (parser.getIsExecuting()) {
            String userInput = ui.getUserInput();
            try {
                Command command = parser.parseCommand(userInput);
                command.execute(cardList, tagList, deckList, ui, storage);
            } catch (InkaException e) {
                ui.printException(e);
            }
            ui.printInputIdentifier();
        }
    }
}
