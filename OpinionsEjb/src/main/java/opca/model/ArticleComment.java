package opca.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;

@SuppressWarnings("serial")
@Entity
@NamedQueries({
})
public class ArticleComment implements Serializable {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	private Article article;
	
	@ManyToOne
	private User user;
	
	private int importance;
	
	private String comment;

	public ArticleComment() {}
	public ArticleComment(Article parentArticle) {
		this.article = parentArticle;
	}
	public Long getId() {
		return id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}
}
