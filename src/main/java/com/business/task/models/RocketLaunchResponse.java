package com.business.task.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RocketLaunchResponse {

    private String missionName;
    private String launchYear;
    private String link;
}
