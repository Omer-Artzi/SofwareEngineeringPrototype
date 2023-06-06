import Entities.Message;
import Events.ChangeMainSceneEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.greenrobot.eventbus.EventBus;

import javafx.scene.control.Button;
import java.io.IOException;

public class SideBar {

    enum SidebarState {
        COLLAPSED,
        EXPANDED
    }

    SidebarState sidebarState = SidebarState.EXPANDED;

    @FXML
    private Button menuButton;

    @FXML
    void OnMenuButtonPressed(ActionEvent event) {
        if (sidebarState == SidebarState.EXPANDED) {
            sidebarState = SidebarState.COLLAPSED;
            menuButton.setText(">");
        } else {
            sidebarState = SidebarState.EXPANDED;
            menuButton.setText("<");
        }
    }

    void ChangeScene(String sceneName) {
        ChangeMainSceneEvent event = new ChangeMainSceneEvent(sceneName);
        EventBus.getDefault().post(event);
    }

    @FXML
    void OnLogoutButtonPressed(ActionEvent event) throws IOException {
        Logout();
    }

    void Logout() throws IOException {
        Message message = new Message(1, "Logout", SimpleClient.getUser());
        SimpleClient.getClient().sendToServer(message);
        UnregisterFromEventBus();
        SimpleChatClient.NewSetRoot("Login");
    }

    void RegisterToEventBus() {
        EventBus.getDefault().register(this);
    }

    void UnregisterFromEventBus() {
        EventBus.getDefault().unregister(this);
    }
}
