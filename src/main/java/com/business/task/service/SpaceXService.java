package com.business.task.service;

import com.business.task.entity.Launch;
import com.business.task.entity.Rocket;
import com.business.task.models.RocketLaunchResponse;
import com.business.task.repo.LaunchRepository;
import com.business.task.repo.RocketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpaceXService {

    private static final String GET_ALL_ROCKETS_URL = "https://api.spacexdata.com/v3/rockets";
    private static final String GET_ALL_LAUNCHES_URL = "https://api.spacexdata.com/v3/launches";

    private final LaunchRepository launchRepository;
    private final RocketRepository rocketRepository;

    @Transactional
    public List<String> getRockets() {
        String jsonResponse = callSpaceX(GET_ALL_ROCKETS_URL);

        log.info("Response: " + jsonResponse);
        List<String> rocketIds = parseToRocketIds(jsonResponse);

        rocketIds.forEach(this::save);
        return rocketIds;
    }

    @Transactional
    public List<RocketLaunchResponse> getRocketLaunches(String rocketId) {
        String jsonContent = callSpaceX(GET_ALL_LAUNCHES_URL + "?" + rocketId);

        log.info("Response: " + jsonContent);

        List<RocketLaunchResponse> rocketLaunchResponses = parseToRocketLaunches(jsonContent);
        rocketLaunchResponses.stream()
                .map(rocketLaunchResponse -> toLaunch(rocketLaunchResponse, rocketId))
                .forEach(launchRepository::save);

        return rocketLaunchResponses;
    }

    private String callSpaceX(String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection con;

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);

            log.info("Response status: " + con.getResponseCode());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (IOException e) {
            log.error("Error while calling spaceX api, url: {}", stringUrl, e);
            return null;
        }
    }

    private Launch toLaunch(RocketLaunchResponse rocketLaunchResponse, String rocketId) {
        return Launch.builder()
                .launchYear(rocketLaunchResponse.getLaunchYear())
                .link(rocketLaunchResponse.getLink())
                .missionName(rocketLaunchResponse.getMissionName())
                .rocket(Rocket.of(rocketId))
                .build();
    }

    private List<String> parseToRocketIds(String jsonResponse) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(jsonResponse);
            Iterator<JSONObject> iterator = jsonArray.iterator();
            List<String> rocketIds = new ArrayList<>();

            while (iterator.hasNext()) {
                rocketIds.add((String) iterator.next().get("rocket_id"));
            }

            return rocketIds;

        } catch (ParseException e) {
            e.printStackTrace();
            return emptyList();
        }
    }

    private List<RocketLaunchResponse> parseToRocketLaunches(String jsonResponse) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray;
            jsonArray = (JSONArray) jsonParser.parse(jsonResponse);
            Iterator<JSONObject> iterator = jsonArray.iterator();
            List<RocketLaunchResponse> rocketLaunchResponses = new ArrayList<>();
            while (iterator.hasNext()) {
                JSONObject currentObject = iterator.next();
                rocketLaunchResponses.add(toRocketLaunch(currentObject));
            }

            return rocketLaunchResponses;
        } catch (Exception e) {
            log.error("Error while parsing response", e);
            return emptyList();
        }
    }

    private RocketLaunchResponse toRocketLaunch(JSONObject currentObject) {
        return RocketLaunchResponse.builder()
                .launchYear((String) currentObject.get("launch_year"))
                .link(toLink((JSONObject) currentObject.get("links")))
                .missionName((String) currentObject.get("mission_name"))
                .build();
    }

    private String toLink(JSONObject jsonObject) {
        return (String) jsonObject.get("wikipedia");
    }

    private void save(String rocketId) {
        rocketRepository.save(Rocket.builder().rocketId(rocketId).build());
    }
}
