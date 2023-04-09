package utils.parser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.CardSelector;
import model.TagSelector;
import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import utils.command.Command;
import utils.exceptions.InkaException;
import utils.exceptions.InvalidSyntaxException;
import utils.exceptions.UnrecognizedCommandException;

/**
 * Abstract class for parsing keyword-specific commands
 */
public abstract class KeywordParser {
    protected static final int FORMAT_HELP_WIDTH = 200;
    protected static final int FORMAT_HELP_LEFT_PAD = 0;
    protected static final int FORMAT_HELP_DESC_PAD = 10;

    private final DefaultParser parser;

    public KeywordParser() {
        parser = DefaultParser.builder().setAllowPartialMatching(false).setStripLeadingAndTrailingQuotes(false).build();
    }

    private static int parseUserIndex(String indexStr) throws InvalidSyntaxException {
        try {
            int index = Integer.parseInt(indexStr);
            // List displayed to user is 1-indexed, will definitely fail to find card
            if (index <= 0) {
                throw InvalidSyntaxException.buildNotValidIndexMessage();
            }

            return index;
        } catch (NumberFormatException ex) {
            throw InvalidSyntaxException.buildNotValidIndexMessage();
        }
    }

    protected static CardSelector getSelectedCard(CommandLine cmd) throws InkaException {
        if (cmd.hasOption(OptionsBuilder.FLAG_CARD)) {
            String cardUUID = cmd.getOptionValue(OptionsBuilder.FLAG_CARD);
            return new CardSelector(cardUUID);
        } else if (cmd.hasOption(OptionsBuilder.FLAG_CARD_INDEX)) {
            String cardIndexStr = cmd.getOptionValue(OptionsBuilder.FLAG_CARD_INDEX);
            int cardIndex = parseUserIndex(cardIndexStr);

            return new CardSelector(cardIndex);
        }

        // Shouldn't be called
        assert false;
        return null;
    }

    protected static TagSelector getSelectedTag(CommandLine cmd) throws InvalidSyntaxException {
        if (cmd.hasOption(OptionsBuilder.FLAG_TAG)) {
            String tagString = cmd.getOptionValue(OptionsBuilder.FLAG_TAG);
            return new TagSelector(tagString);
        } else if (cmd.hasOption(OptionsBuilder.FLAG_TAG_INDEX)) {
            String tagIndexStr = cmd.getOptionValue(OptionsBuilder.FLAG_TAG_INDEX);
            int tagIndex = parseUserIndex(tagIndexStr);

            return new TagSelector(tagIndex);
        }

        // May return a null TagSelector
        return null;
    }

    /**
     * Formats mutually exclusive options as '-flag1 / -flag2 / -flag3'
     */
    private static String formatOptionGroup(OptionGroup optionGroup) {
        return optionGroup.getNames().stream().map(flag -> "-" + flag).collect(Collectors.joining(" / "));
    }

    /**
     * Formats option as '-flag'
     */
    private static String formatOption(Option option) {
        return "-" + option.getOpt();
    }

    private static String formatOption(String option) {
        return "-" + option;
    }

    /**
     * Wrapper around {@link DefaultParser#parse(Options, String[])}
     */
    protected CommandLine parseUsingOptions(Options options, List<String> tokens)
            throws ParseException, InvalidSyntaxException {
        CommandLine cmd = parser.parse(options, tokens.toArray(new String[0]));
        if (cmd.getArgs().length != 0) {
            // Has unrecognized arguments
            throw InvalidSyntaxException.buildTooManyTokensMessage();
        }

        return cmd;
    }

    public Command parseTokens(List<String> tokens) throws InkaException {
        if (tokens.size() == 0) {
            throw InvalidSyntaxException.buildGenericMessage();
        }

        String action = tokens.get(0);
        List<String> flags = tokens.subList(1, tokens.size());

        try {
            return handleAction(action, flags);
        } catch (ParseException ex) {
            throw handleException(ex);
        }
    }

    /**
     * Handler to convert {@link ParseException} to the relevant {@link InkaException}
     *
     * @param ex Exception thrown by Apache CLI parser
     * @return Converted custom exception
     */
    private InkaException handleException(ParseException ex) {

        // No pattern matching :(
        if (ex instanceof AlreadySelectedException) {
            return convertAlreadySelectedException((AlreadySelectedException) ex);
        } else if (ex instanceof MissingArgumentException) {
            return convertMissingArgumentException((MissingArgumentException) ex);
        } else if (ex instanceof MissingOptionException) {
            return convertMissingOptionException((MissingOptionException) ex);
        } else if (ex instanceof UnrecognizedOptionException) {
            return convertUnrecognizedOptionException((UnrecognizedOptionException) ex);
        } else {
            // Something else has somehow broken
            return InvalidSyntaxException.buildGenericMessage();
        }
    }

    /**
     * Converter for {@link AlreadySelectedException}
     *
     * @param ex Exception thrown when multiple mutually exclusive options in {@link OptionGroup} are selected
     * @return Converted custom exception
     */
    private InkaException convertAlreadySelectedException(AlreadySelectedException ex) {
        String formattedFlags = formatOptionGroup(ex.getOptionGroup());
        return InvalidSyntaxException.buildAlreadySelectedMessage(formattedFlags);
    }

    /**
     * Converter for {@link MissingArgumentException}
     *
     * @param ex Exception thrown when {@link Option} is missing an argument
     * @return Converted custom exception
     */
    private InkaException convertMissingArgumentException(MissingArgumentException ex) {
        String formattedFlag = formatOption(ex.getOption());
        return InvalidSyntaxException.buildMissingArgumentMessage(formattedFlag);
    }

    /**
     * Converter for {@link MissingOptionException}
     *
     * @param ex Exception thrown when a required {@link Option} is not present
     * @return Converted custom exception
     */
    private InkaException convertMissingOptionException(MissingOptionException ex) {
        // Apache will return an untyped List containing either String or OptionGroup
        List<String> formattedFlags = new ArrayList<>();
        for (Object obj : ex.getMissingOptions()) {
            if (obj instanceof String) {
                // Single option
                String formattedFlag = formatOption((String) obj);
                formattedFlags.add(formattedFlag);
            } else if (obj instanceof OptionGroup) {
                // Mutually exclusive options
                String formattedFlag = formatOptionGroup((OptionGroup) obj);
                formattedFlags.add(formattedFlag);
            }
        }

        return InvalidSyntaxException.buildMissingOptionMessage(formattedFlags);
    }

    /**
     * Converter for {@link UnrecognizedOptionException}
     *
     * @param ex Exception thrown when an unrecognized {@link Option} is present
     * @return Converted custom exception
     */
    private InkaException convertUnrecognizedOptionException(UnrecognizedOptionException ex) {
        // Option should already be formatted properly formatted
        return InvalidSyntaxException.buildUnrecognizedOptionMessage(ex.getOption());
    }

    /**
     * Derived classes should override this method and handle the action corresponding
     *
     * @param action Action of the corresponding keyword
     * @param tokens Rest of the user input
     * @return Parsed command
     * @throws ParseException               Syntax errors in command
     * @throws UnrecognizedCommandException Unrecognized action for this keyword
     */
    protected abstract Command handleAction(String action, List<String> tokens)
            throws ParseException, InkaException;

    /**
     * Combines help messages for all actions into a single message
     *
     * @param keyword     Associated keyword
     * @param actionList  Actions
     * @param headerList  Descriptions of actions
     * @param optionsList All action Options for this keyword
     * @return Formatted help string
     */
    protected String formatHelpMessage(String keyword, String[] actionList, String[] headerList,
            Options[] optionsList) {
        assert optionsList.length == headerList.length;
        assert optionsList.length == actionList.length;

        HelpFormatter formatter = new HelpFormatter();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        for (int i = 0; i < optionsList.length; i++) {
            String syntax = "`" + keyword + " " + actionList[i] + "`";
            formatter.printHelp(printWriter, FORMAT_HELP_WIDTH, syntax, headerList[i], optionsList[i],
                    FORMAT_HELP_LEFT_PAD, FORMAT_HELP_DESC_PAD, "\n",
                    false);
        }

        // Fix for extra newlines at end
        return stringWriter.toString().trim() + System.lineSeparator();
    }
}
