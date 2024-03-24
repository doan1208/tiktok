package com.dle.controller;

import com.dle.Repository.CommentRepository;
import com.dle.bean.database.Comment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

@Path("/common")
public class CommonController {

    @Inject
    CommentRepository commentRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @POST
    @Path("/comment/log")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response logComment(Comment content) throws JsonProcessingException {
        content.setCreateDate(new Date());
        commentRepository.persist(content);
//        System.out.println(content.getId() + " - " + content.getCommentator() + " - " + content.getContent());
        System.out.println(objectMapper.writeValueAsString(content));
        return Response.ok(content).build();
    }

    @GET
    @Path("/comment/log")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComment() throws IOException {

        List<Comment> comments = commentRepository.find("type = ?1", "CHAT_MESSAGE").list();
        Map<String, List<String>> commentByUsername = new HashMap<>();
        Map<String , List<String> > comment_users = new HashMap<>();

        Map<String, Integer> count = new TreeMap<String, Integer>();
        for (int j = 0; j < comments.size(); j++) {
            addElement(count, comments.get(j), comment_users);
            if (!commentByUsername.containsKey(comments.get(j).getCommentator())) {
                commentByUsername.put(comments.get(j).getCommentator(), new ArrayList<>());
            }
            commentByUsername.get(comments.get(j).getCommentator()).add(comments.get(j).getContent());}

        StringBuilder builder = new StringBuilder();
        builder.append("Ten,").append("So comment,").append("Comment").append("\n");
        commentByUsername.forEach((k,v) -> {
            builder.append("\"").append(k).append("\"").append(",").append("\"").append(v.size()).append("\"").append(",").append("\"").append(String.join("|", v)).append("\"").append("\n");
        });
        //Files.write(Path.of(new URI("/Users/dle/IdeaProjects/tiktok/data.csv")), builder.toString().getBytes(Charset.forName("UTF-8")));

        java.io.File file = new java.io.File("/Users/dle/IdeaProjects/tiktok/" + "user_"+commentByUsername.size()+"_"+"comments_"+comments.size()+"_"+"trung_" + (comments.size()-count.size()) + ".csv");
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        writer.write(builder.toString());
        writer.close();

        StringBuilder builder1 = new StringBuilder();
        builder1.append("Noi dung,").append("So lan xuat hien,").append("Ten/so lan comment").append("\n");
        comment_users.forEach((k, v) -> {
            Map<String, Integer> user_solancommentcung1cau = new HashMap<>();
            for (String user : v) {
                addElement(user_solancommentcung1cau, user);
            }
            List<String> userJoin = new ArrayList<>();
            user_solancommentcung1cau.forEach((x, y) -> {
                userJoin.add(x+"("+y+")");
            });
            builder1.append("\"").append(k).append("\"").append(",").append("\"").append(v.size()).append("\"").append(",").append("\"").append(String.join("|", userJoin)).append("\"").append("\n");
        });
        java.io.File file1 = new java.io.File("/Users/dle/IdeaProjects/tiktok/" + "comment_"+comment_users.size()+ ".csv");
        Writer writer1 = new OutputStreamWriter(new FileOutputStream(file1), "UTF-8");
        writer1.write(builder1.toString());
        writer1.close();

        return Response.ok(comments).build();
    }

    @GET
    @Path("/comment/log/bytype")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllComment(@QueryParam("type") @DefaultValue("CHAT_MESSAGE") String type) throws IOException {
        List<Comment> comments = commentRepository.find("type = ?1", type).list();
        return Response.ok(comments).build();
    }

    public static void addElement(Map<String, Integer> map, Comment element, Map<String, List<String>> cus) {
        if (map.containsKey(element.getContent())) {
            int count = map.get(element.getContent()) + 1;
            cus.get(element.getContent()).add(element.getCommentator());
            map.put(element.getContent(), count);
        } else {
            map.put(element.getContent(), 1);
            List<String> users = new ArrayList<>();
            users.add(element.getCommentator());
            cus.put(element.getContent(), users);
        }
    }

    public static void addElement(Map<String, Integer> map, String element) {
        if (map.containsKey(element)) {
            int count = map.get(element) + 1;
            map.put(element, count);
        } else {
            map.put(element, 1);
        }
    }

}
