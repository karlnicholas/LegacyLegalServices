package opinions.board.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class BoardPost {
	private static final int maxTextLength = 10000;
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@OrderBy	// probably redundant since I do order by in queries
	private LocalDateTime date;
	private String postText;

	public Long getId() {
		return id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getPostText() {
		return postText;
	}
	public void setPostText(String postText) {
		if ( postText != null && postText.length() > maxTextLength ) {
			this.postText = postText.substring(0, maxTextLength);
		} else {
			this.postText = postText;
		}
	}
}
