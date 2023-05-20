package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Question;

import java.util.List;

public class ApiResponse {
    private int response_code;

    private List<ResponseQuestion> results;

    public List<ResponseQuestion> getResults() {
        return results;
    }
}
