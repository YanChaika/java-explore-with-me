package org.example.ewm.event;

import org.example.ewm.event.dto.CommentDto;
import org.example.ewm.event.dto.CommentFullDto;
import org.example.ewm.event.model.Comment;
import org.example.ewm.event.model.Event;
import org.example.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment fromCommentDto(CommentDto commentDto, Event event, User user, LocalDateTime time) {
        return new Comment(
                null,
                commentDto.getText(),
                event,
                user,
                time
        );
    }

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return new CommentFullDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated().toString()
        );
    }

    public static List<CommentFullDto> toCommentsFullDto(List<Comment> comments) {
        List<CommentFullDto> commentsFullDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsFullDto.add(toCommentFullDto(comment));
        }
        return commentsFullDto;
    }
}
