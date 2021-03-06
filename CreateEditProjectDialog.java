import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * CreateEditTaskDialog is a JDialog that handles creating and editing projects.
 *
 * A user can modify the name of the Project by inputting a String in the JTextfield
 * The status of the project can be manipulated with commands given with a representation
 * of all the statuses in a JList.
 *
 * Users can Add, Delete, Move, and Rename Statuses.
 * Created by Lok Man Chu
 */
public class CreateEditProjectDialog extends JDialog {


    private JLabel nameLabel = new JLabel("Project Name: ");
    private JTextField projectName;
    private JList<String> list;
    private JScrollPane listScroll;
    private ProjectModel projectModel = new ProjectModel("", null);
    private ProjectModel mainModel;
    private MainScreen parent;
    private JButton upButton = new JButton("Move Up");
    private JButton downButton = new JButton("Move Down");
    private JButton addButton = new JButton("Add Status");
    private JButton removeButton = new JButton("Remove Status");
    private JButton confirmButton = new JButton("Confirm");
    private JButton cancelButton = new JButton("Cancel");
    private JButton editButton = new JButton("Edit Status");


    /**
     * Private Constructor for CreateEditProjectDialog. Construct new Dialog from show method
     *
     * Jobs covered:
     * - Creates JDialog
     * - Creates a ProjectModel to make changes on.
     * - Creates a ListModel for JList
     * - Calls GUI creation method
     * @param parent MainScreen of TaskBoard
     * @param model  ProjectModel to be edited, null if new Project is to be constructed
     */
    private CreateEditProjectDialog(MainScreen parent, ProjectModel model) {
        super(SwingUtilities.getWindowAncestor(parent));
        this.parent = parent;
        projectModel.copyFrom(model);
        mainModel = model;
        list = new JList<String>(projectModel);
        createGUI();
    }

    /**
     * Static method for creating CreateEditProjectDialog.
     * This call is used for Creating or Editing a Project
     * @param parent MainScreen calling the Dialog
     * @param projectModel ProjectModel to be modified. null if new
     */
    public static void show(MainScreen parent, ProjectModel projectModel) {
        new CreateEditProjectDialog(parent, projectModel);
    }

    /**
     * Creates the GUI for CreateEditProjectDialog
     *
     * Jobs covered:
     * - Sets Dialog settings for modality and resizing
     * - Sets text to be for Creating Task or Editing Task
     * - Sets default values of name input field
     * - Adds all elements to Dialog
     * - Assigns ActionListeners to all buttons
     */
    private void createGUI() {
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setModal(true);
        this.setResizable(false);

        this.setTitle((mainModel == null ? "Create" : "Edit") + " " + (mainModel == null ? "Project" : mainModel.getName()));
        confirmButton.setText(mainModel == null ? "Create" : "Edit");
        projectName = new JTextField(mainModel == null ? ("Project " + (parent.getTaskBoardModel().numProjects() + 1)) : mainModel.getName());
        projectName.setPreferredSize(new Dimension(125, 26));
        listScroll = new JScrollPane(list);

        Dimension size = new Dimension(120, 26);
        upButton.setMinimumSize(size);
        downButton.setMinimumSize(size);
        addButton.setMinimumSize(size);
        removeButton.setMinimumSize(size);
        editButton.setMinimumSize(size);
        upButton.setMaximumSize(size);
        downButton.setMaximumSize(size);
        addButton.setMaximumSize(size);
        removeButton.setMaximumSize(size);
        editButton.setMaximumSize(size);
        list.setVisibleRowCount(8);
        list.setPrototypeCellValue("abcdefghijklmnopqr");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JPanel namePanel = new JPanel();
        namePanel.add(nameLabel);
        namePanel.add(projectName);
        mainPanel.add(namePanel);
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
        boxPanel.add(upButton);
        boxPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        boxPanel.add(downButton);
        boxPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        boxPanel.add(addButton);
        boxPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        boxPanel.add(removeButton);
        boxPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        boxPanel.add(editButton);
        JPanel statusPanel = new JPanel();
        statusPanel.add(boxPanel);
        statusPanel.add(listScroll);
        mainPanel.add(statusPanel);
        JPanel confirmCancelPanel = new JPanel();
        confirmCancelPanel.add(confirmButton);
        confirmCancelPanel.add(cancelButton);
        mainPanel.add(confirmCancelPanel);

        upButton.addActionListener((ActionEvent e) -> {
            moveUp();
        });
        downButton.addActionListener((ActionEvent e) -> {
            moveDown();
        });
        addButton.addActionListener((ActionEvent e) -> {
            addStatus();
        });
        removeButton.addActionListener((ActionEvent e) -> {
            deleteStatus();
        });
        editButton.addActionListener((ActionEvent e) -> {
            changeName();
        });
        confirmButton.addActionListener((ActionEvent e) -> {
            confirm();
            this.dispose();
        });
        cancelButton.addActionListener((ActionEvent e) -> {
            this.dispose();
        });

        this.add(mainPanel);

        pack();
        this.setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * addStatus method prompts the user for a String name with a InputDialog.
     * If the String entered is not empty, a new Status will be created with
     * String name. If the String is empty, a new Status will not be added
     * and a notification sound will play.
     */
    private void addStatus() {
        String name = JOptionPane.showInputDialog(this, "Enter new status name:", null);
        if (!"".equals(name)) {
            projectModel.addStatus(name);
            list.setSelectedIndex(projectModel.numStatuses() - 1);
            list.updateUI();
        }
        else
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * changeName allows for a user to change the name of the Status currently selected in the JList
     */
    private void changeName() {
        String name = JOptionPane.showInputDialog(this, "Enter new status name:", null);
        if (name != null) {
            projectModel.editStatus(list.getSelectedIndex(), name);
            list.updateUI();
        }
    }

    /**
     * deleteStatus method prompts the user if they want to delete the selected status.
     * If Yes, the status is deleted along with all the Tasks within it.
     * If No, the status is not deleted.
     *
     * If there is no Status selected, or if there are no Statuses available, a notification sound will play.
     */
    private void deleteStatus() {
        int index = list.getSelectedIndex();
        if (index == -1) {
            Toolkit.getDefaultToolkit().beep();
        } else if (JOptionPane.showConfirmDialog(this, "Are You Sure You Want to " +
                "Delete This Status?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            projectModel.deleteStatus(index);
            list.clearSelection();
            list.updateUI();
        }
    }

    /**
     * moveUp method moves the selected status up in the JList.
     * Will play a notification noise if the selected status cannot move farther up.
     */
    private void moveUp() {
        int index = list.getSelectedIndex();
        if (index <= 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        } else {
            projectModel.swap(index, index - 1);
            list.setSelectedIndex(index - 1);
            list.updateUI();
        }
    }

    /**
     * moveDown method moves the selected status down in the JList.
     * Will play a notification noise if the selected status cannot move farther down.
     */
    private void moveDown() {
        int index = list.getSelectedIndex();
        if (index == -1 || index == projectModel.numStatuses() - 1) {
            Toolkit.getDefaultToolkit().beep();
            return;
        } else {
            projectModel.swap(index, index + 1);
            list.setSelectedIndex(index + 1);
            list.updateUI();
        }
    }

    /**
     * confirm is called when the Create or Edit button is pressed.
     * If Create, a new ProjectModel will be added to the TaskBoardModel with
     * statuses shown on the JList.
     *
     * If Edit, the ProjectModel will be modified to the values in the Dialog
     */
    private void confirm()
    {
        projectModel.setName(projectName.getText());
        projectModel.addListener(parent);
        if (mainModel == null) {
            parent.getTaskBoardModel().addProjects(projectModel);
        }
        else
        {
            parent.getCurrProj().copyFrom(projectModel);
            parent.getCurrProj().update();
        }
    }
}
