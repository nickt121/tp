package tutorly.logic.commands;

import static java.util.Objects.requireNonNull;
import static tutorly.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static tutorly.logic.parser.CliSyntax.PREFIX_EMAIL;
import static tutorly.logic.parser.CliSyntax.PREFIX_MEMO;
import static tutorly.logic.parser.CliSyntax.PREFIX_NAME;
import static tutorly.logic.parser.CliSyntax.PREFIX_PHONE;
import static tutorly.logic.parser.CliSyntax.PREFIX_TAG;
import static tutorly.model.Model.FILTER_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import tutorly.commons.util.CollectionUtil;
import tutorly.commons.util.ToStringBuilder;
import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.person.Address;
import tutorly.model.person.Email;
import tutorly.model.person.Identity;
import tutorly.model.person.Memo;
import tutorly.model.person.Name;
import tutorly.model.person.Person;
import tutorly.model.person.Phone;
import tutorly.model.tag.Tag;
import tutorly.ui.Tab;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditStudentCommand extends StudentCommand {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_STRING = StudentCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Edits the details of the student identified by a STUDENT_IDENTIFIER (ID or full name). "
            + "Existing values will be overwritten by the input values."
            + "\nParameters: STUDENT_IDENTIFIER "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_MEMO + "MEMO]"
            + "\nExample: " + COMMAND_STRING + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private final Identity identity;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param identity             containing the ID or name of the person to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditStudentCommand(Identity identity, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(identity);
        requireNonNull(editPersonDescriptor);

        this.identity = identity;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}. The ID of the person cannot be edited.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Memo updatedMemo = editPersonDescriptor.getMemo().orElse(personToEdit.getMemo());

        Person person = new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags, updatedMemo);
        person.setId(personToEdit.getId());
        return person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Person> personToEdit = model.getPersonByIdentity(identity);
        if (personToEdit.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND);
        }

        Person editedPerson = createEditedPerson(personToEdit.get(), editPersonDescriptor);

        if (!personToEdit.get().isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit.get(), editedPerson);
        model.updateFilteredPersonList(FILTER_SHOW_ALL_PERSONS);
        return new CommandResult.Builder(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)))
                .withTab(Tab.student(editedPerson))
                .withReverseCommand(new EditStudentCommand(
                        new Identity(personToEdit.get().getId()), EditPersonDescriptor.fromPerson(personToEdit.get())))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditStudentCommand otherEditCommand)) {
            return false;
        }

        return identity.equals(otherEditCommand.identity)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("identity", identity)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Memo memo;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setMemo(toCopy.memo);
        }

        /**
         * Returns a {@code EditPersonDescriptor} with the same values as {@code person}.
         */
        public static EditPersonDescriptor fromPerson(Person person) {
            EditPersonDescriptor descriptor = new EditPersonDescriptor();
            descriptor.setName(person.getName());
            descriptor.setPhone(person.getPhone());
            descriptor.setEmail(person.getEmail());
            descriptor.setAddress(person.getAddress());
            descriptor.setTags(person.getTags());
            descriptor.setMemo(person.getMemo());
            return descriptor;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags, memo);
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        public Optional<Memo> getMemo() {
            return Optional.ofNullable(memo);
        }

        public void setMemo(Memo memo) {
            this.memo = memo;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(memo, otherEditPersonDescriptor.memo);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("memo", memo)
                    .toString();
        }
    }
}
