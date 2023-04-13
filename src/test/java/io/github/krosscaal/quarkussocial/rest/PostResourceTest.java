package io.github.krosscaal.quarkussocial.rest;

import io.github.krosscaal.quarkussocial.domain.model.Follower;
import io.github.krosscaal.quarkussocial.domain.model.Post;
import io.github.krosscaal.quarkussocial.domain.model.User;
import io.github.krosscaal.quarkussocial.domain.repository.FollowerRepository;
import io.github.krosscaal.quarkussocial.domain.repository.PostRepository;
import io.github.krosscaal.quarkussocial.domain.repository.UserRepository;
import io.github.krosscaal.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {


    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;
    @BeforeEach
    @Transactional
    public void setUp(){
        /* usuário padrão */
        var user = new User();
        user.setName("Mister Quarkus");
        user.setAge(35);
        userRepository.persist(user);
        userId = user.getId();

        Post post = new Post();
        post.setUser(user);
        post.setText("Hello");
        postRepository.persist(post);

        /* usuário não é follower*/
        var userNotFollower = new User();
        userNotFollower.setAge(40);
        userNotFollower.setName("Não sou de seguir as pessoas");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        /* usuário follower */
        var userFollower = new User();
        userFollower.setName("sou follower");
        userFollower.setAge(35);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        var entity = new Follower();
        entity.setUser(user);
        entity.setFollower(userFollower);
        followerRepository.persist(entity);
    }
    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some post");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
        .when()
                .post()
        .then().statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some post");
        Long inexistentUserID = 999L;
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserID)
        .when()
                .post()
        .then().statusCode(404);
    }

    @Test
    @DisplayName("should retunr 404 when user doesn´t exist")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 999;
        given()
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should retunr 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given()
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("\"message\":\"You forgot the header followerId\""));

    }
    @Test
    @DisplayName("should retunr 400 when follower doesn´t exist")
    public void listPostFollowerNotFoundTest(){
        var inexistendFollowerId = 9999;
        given()
                .pathParam("userId", userId)
                .header("followerId", inexistendFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("\"message\":\"Inexistent FollowerId\""));


    }
    @Test
    @DisplayName("should retunr 403 when follower isn´t a follower of user")
    public void listPostNotAFollowerTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("\"message\":\"you can´t see these posts\""));


    }
    @Test
    @DisplayName("should retunr posts")
    public void listPostTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when().get()
                .then()
                .statusCode(200).body("size()", Matchers.anything());
    }
}