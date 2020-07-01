package com.business.task;

import com.business.task.models.RocketLaunchResponse;
import com.business.task.service.SpaceXService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final SpaceXService spaceXService;

    @GetMapping("/rockets")
    public List<String> getRockets() {
        return spaceXService.getRockets();
    }

    @GetMapping("/rocket/launch/{rocket_id}")
    public List<RocketLaunchResponse> getRocketLaunches(@PathVariable("rocket_id") String rocket_id) {
        return spaceXService.getRocketLaunches(rocket_id);
    }

}
