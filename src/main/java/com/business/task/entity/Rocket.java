package com.business.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Rocket")
public class Rocket {
    @Id
    @Column(name = "ROCKET_ID")
    private String rocketId;
    @OneToMany(mappedBy = "rocket")
    private Set<Launch> launches;

    public static Rocket of(String rocketId) {
        return builder().rocketId(rocketId).build();
    }
}
