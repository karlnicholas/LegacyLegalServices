package opca.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@NamedQueries({
    @NamedQuery(name = Article.GET_ARTICLES_AND_COMMENTS, query = "select distinct(a) from Article a left join fetch a.articleComments order by a.id desc"),  
})

public class Article implements Serializable {
	public static final String GET_ARTICLES_AND_COMMENTS = "Article.GET_ARTICLES_AND_COMMENTS";
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
	
    @Temporal(TemporalType.TIMESTAMP)
	private Date date;
	private String title;
	private String contents;
	
	@OneToMany(mappedBy="article")
	private List<ArticleComment> articleComments;
	
	public Long getId() {
		return id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public List<ArticleComment> getArticleComments() {
		return articleComments;
	}
	public void setArticleComments(List<ArticleComment> articleComments) {
		this.articleComments = articleComments;
	}
	@Override
	public String toString() {
		return date.toString() + ":" + title + ":" + contents;
	}
}
