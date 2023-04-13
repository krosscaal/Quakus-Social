package io.github.krosscaal.quarkussocial.rest;

import io.github.krosscaal.quarkussocial.domain.model.Follower;
import io.github.krosscaal.quarkussocial.domain.model.User;
import io.github.krosscaal.quarkussocial.domain.repository.FollowerRepository;
import io.github.krosscaal.quarkussocial.domain.repository.UserRepository;
import io.github.krosscaal.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {
    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void seTup(){
        /* Usario padrão dos testes*/
        var user = new User();
        user.setAge(35);
        user.setName("Usuário");
        userRepository.persist(user);
        userId = user.getId();

        /* usuario seguidor */
        var follower = new User();
        follower.setName("seguidor");
        follower.setAge(30);
        userRepository.persist(follower);
        followerId = follower.getId();

        /* cria um follower*/
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest(){
        var userHimself = new FollowerRequest();
        userHimself.setFollowerId(userId);
        given()
                .contentType(ContentType.JSON)
                .body(userHimself)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(409)
                .body(Matchers.is("You can´t follow yourself"));

    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doesn´t exist ")
    public void userNotFoundWhenTryingToFollowTest(){
        var anyUser = new FollowerRequest();
        anyUser.setFollowerId(userId);
        given()
                .contentType(ContentType.JSON)
                .body(anyUser)
                .pathParam("userId", 999)
        .when()
                .put()
        .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 204 when follow a user")
    public void followUserTest(){
        var anyUser = new FollowerRequest();
        anyUser.setFollowerId(followerId);
        given()
                .contentType(ContentType.JSON)
                .body(anyUser)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("should return 404 on list user followers a User id doesn´t exist ")
    public void userNotFoundWhenListFollowersTest(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 999)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }
    @Test
    @DisplayName("should return list a userś followers ")
    public void listFollowersTest(){
        var response =
                given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .extract().response();
        var followersCount =  response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1,followersCount);
        assertEquals(1, followersContent.size());
    }
    @Test
    @DisplayName("should return 404 on  unfollow  user and User id doesn´t exist ")
    public void userNotFoundWhenUnfollowingAUserTest(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 999)
                .queryParam("followerId", followerId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }
    @Test
    @DisplayName("should unfollow and User ")
    public void unfollowUserTest(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }

}