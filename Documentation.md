# Easy Flashcards
#### Screens
1. SetOverview
1. AddOrEditSetDialog
1. Set
1. AddOrEditCardDialog
1. Play
1. Settings
1. More
1. DuplicateFinder
1. Licenses
## SetOverview

The dialog consists of: 
* a list that contains set-items
* a text that displays an "empty"-message
* a button that starts the game with cards front-face up
* a button that starts the game with cards back-face up
* a button with the symbol 'add'
* an overflow menu containing 'More', 'Settings' and 'Select All'

**Scenarios:**
1. The user opens the screen for the first time
2. The user opens the screen when there are no sets and have seen the tutorial 'SetOverview'
3. The user opens the screen when there is one set
4. The user opens the screen when there is more than one set

#### The user opens the screen for the first time
When the user opens the screen for the first time, they're greeted with a tutorial that explains that there are no sets yet and that they have to create a new set, highlighting the 'Add'-Button used to add sets. The user may click anywhere on the screen to close the tutorial. If they click on the 'Add'-button, the AddOrEditSetDialog is opened. More on that in the AddOrEditSetDialog-section.

#### The user opens the screen when there are no sets and have seen the tutorial 'SetOverview'

When the user opens the screen when there are no sets and have seen the tutorial 'SetOverview', they will see an empty list of sets. In addition, a text is displayed that says "There are currently no sets available. Click the Add-Button to add a set". When the user clicks on the 'Add'-Button, the AddOrEditSetDialog is opened.

#### The user opens the screen when there is one set

When the user opens the screen when there is one set, they will see the list with one item. The empty-text is no longer visible.

#### The user opens the screen when there is more than one set

When the user opens the screen when there is more than one set and hasn't seen the tutorial

## AddOrEditSetDialog

**Scenarios:**
1. The User has opened this dialog by clicking the 'Add'-Button in the SetOverview
2. The User has opened this dialog by clicking the 'Edit'-Button in the Set

#### The User has opened this dialog by clicking the 'Add'-Button in the SetOverview

The dialog opens, they keyboard is visible and the cursor is placed in the 
