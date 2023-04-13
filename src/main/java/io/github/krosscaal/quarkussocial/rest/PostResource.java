package io.github.krosscaal.quarkussocial.rest;

import io.github.krosscaal.quarkussocial.domain.model.Follower;
import io.github.krosscaal.quarkussocial.domain.model.Post;
import io.github.krosscaal.quarkussocial.domain.model.User;
import io.github.krosscaal.quarkussocial.domain.repository.FollowerRepository;
import io.github.krosscaal.quarkussocial.domain.repository.PostRepository;
import io.github.krosscaal.quarkussocial.domain.repository.UserRepository;
import io.github.krosscaal.quarkussocial.rest.dto.CreatePostRequest;
import io.github.krosscaal.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository repository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    public PostResource(
            UserRepository repository,
            PostRepository postRepository,
            FollowerRepository followerRepository){
        this.repository = repository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postRequest){
        User user = repository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post newPost = new Post();
        newPost.setText(postRequest.getText());
        newPost.setUser(user);
        postRepository.persist(newPost);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId){

        User user = repository.findById(userId);
        if(user == null){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        if(followerId == null){
            String message = "\"message\":\"You forgot the header followerId\"";
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(message)
                    .build();
        }

        User follower = repository.findById(followerId);
        if(follower == null){
            String message = "\"message\":\"Inexistent FollowerId\"";
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(message)
                    .build();
        }

        boolean follows = followerRepository.follows(follower, user);
        if(!follows){
            String message = "\"message\":\"you can´t see these posts\"";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(message)
                    .build();
        }


        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);

        var list = query.list();
        List<PostResponse> postResponseList = list.stream()
                /* uma forma de map */
//                .map(post -> PostResponse.fromEntity(post))

                /* outra forma passando a referência do método que va usar, já que mapeamos post e o método estático recebe post, */
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());
        return Response
                .status(Response.Status.OK)
                .entity(postResponseList)
                .build();
    }
}
