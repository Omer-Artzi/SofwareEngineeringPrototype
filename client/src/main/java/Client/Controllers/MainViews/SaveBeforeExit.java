package Client.Controllers.MainViews;

import Client.Events.ChangeMainSceneEvent;
import Client.Events.LogoutEvent;
import Client.SimpleChatClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Optional;

public class SaveBeforeExit {

    /**
     * Starts the process of preventing loss of data when changing scenes
     * @param event ChangeMainSceneEvent is sent changes scene through the Sidebar
     */
    @Subscribe
    public void TriggerDataCheck(ChangeMainSceneEvent event) {
        boolean unsavedData = CheckForUnsavedData();
        System.out.println("TriggerDataCheck unsavedData: " + unsavedData);
        Platform.runLater(() -> {
            if (unsavedData) {
                boolean changeScreen = PromptUserToSaveData(event.getSceneName());
            }
            else {
                try {
                    EventBus.getDefault().unregister(this);
                    SimpleChatClient.setRoot(event.getSceneName());
                    System.out.println("TriggerDataCheck changing scene");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Prompts the user to save data before changing scenes
     * @param sceneName the name of the scene to change to
     * @return true if the user chose to save the data, false if the user chose not to save the data or cancelled
     */
    @FXML
    public boolean PromptUserToSaveData(String sceneName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved data. Would you like to save it?", ButtonType.YES, javafx.scene.control.ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Unsaved Data");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                //System.out.println("User chose to save data.");
                try {
                    SaveData();
                    EventBus.getDefault().unregister(this);
                    SimpleChatClient.setRoot(sceneName);
                    System.out.println("PromptUserToSaveData changing scene");
                    return true;
                }
                catch (SaveDataFailedException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to save data.", ButtonType.OK);
                    errorAlert.setTitle("Error");
                    errorAlert.showAndWait();
                    return false;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (result.get().equals(ButtonType.NO)) {
                //System.out.println("User chose not to save data.");
                try {
                    SimpleChatClient.setRoot(sceneName);
                    EventBus.getDefault().unregister(this);
                    System.out.println("PromptUserToSaveData changing scene 2");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            else if (result.get() == ButtonType.CANCEL) {
                //System.out.println("User chose to cancel.");
                return false;
            }

        }
        return false;
    }

    /**
     * Checks if there is unsaved data in the scene
     * method to be overriden if screen has potentially unsaved data
     * @return true if there is unsaved data in the scene
     */
    public boolean CheckForUnsavedData() {
        System.out.println("CheckForUnsavedData Client.Controllers.MainPanelScreens.SaveBeforeExit");
        return false;
    }

    /**
     * Saves the data in the scene
     * method to be overriden if screen has potentially savable data
     * @throws SaveDataFailedException if the data save failed
     */
    public void SaveData() throws SaveDataFailedException {

    }

    public static class SaveDataFailedException extends Exception {
    }

    @Subscribe
    public void UnregisterOnLogout(LogoutEvent event){
        EventBus.getDefault().unregister(this);
    }

    // flow: sidebar sends an event with the target scene name.
    // receiver checks if there is unsaved data.
    // if there isn't, it loads the scene.
    // if there is, it prompts the user to save the data.
    // if the user chooses not to save the data, it loads the scene.
    // if the user chooses to save the data, it saves the data and then loads the scene.
    /// if data save fails, it shows an error message and doesn't load the scene.
    // if the user chooses to cancel, it doesn't load the scene.
}
