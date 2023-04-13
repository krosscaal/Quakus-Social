package io.github.krosscaal.quarkussocial.rest.dto;

import io.github.krosscaal.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {

    private Long id;

    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Follower follower){
       this (follower.getId(), follower.getFollower().getName());
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
