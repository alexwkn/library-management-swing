package com.librarymanagement.view;

import com.librarymanagement.controller.CategoryController;
import com.librarymanagement.controller.UserController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.User;
import com.librarymanagement.util.FormValidator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.time.LocalDate;

/**
 * Formulaire de création ({@code existingUser == null}) ou de modification d'un utilisateur.
 */
public class UserFormPanel extends JPanel {

    private static final int COLUMNS = 25;
    private static final String NO_CATEGORY = "(aucune)";

    private final MainFrame mainFrame;
    private final UserController userController = new UserController();
    private final User existingUser;

    private final JTextField firstName = new JTextField(COLUMNS);
    private final JTextField familyName = new JTextField(COLUMNS);
    private final JTextField email = new JTextField(COLUMNS);
    private final JTextField pseudo = new JTextField(COLUMNS);
    private final JPasswordField password = new JPasswordField(COLUMNS);
    private final JSpinner birthdate = SwingUtils.dateSpinner(LocalDate.of(2000, 1, 1));
    private final JTextField streetName = new JTextField(COLUMNS);
    private final JTextField streetNumber = new JTextField(COLUMNS);
    private final JTextField postbox = new JTextField(COLUMNS);
    private final JTextField postcode = new JTextField(COLUMNS);
    private final JTextField locality = new JTextField(COLUMNS);
    private final JTextField country = new JTextField("Belgique", COLUMNS);
    private final JTextField phoneNumber = new JTextField(COLUMNS);
    private final JComboBox<String> preferredCategory = new JComboBox<>();
    private final JCheckBox student = new JCheckBox();
    private final JCheckBox admin = new JCheckBox();

    public UserFormPanel(MainFrame mainFrame, User existingUser) {
        this.mainFrame = mainFrame;
        this.existingUser = existingUser;
        boolean isUpdate = existingUser != null;

        preferredCategory.addItem(NO_CATEGORY);
        try {
            for (Category category : new CategoryController().getAllCategories()) {
                preferredCategory.addItem(category.getLabel());
            }
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }

        FormLayout form = new FormLayout(this);
        form.title(isUpdate ? "Modification de l'utilisateur n°" + existingUser.getId() : "Nouvel utilisateur");
        form.field("Prénom *", firstName);
        form.field("Nom *", familyName);
        form.field("Email *", email);
        form.field("Nom d'utilisateur", pseudo);
        form.field(isUpdate ? "Nouveau mot de passe (vide = inchangé)" : "Mot de passe * (8 caractères min.)", password);
        form.field("Date de naissance *", birthdate);
        form.field("Rue *", streetName);
        form.field("Numéro *", streetNumber);
        form.field("Boîte", postbox);
        form.field("Code postal *", postcode);
        form.field("Localité *", locality);
        form.field("Pays *", country);
        form.field("Téléphone", phoneNumber);
        form.field("Catégorie préférée", preferredCategory);
        form.field("Étudiant", student);
        form.field("Administrateur", admin);

        JButton cancel = new JButton("Annuler");
        cancel.addActionListener(e -> mainFrame.showUserListing());
        JButton submit = new JButton(isUpdate ? "Enregistrer les modifications" : "Créer l'utilisateur");
        submit.addActionListener(e -> submit());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(cancel);
        buttons.add(submit);
        form.button(buttons);

        if (isUpdate) fill(existingUser);
    }

    private void fill(User user) {
        firstName.setText(user.getFirstName());
        familyName.setText(user.getFamilyName());
        email.setText(user.getEmail());
        pseudo.setText(user.getPseudo());
        SwingUtils.setDate(birthdate, user.getBirthdate());
        streetName.setText(user.getStreetName());
        streetNumber.setText(String.valueOf(user.getStreetNumber()));
        postbox.setText(user.getPostbox());
        postcode.setText(user.getPostcode());
        locality.setText(user.getLocality());
        country.setText(user.getCountry());
        phoneNumber.setText(user.getPhoneNumber());
        preferredCategory.setSelectedItem(user.getPreferredCategory() == null ? NO_CATEGORY : user.getPreferredCategory());
        student.setSelected(user.isStudent());
        admin.setSelected(user.isAdmin());
    }

    private void submit() {
        try {
            User user = readForm();
            if (existingUser == null) {
                userController.createUser(user);
                mainFrame.displayMessage("Utilisateur créé avec succès.", "Succès");
            } else {
                userController.updateUser(user);
                mainFrame.displayMessage("Utilisateur modifié avec succès.", "Succès");
            }
            mainFrame.showUserListing();
        } catch (ValidationException | DataAccessException e) {
            mainFrame.displayException(e);
        }
    }

    private User readForm() throws ValidationException {
        User user = new User();
        if (existingUser != null) user.setId(existingUser.getId());
        user.setFirstName(FormValidator.trimToNull(firstName.getText()));
        user.setFamilyName(FormValidator.trimToNull(familyName.getText()));
        user.setEmail(FormValidator.trimToNull(email.getText()));
        user.setPseudo(FormValidator.trimToNull(pseudo.getText()));
        user.setPassword(new String(password.getPassword()));
        user.setBirthdate(SwingUtils.getDate(birthdate));
        user.setStreetName(FormValidator.trimToNull(streetName.getText()));
        String number = streetNumber.getText().trim();
        if (!FormValidator.isNumber(number) || number.length() > 6) {
            throw new ValidationException("Le numéro de rue doit être un nombre.");
        }
        user.setStreetNumber(Integer.parseInt(number));
        user.setPostbox(FormValidator.trimToNull(postbox.getText()));
        user.setPostcode(FormValidator.trimToNull(postcode.getText()));
        user.setLocality(FormValidator.trimToNull(locality.getText()));
        user.setCountry(FormValidator.trimToNull(country.getText()));
        user.setPhoneNumber(FormValidator.trimToNull(phoneNumber.getText()));
        String category = (String) preferredCategory.getSelectedItem();
        user.setPreferredCategory(category == null || NO_CATEGORY.equals(category) ? null : category);
        user.setStudent(student.isSelected());
        user.setAdmin(admin.isSelected());
        return user;
    }
}
