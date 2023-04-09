package utils.parser;

import java.util.List;
import model.TagSelector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import utils.command.AddTagToDeckCommand;
import utils.command.Command;
import utils.command.DeleteTagCommand;
import utils.command.EditTagNameCommand;
import utils.command.ListCardsUnderTagCommand;
import utils.command.ListTagsCommand;
import utils.command.PrintHelpCommand;
import utils.exceptions.InkaException;
import utils.exceptions.InvalidSyntaxException;
import utils.exceptions.UnrecognizedCommandException;

public class TagKeywordParser extends KeywordParser {

    public static final String TAG_MODEL = "tag";
    public static final String DELETE_ACTION = "delete";
    public static final String EDIT_ACTION = "edit";
    public static final String HELP_ACTION = "help";
    public static final String LIST_ACTION = "list";
    public static final String DECK_ACTION = "deck";

    @Override
    protected Command handleAction(String action, List<String> tokens)
            throws ParseException, InkaException {
        switch (action) {
        case DECK_ACTION:
            return handleDeck(tokens);
        case DELETE_ACTION:
            return handleDelete(tokens);
        case EDIT_ACTION:
            return handleEdit(tokens);
        case HELP_ACTION:
            return handleHelp(tokens);
        case LIST_ACTION:
            return handleList(tokens);
        default:
            throw new UnrecognizedCommandException();
        }
    }

    private Command handleDelete(List<String> tokens) throws ParseException, InvalidSyntaxException {

        Options deleteOption = new OptionsBuilder(TAG_MODEL, DELETE_ACTION).buildOptions();
        CommandLine cmd = parseUsingOptions(deleteOption, tokens);

        TagSelector tagSelector = getSelectedTag(cmd);
        return new DeleteTagCommand(tagSelector);
    }

    private Command handleEdit(List<String> tokens) throws ParseException, InvalidSyntaxException {

        Options editOption = new OptionsBuilder(TAG_MODEL, EDIT_ACTION).buildOptions();
        CommandLine cmd = parseUsingOptions(editOption, tokens);

        String oldTagName = cmd.getOptionValue("o");
        String newTagName = cmd.getOptionValue("n");
        return new EditTagNameCommand(oldTagName, newTagName);
    }

    private Command handleHelp(List<String> tokens) throws InvalidSyntaxException {

        if (tokens.size() != 0) {
            throw InvalidSyntaxException.buildTooManyTokensMessage();
        }

        Options deleteOption = new OptionsBuilder(TAG_MODEL, DELETE_ACTION).buildOptions();
        Options editOption = new OptionsBuilder(TAG_MODEL, EDIT_ACTION).buildOptions();
        Options listOption = new OptionsBuilder(TAG_MODEL, LIST_ACTION).buildOptions();

        // For adding the deck help message for tag later
        Options deckOption = new OptionsBuilder(TAG_MODEL, DECK_ACTION).buildOptions();

        // Combine all actions
        String[] actionList = {EDIT_ACTION, DELETE_ACTION, LIST_ACTION};
        String[] headerList = {"Edit existing tags", "Delete tags", "List tags"};
        Options[] optionsList = {editOption, deleteOption, listOption};

        String helpMessage = formatHelpMessage("tag", actionList, headerList, optionsList);
        return new PrintHelpCommand(helpMessage);
    }

    private Command handleList(List<String> tokens) throws ParseException, InvalidSyntaxException {

        Options listOption = new OptionsBuilder(TAG_MODEL, LIST_ACTION).buildOptions();
        CommandLine cmd = parseUsingOptions(listOption, tokens);

        TagSelector tagSelector = getSelectedTag(cmd);
        if (tagSelector == null) {
            return new ListTagsCommand();
        } else {
            return new ListCardsUnderTagCommand(tagSelector);
        }
    }

    private Command handleDeck(List<String> tokens) throws ParseException, InkaException {

        Options deckOption = new OptionsBuilder(TAG_MODEL, DECK_ACTION).buildOptions();
        CommandLine cmd = parseUsingOptions(deckOption, tokens);

        String deckName = cmd.getOptionValue("d");
        TagSelector tagSelector = getSelectedTag(cmd);

        return new AddTagToDeckCommand(deckName, tagSelector);
    }
}
