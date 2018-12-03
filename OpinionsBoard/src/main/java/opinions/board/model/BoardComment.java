package opinions.board.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class BoardComment {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@OrderBy	// probably redundant since I do order by in queries
	private LocalDateTime date;
	@ManyToOne
	private BoardPost boardPost;
	@OneToMany(mappedBy="boardComment")
	private List<BoardReply> boardReplies;
	
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
	public List<BoardReply> getBoardReplies() {
		return boardReplies;
	}
	public void setBoardReplies(List<BoardReply> boardReplies) {
		this.boardReplies = boardReplies;
	}
}
