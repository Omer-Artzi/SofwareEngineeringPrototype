package Client.Controllers.Sidebars;

import Client.Events.ChangeMainSceneEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class SideBar {

    enum SidebarState {
        COLLAPSED,
        EXPANDED
    }

    SidebarState sidebarState = SidebarState.EXPANDED;

    @FXML
    private AnchorPane sideBarPane;

    @FXML
    private VBox sidebarVBox;

    private final int vBoxExpandedWidth = 180;

    private final int vBoxCollapsedWidth = 40;

    final Animation ExpandAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(1000));
        }

        protected void interpolate(double frac) {
            final int n = Math.round((vBoxExpandedWidth-vBoxCollapsedWidth) * (float) frac);
            //System.out.println(vBoxCollapsedWidth + n);
            sideBarPane.setPrefWidth(vBoxCollapsedWidth + n);
        }

    };

    final Animation CollapseAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(1000));
        }

        protected void interpolate(double frac) {
            final int n = Math.round((vBoxExpandedWidth-vBoxCollapsedWidth) * (float) frac);
            //System.out.println(vBoxExpandedWidth - n);
            sideBarPane.setPrefWidth(vBoxExpandedWidth - n);
        }

    };

    @FXML
    private Button menuButton;

    @FXML
    void initialize() {

    }

    /**
     * This method is called when the user clicks on the "Menu" button and collpases/expands the sidebar
     */
    @FXML
    void OnMenuButtonPressed(ActionEvent event) {
        if (sidebarState == SidebarState.EXPANDED) {
            sidebarState = SidebarState.COLLAPSED;
            System.out.println("Collapsing");
            CollapseAnimation.play();
            for(int i = 0; i < sidebarVBox.getChildren().size(); i++) {
                ((Button) sidebarVBox.getChildren().get(i)).setGraphicTextGap(20);
            }
            //menuButton.setAlignment(Pos.BASELINE_LEFT);

        } else {
            sidebarState = SidebarState.EXPANDED;
            System.out.println("Expanding");
            ExpandAnimation.play();
            for(int i = 0; i < sidebarVBox.getChildren().size(); i++) {
                ((Button) sidebarVBox.getChildren().get(i)).setGraphicTextGap(4);
            }
            //menuButton.setAlignment(Pos.BASELINE_CENTER);
        }
    }


    /**
     * called when a sidebar button is clicked to handle scene switching
     */
    void ChangeScene(String sceneName) {
        ChangeMainSceneEvent event = new ChangeMainSceneEvent(sceneName);
        System.out.println("sidebar changing scene to " + sceneName);
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
        //SimpleChatClient.setScene(new Scene(SimpleChatClient.loadFXML("login"), 1024, 768));
        SimpleChatClient.getClientStage().setScene(SimpleChatClient.getScene());
        SimpleChatClient.getClientStage().centerOnScreen();
    }

    void RegisterToEventBus() {
        EventBus.getDefault().register(this);
    }

    void UnregisterFromEventBus() {
        EventBus.getDefault().unregister(this);
    }
}
