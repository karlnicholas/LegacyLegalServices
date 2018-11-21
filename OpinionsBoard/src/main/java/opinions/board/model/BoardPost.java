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

	private LocalDateTime dateTime;

	@OneToMany(mappedBy="boardPost")
	private List<BoardComment> boardComments;
}
