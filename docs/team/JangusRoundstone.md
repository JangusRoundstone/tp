# Wang Silang - Project Portfolio Page

## Overview

Inka is a CLI-based software that allows users to add Cards containing questions and answers, attach tags into each Card
and put groups of cards into a deck. Inka aims to help students revise for their exam by providing a flashcard-like
experience.

## Summary of Contributions

### Features Implemented

#### 1. Parser Version 1

- ***What it does :***
  This is the first parser of Inka, which parses user input with long switch cases.

- ***Value :***
  With this parser, Inka v1.0 is able to run, and we could test all of our initial functionalities.


#### 2. Parser Version 2: OptionBuilder

- ***What it does :*** 
  The `OptionBuilder` class is a part of `Parser` Version 2, which custom builds `Options` object for the Apache Commons CLI parser, 
  allowing us to parse flags and their arguments in users' inputs more easily.


- ***Value :*** 
  Now we can scale the variety of acceptable command line inputs more easily. For example, we can now more easily support Inka to 
  take in multi-flag user input, such as `tag edit -o TAG_OLD -n TAG_NEW`.


- ***What it does :***
  This feature allows users to view what are the `tags` and `decks` that the card is currently associated with. It makes
  use of the
  reference to the `ArrayList` representing `tags` and `decks` inside the `Card` class to ensure that the
  appropriate `tags` and `decks` get displayed.


- ***Justification :*** : This feature is introduced so that users can make sure that their cards already have the
  correct `tags` and `decks`. This is because
  information provided by `card list` does not include the `tags` and `decks` for brevity purposes,
  Additionally, `questions` and `answers` displayed here are no longer truncated
  even when they exceed 50 characters.

### Code Contribution

The code contribution detected and analyzed using RepoSense can be
found [here.](https://nus-cs2113-ay2223s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2023-02-17&tabOpen=true&tabType=authorship&tabAuthor=mitchmalvin1&tabRepo=AY2223S2-CS2113-F10-1%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Team-based tasks :

- Attended and contributed to weekly team meetings.
- Fixed bugs raised during PE-dry-run such as the crucial `InvalidUUIDException`, the detailed list of bugs solved can
  be found
  in [#150.](https://github.com/AY2223S2-CS2113-F10-1/tp/pull/150)
- Contributed to various exception checking and the unit test to ensure that the exception message is appropriate.
- Contributed to the overall architecture of `Inka` such as all the `UUID` classes.
- Contributed to the skeleton code in `Inka` so the `tp` can get started.
- Set up the [AY2223-S2-CS21113-F10-1](https://github.com/AY2223S2-CS2113-F10-1) organization and
  the [tp](https://github.com/AY2223S2-CS2113-F10-1/tp) repository inside.
- Helped create issues in `v1.0` and `v2.0` milestones and close the milestones in a timely manner.
- Helped teammates with `git`  and `checkstyle` issues.
- Reviewed and merged teammates' PRs regularly.

### Documentation

#### User Guide

Add documentations for the following features :

- `card tag {-c CARD_UUID | -i CARD_INDEX} -t TAG_NAME ` to attach a tag onto a Card
- `card untag {-c CARDUUID | -i CARD_INDEX} {-t TAGNAME | -x TAG_INDEX}` to remove the tag from a Card
- `card view {-c CAR_DUUID | -i CARD_INDEX}` to view the card
- `tag edit -o OLD_NAME -n NEW_NAME` to edit the name of the Tag
- `tag list [-c CARD_UUID | -i CARD_INDEX]` to list all the Cards that fall under the Tag
- `tag delete {-t TAG_NAME | -x TAG_INDEX}` to remove the Tag from the TagList
- `tag deck {-t TAG_NAME | -x TAG_INDEX} -d DECK_NAME` to add all the cards that fall under a tag into the dck

#### Developer Guide

- Add Class Diagrams for `CardList` and `TagList`

The Class Diagram for `CardList` can be found below :
![CardList Class Diagram](../img/CardListClass.svg)

The Class Diagram for `TagList` can be found below :
![TagList Class Diagram](../img/TagListClass.svg)

- Add Sequence Diagram for `card view {-c CARDUUID | -i CARDINDEX}`, `card untag {-c CaRDUUID | -t TAGNAME}`
  and `tag list {-c CARDUUID | -i CARDINDEX}`

The Sequence Diagram for `card view {-c CARDUUID | -i CARDINDEX}` can be found below :
![Card View Sequence Diagram](../img/CardViewSequence.png)

The Sequence Diagram for `card untag {-c CaRDUUID | -t TAGNAME}` can be found below :
![Card Untag Sequence Diagram](../img/CardUntagSequence.png)

The Sequence Diagram for `tag list {-c CARDUUID | -i CARDINDEX}` can be found below :
![List Card Under Tag Sequence Diagram](../img/ListCardsUnderTagSequence.png)







