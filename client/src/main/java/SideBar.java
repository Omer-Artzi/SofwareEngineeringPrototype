import Events.ChangeMainSceneEvent;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public interface SideBar {

    default void changeScene(String sceneName) {
        ChangeMainSceneEvent event = new ChangeMainSceneEvent(sceneName);
        EventBus.getDefault().post(event);
    }
}
