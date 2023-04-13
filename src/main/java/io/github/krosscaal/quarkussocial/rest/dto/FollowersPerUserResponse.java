package io.github.krosscaal.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {

    private Integer followersCount;

    private List<FollowerResponse> content;

}
