package opinions.board.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

@Entity
public class BoardComment {
	private static final int maxTextLength = 10000;
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@OrderBy	// probably redundant since I do order by in queries
	private LocalDateTime date;
	@ManyToOne
	private BoardPost boardPost;
	@Column(length=maxTextLength)
	private String commentText;
	
	public Long getId() {
		return id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public BoardPost getBoardPost() {
		return boardPost;
	}
	public void setBoardPost(BoardPost boardPost) {
		this.boardPost = boardPost;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		if ( commentText != null && commentText.length() > maxTextLength ) {
			this.commentText = commentText.substring(0, maxTextLength);
		} else {
			this.commentText = commentText;
		}
	}
}
