package io.github.krosscaal.quarkussocial.rest;

import io.github.krosscaal.quarkussocial.domain.model.User;
import io.github.krosscaal.quarkussocial.domain.repository.UserRepository;
import io.github.krosscaal.quarkussocial.rest.dto.CreateUserRequest;
import io.github.krosscaal.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jdk.net.SocketFlow;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;

    private Validator validator;

    /* forma do curso*/
    @Inject
    public UserResource(UserRepository repository, Validator validator){
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()){
            /*
            ConstraintViolation<CreateUserRequest> erro = violations.stream().findAny().get();
            String errorMessage = erro.getMessage();
            */
            ResponseError responseError = ResponseError.createFromValidation(violations);

            /* return 400 padrão*/
            //return Response.status(Response.Status.BAD_REQUEST).entity(responseError).build();

            /* return 422 específico rest*/
            return responseError.withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);
        /* retorno padrão*/
        //return Response.ok(user).build();

        /* retorno mais especifico status 201=CREATED*/
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAllUsers(){

        PanacheQuery<User> query = repository.findAll();
        /* retorno padrão*/
        //return Response.ok().build();

        /* retorno mais especifico*/
        return Response.status(Response.Status.OK).entity(query.list()).build();
    }

    @GET
    @Path("/user/{id}")
    public Response findUser(@PathParam("id") Long id){
        User findUser = repository.findById(id);
        if(findUser != null){
            return Response.status(Response.Status.OK).entity(findUser).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /* método do curso*/
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
        User userUpdate = repository.findById(id);
        if(userUpdate != null){
            userUpdate.setName(userData.getName());
            userUpdate.setAge(userData.getAge());
            //repository.persist(userUpdate);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    /* meu método*/
    @PUT
    @Path("/update/{id}")
    @Transactional
    public Response updateUserNovo(@PathParam("id") Long id, CreateUserRequest userData){
        User userUpdate = repository.findById(id);
        if(userUpdate != null){
            userUpdate.setName(userData.getName());
            userUpdate.setAge(userData.getAge());
            //repository.persist(userUpdate);
            return Response.status(Response.Status.NO_CONTENT).entity(userUpdate).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /* método do curso*/
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = repository.findById(id);
        if(user != null){
            repository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /* meu método*/
    @DELETE
    @Path("/delete/{id}")
    @Transactional
    public Response deleteUserNovo(@PathParam("id") Long id){
        User userDelete = repository.findById(id);
        if(userDelete != null){
            repository.delete(userDelete);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
