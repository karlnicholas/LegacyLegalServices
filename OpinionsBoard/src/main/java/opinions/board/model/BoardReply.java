package opinions.board.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

@Entity
public class BoardReply {
	private static final int maxTextLength = 10000;
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@OrderBy	// probably redundant since I do order by in queries
	private LocalDateTime date;
	@ManyToOne
	private BoardComment boardComment;
	private String replyText;

	public Long getId() {
		return id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public BoardComment getBoardComment() {
		return boardComment;
	}
	public void setBoardComment(BoardComment boardComment) {
		this.boardComment = boardComment;
	}
	public String getReplyText() {
		return replyText;
	}
	public void setReplyText(String replyText) {
		if ( replyText != null && replyText.length() > maxTextLength ) {
			this.replyText = replyText.substring(0, maxTextLength);
		} else {
			this.replyText = replyText;
		}
	}
}
