package com.business.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "LAUNCH")
public class Launch {
    @Id
    @Column(name = "MISSION_NAME")
    private String missionName;
    @Column(name = "LAUNCH_YEAR")
    private String launchYear;
    @Column(name = "LINK")
    private String link;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ROCKET_ID")
    private Rocket rocket;
}
