package opinions.board.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class BoardPost {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;

	@OneToMany(mappedBy="boardPost")
	private List<BoardComment> boardComments;

	public Long getId() {
		return id;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public List<BoardComment> getBoardComments() {
		return boardComments;
	}

	public void setBoardComments(List<BoardComment> boardComments) {
		this.boardComments = boardComments;
	}
}
