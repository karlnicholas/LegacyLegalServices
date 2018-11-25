package opinions.board.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class BoardReply {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;

	@ManyToOne
	private BoardComment boardComment;
}
