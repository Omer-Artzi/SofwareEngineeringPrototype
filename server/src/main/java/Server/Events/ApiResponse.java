package Server.Events;

import java.util.List;

public class ApiResponse {
    private int response_code;

    private List<ResponseQuestion> results;

    public List<ResponseQuestion> getResults() {
        return results;
    }
}
