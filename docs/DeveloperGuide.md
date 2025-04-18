---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) project created by the SE-EDU initiative.
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/Main.java) and [`MainApp`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `student delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. The classes `PersonListPanel`, `SessionListPanel`, and `AttendanceRecordListPanel` inherit from the abstract `ListPanel` class which captures the commonalities of a panel in the GUI that displays a list of items. Each item in the list is represented as a card (e.g. `SessionCard`). All these, including the `MainWindow` and `ListPanel`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person`, `Session`, and `AttendanceRecord` objects residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `StudentCommandParser` and `DeleteStudentCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteStudentCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteStudentCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `StudentCommandParser`) which can then create more parsers as required (e.g., `AddStudentCommandParser`) and use the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddStudentCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `StudentCommandParser`, `AddStudentCommandParser`, `DeleteStudentCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person`, `Session`, and `AttendanceRecord` objects (which are contained in `UniquePersonList`, `UniqueSessionList` and `UniqueAttendanceRecordList` objects respectively), as well as the IDs of the next `Person` or `Session` to be added.
* stores the currently filtered `Person` and `Session` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` and `ObservableList<Session>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

### Storage component

**API** : [`Storage.java`](https://github.com/AY2425S2-CS2103T-T17-3/tp/tree/master/src/main/java/tutorly/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `tutorly.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Undo feature

There are several ways to build an undo feature. One way is to keep a stack of `AddressBook`s in memory. Each time a change is made to the `AddressBook`, push a copy of the current `AddressBook` onto the stack. When the user requests an undo, pop the top `AddressBook` from the stack and set it as the current `AddressBook`. This is a straightforward and relatively less error-prone way to implement undo. However, it has the following drawbacks:
* It requires a lot of memory to store multiple copies of the `AddressBook` object.
* It is not efficient to copy the entire `AddressBook` object every time a change is made.

#### Our implementation

We build upon this idea, but instead of keeping a stack of `AddressBook`s, we keep a stack of `Command`s. Each time a change is made to the `AddressBook`, we push the reverse of the command that made the change onto the stack. When the user requests an undo, we pop the top `Command` from the stack and execute it. This way, we do not need to keep multiple copies of the `AddressBook` object in memory. This is more efficient in terms of memory usage and performance.

Each `Command` defines its reverse operation during execution. When building the `CommandResult`, the `Command` also specifies the reverse command to be executed when the user requests an undo. This is kept track of by `LogicManager`, which maintains a stack of `Command`s. When the undo command is executed, `LogicManager` pops the top `Command` from the stack and executes it. The `Command` then executes its reverse operation on the `Model` to revert the changes made by the original command.

The following sequence diagram shows how an undo operation goes through the `Logic` component, when used to undo a student addition.

![Interactions Inside the Logic Component for the `undo` Command](images/UndoSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `AddStudentCommand`, `UndoCommand`, and `DeleteStudentCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* private tutor
* need to track large number of students' details and sessions
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: tracks and manages students’ details faster than a typical mouse/GUI driven app,
reducing manual effort and ensuring better organization.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                           | I want to …​                                     | So that I can…​                                                                   |
|----------|-----------------------------------|--------------------------------------------------|-----------------------------------------------------------------------------------|
| `* * *`  | new user                          | see usage instructions                           | refer to instructions when I forget how to use the app                            |
| `* * *`  | user                              | add a new student with basic details             | begin tracking their progress                                                     |
| `* * *`  | user                              | record additional student details                | recall other information about the student                                        |
| `* * *`  | user with many students           | search a student by name or phone number         | quickly find their details before a session without going through the entire list |
| `* * *`  | user                              | edit student records                             | update student details when they change                                           |
| `* * *`  | long-time user                    | delete old student records                       | stop tracking students that I no longer teach                                     |
| `* * *`  | user                              | record student attendance                        | track my student's participation                                                  |
| `* *`    | potential user                    | see the app populated with sample data initially | easily visualise how it will look like in real use                                |
| `* *`    | user ready to start using the app | delete all sample data                           | start fresh with my actual students                                               |
| `* *`    | user                              | log lesson feedback                              | keep track of progress of a student in the session                                |
| `* *`    | user                              | create custom tags for students                  | categorise them based on needs                                                    |
| `*`      | user with many students           | sort students by any field                       | locate a student easily                                                           |
| `*`      | user                              | filter students by custom tags                   | see all students with particular needs                                            |
| `*`      | expert user                       | bulk-edit lesson notes or assignments            | save time by updating multiple records at once                                    |
| `*`      | user teaching multiple subjects   | customise tracking fields for different subjects | tailor my records to different teaching needs                                     |
| `*`      | user teaching group classes       | create group sessions with multiple students     | track their progress collectively as a class                                      |
| `*`      | user                              | generate a progress report for a student         | share updates with parents                                                        |
| `*`      | user                              | receive a weekly summary of my sessions          | review my workload                                                                |
| `*`      | user                              | set reminder for upcoming sessions               | remember upcoming lessons                                                         |
| `*`      | user                              | view reminders for upcoming sessions             | plan my schedule                                                                  |
| `*`      | user                              | hide private contact details                     | minimise chance of someone else seeing them by accident                           |


### Use Cases

(For all use cases below, the **System** is `Tutorly`, and the **Actor** is the `tutor`, unless specified otherwise.)

---

**Use case: Add a student record**

**MSS**

1. Tutor requests to add a new student with the required details (Name, Phone, Email, Address, Tag, Memo).
2. Tutorly validates the input.
3. Tutorly adds the student profile to the database and confirms success.

   Use case ends.

**Extensions**

- 1a. Tutor does not provide all compulsory fields.
    - 1a1. Tutorly prompts for the missing information.
    - 1a2. Tutor enters the all the required details
    - Use case resumes at step 2.

- 2a. Tutor provides invalid input for any field.
    - 2a1. Tutorly displays an appropriate error message.
    - 2a2. Tutor corrects the input.
    - Use case resumes at step 2.

- 2b. The student already exists (Same Name).
    - 2b1. Tutorly displays an error message indicating the student already exists.
    - Use case ends.

---

**Use case: Search for a student record**

**MSS**

1. Tutor requests to search for a student by entering a query.
2. Tutorly validates the search query.
3. Tutorly retrieves and displays matching student profiles.

   Use case ends.

**Extensions**

- 3a. No students match the search query.
    - 3a1. Tutorly responds that no students match the search query.
    - Use case ends.

---

**Use case: Update a student record**

**MSS**

1. Tutor requests to update a student record by providing the student’s Identifier and updated details. (e.g., Name, Phone, Email, Address, Tag, Memo).
2. Tutorly validates the input.
3. Tutorly updates the student profile and confirms success.

   Use case ends.

**Extensions**

- 1a. Tutor does not provide any update parameters.
    - 1a1. Tutorly displays an error message indicating that there must be at least one update parameter.
    - Use case ends.

- 2a. The student Identifier does not exist.
    - 2a1. Tutorly responds that the student does not exist.
    - Use case ends.

- 2c. Tutor provides invalid input for any field.
    - 2c1. Tutorly displays an appropriate error message.
    - 2c2. Tutor corrects the input.
    - Use case resumes at step 2.

---

**Use case: Delete a student record**

**MSS**

1. Tutor requests to delete a student record by providing the student’s Identifier.
2. Tutorly validates the request and performs the action.
3. Tutorly confirms the success of the operation.

   Use case ends.

**Extensions**

- 2a. The student Identifier does not exist.
    - 2a1. Tutorly displays an error message indicating the student does not exist.
    - Use case ends.

---

**Use case: Add a Session**

**MSS**

1. Tutor requests to add a new session with the required details (Timeslot, Subject).
2. Tutorly validates the input.
3. Tutorly adds the session to the database and confirms success.

   Use case ends.

**Extensions**

- 1a. Tutor does not provide all required fields.
    - 1a1. Tutorly prompts for the missing information.
    - Use case resumes at step 2.

- 2a. Tutor provides invalid input for any field.
    - 2a1. Tutorly displays an appropriate error message.
    - 2a2. Tutor corrects the input.
    - Use case resumes at step 2.

- 2b. The timeslot overlaps with another existing session.
    - 2b1. Tutorly displays an error message indicating the timeslot overlaps with an existing session.
    - Use case ends.

---

**Use case: Enrol a student to a session**

**MSS**

1. Tutor requests to enrol a new student to an existing session by providing the student’s identifier and Session ID.
2. Tutorly validates the input.
3. Tutorly adds the student profile to the session and confirms success.

   Use case ends.

**Extensions**

- 1a. Tutor does not provide all required fields.
    - 1a1. Tutorly prompts for the missing information.
    - 1a2. Tutor corrects the input.
    - Use case resumes at step 2.

- 2a. Tutor provides invalid input for any field.
    - 2a1. Tutorly displays an appropriate error message.
    - 2a2. Tutor corrects the input.
    - Use case resumes at step 2.

--

**Use case: Mark attendance for a tutoring session**

**MSS**

1. Tutor requests to mark a session as attended by providing the student’s identifier, Session ID, and attendance status.
2. Tutorly validates the input.
3. Tutorly logs the attendance.
4. Tutorly confirms success.

   Use case ends.

**Extensions**
- 2a. The student identifier does not exist.
    - 2a1. Tutorly displays an error message showing the student identifier does not exist.
    - Use case ends.

- 2b. The Session ID does not exist.
    - 2b1. Tutorly displays an error message showing the session ID does not exist.
    - Use case ends.

- 2c. The student is not enrolled in the session.
    - 2c1. Tutorly displays an error message indicating the student is not enrolled in the session.
    - Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage, ensuring that typical operations (such as loading, searching, and editing records) complete within 3 seconds.
3.  The graphical user interface shall be easy to use such that a new user can complete primary workflows (e.g., adding a record or searching for a student) within 5 minutes of first use.
4.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5.  The codebase should be modular and well-documented, allowing for easier updates or the integration of future features. At least 90% of the codebase shall have inline or external documentation, and modules must have well-defined interfaces.
6.  The source code should be open source and shall be released under an approved open source license (e.g., MIT, Apache 2.0) and published in a publicly accessible repository with minimal entry barriers.

### Glossary

* **Tutor**: An educator who uses Tutorly to manage student details, schedule sessions, log lesson notes, and track attendance.
* **Student Record / Student Profile**: The digital record for each student stored in Tutorly.
* **Session**: A scheduled tutoring meeting or lesson.
* **Lesson**: The content delivered during a session.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

### Launch and Shutdown

1. **Initial Launch**
    1. Download the jar file and copy it into an empty folder.
    2. Double-click the jar file.
        - Expected: Shows the GUI with a set of sample students. The window size may not be optimal.

2. **Saving Window Preferences**
    1. Resize the window to an optimal size. Move the window to a different location. Close the window.
    2. Re-launch the app by double-clicking the jar file.
        - Expected: The most recent window size and location are retained.

### Saving Data

1. **Dealing with Missing Data Files**
    1. Simulate a missing file by renaming or deleting the data file.
    2. Launch the application.
        - Expected: Application should handle the error gracefully, by creating a new data file containing the sample data once any command is successfully executed. (For student/session add commands, it will also include the added student/session.)

2. **Dealing with Corrupted Data Files**
    1. Simulate a corrupted file by modifying the data file to be unreadable.
    2. Launch the application.
        - Expected: Application should clear the corrupted data file and create a empty data file once any command is successfully executed.

### Adding a Student

1. **Adding a New Student**
    1. Test case: Add a new student with valid details. `student add n/John Doe`
        - Expected: Student is added successfully, and a confirmation message is shown.
    2. Test case: Add a new student with invalid details (e.g., missing required fields `student add p/12345678`).
        - Expected: Error message is shown, prompting for correct input.

### Deleting a Student

1. **Deleting a Student**
    Prerequisite: The student must exist in the list.
    1. Test case: `student delete 1`
        - Expected: Student with id 1 is deleted from the list. Details of the deleted students are shown.
    2. Test case: `student delete 0`
        - Expected: No student is deleted. Error details are shown in the status message.
    3. Other incorrect delete commands to try: `student delete`, `student delete x`(where x is a number that is larger than the list size)
        - Expected: Similar to previous.

### Searching for a Student

1. **Search for a Student**
    1. Test case: Search for a student by name. `student search n/John`
        - Expected: A message "x students listed!" is shown. Matching student profiles are displayed. (x is the number of students matching the search query. If no matches are found, x = 0. If multiple matches are found, x > 1. If only one match is found, x = 1. The list of students is filtered to show only those matching the search query.)
    2. Test case: Search with a query that has no matches. `student search n/NonExistent`
        - Expected: "0 students listed!" message is shown.

### Editing a Student

1. **Editing a Student**
    1. Test case: Edit a student’s details with valid input. `student edit 1 n/John Smith`
        - Expected: Student details are updated successfully, and a confirmation message is shown.
    2. Test case: Edit a student’s details with invalid input. `student edit x n/John Smith`(x is a number larger than the list size)
        - Expected: Error message "Student not found!" is shown.
    3. Test case: Edit a student’s details with missing required fields. `student edit 1 n/`
        - Expected: Error message is shown, prompting for correct input.

### Adding a Session

1. **Adding a Session**
    1. Test case: Add a new session with valid details. `session add t/30 Mar 2025 11:30-13:30 sub/Math`
        - Expected: Session is added successfully, and a confirmation message is shown.
    2. Test case: Add a new session with invalid details (e.g., missing required fields). `session add`
        - Expected: Error message is shown, prompting for correct input.
    3. Test case: Add a new session with overlapping timeslots. `session add t/30 Mar 2025 11:30-13:30 sub/Science` 
   (Suppose there is a session whose time slot has overlapped with the new session's time slot.)
        - Expected: Error message is shown, indicating the timeslot overlaps with an existing session.

### Marking Attendance for a Session

1. **Marking attendance for a session**
    Prerequisite: The session and student must exist and the student must be enrolled in the session.
    1. Test case: Mark a session with valid input. `session mark 1 ses/1`
        - Expected: Attendance is marked successfully, and a confirmation message is shown.
    2. Test case: Mark a session as completed with invalid input (e.g., invalid session id). `session mark 1 ses/x`(x is a number larger than the list size)
        - Expected: Error message is shown, prompting for correct input.
    3. Test case: Mark a session which the student is not enrolled in. `session mark 1 ses/2`
        - Expected: Error message is shown, indicating the student is not enrolled in the session.

### Adding Feedback

1. **Adding feedback for a session**
    Prerequisite: The session and student must exist and the student must be enrolled in the session.
    1. Test case: Add feedback with valid input. `session feedback 1 ses/1 f/Great session!`
        - Expected: Feedback is added successfully, and a confirmation message is shown.
    2. Test case: Add feedback with invalid input (e.g., missing required fields). `session feedback`
        - Expected: Error message is shown, prompting for correct input.
    3. Test case: Add feedback for a session which the student is not enrolled in. `session feedback 1 ses/2 f/Great session!`
   (Suppose student 1 is not enrolled in session 2)
        - Expected: Error message is shown, indicating the student is not enrolled in the session.

### Undo Feature

1. **Undo Operations**
    1. Test case: Perform an action (e.g., add a student), then undo the action.
        - Expected: The action is undone, and the previous state is restored. There is also a confirmation message shown.

### Error Handling

1. **Invalid Commands**
    1. Test case: Enter an invalid command.
        - Expected: Error message is shown, indicating the command is not recognized/unknown.

2. **System Errors**
    1. Test case: Simulate a system error (e.g., by corrupting a data file).
        - Expected: Application handles the error gracefully, removing or replacing the corrupted data file

These instructions provide a starting point for testers to work on; testers are expected to do more exploratory testing.

## **Appendix: Planned Enhancements**

1. Enhance undo functionality for the add command to allow for rollback of assigned IDs.
2. Improve the output display to seamlessly present long messages without requiring scrolling.
3. Refine cell selection behavior in the UI to reduce flickering and improve responsiveness.
4. Optimize the list view so it dynamically adjusts its height in response to changes in the filtered item count.
5. Enhance the UI focus management to ensure that the correct item is consistently highlighted.
6. Add support for multiple students of the same name.
7. A `redo` command to undo an `undo` command.
8. Improved `search` command for `student` and `session` with other fields including **tags** and **date/time range** with control over matching **any** or **all** fields.
9. `class` management commands that handle adding of **multiple** sessions and **mass** enrolling/marking of attendance for students.
10. Viewing sessions each student is enrolled in via the `students` tab.

