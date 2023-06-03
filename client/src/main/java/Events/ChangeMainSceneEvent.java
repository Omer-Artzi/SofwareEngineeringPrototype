package Events;

public class ChangeMainSceneEvent {

    String sceneName;

    public ChangeMainSceneEvent(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }
}
